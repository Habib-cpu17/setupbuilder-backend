package com.setupbuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setupbuilder.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    @Value("${gemini.api.models}")
    private String modelsCsv;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(60);
    private static final int MAX_RETRIES = 2;

    /**
     * Send a multi-turn chat to Gemini and return the assistant's reply text.
     * Returns null if all models fail.
     */
    public String chat(String systemInstruction, List<ChatMessage> history) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key is not set");
            return null;
        }

        List<String> models = Arrays.stream(modelsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        for (String model : models) {
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try {
                    String reply = callModel(model, systemInstruction, history);
                    if (reply != null) return reply;
                    break; // non-retryable
                } catch (RetryableException e) {
                    log.warn("Chat model {} attempt {}: {}", model, attempt, e.getMessage());
                    sleep(500L * attempt);
                } catch (Exception e) {
                    log.warn("Chat model {} error: {}", model, e.getMessage());
                    break;
                }
            }
            log.warn("Chat giving up on model {}, trying next…", model);
        }

        log.error("All chat models failed");
        return null;
    }

    private String callModel(String model, String systemInstruction, List<ChatMessage> history)
            throws Exception {

        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatMessage m : history) {
            contents.add(Map.of(
                    "role", m.getRole(),                 // "user" or "model"
                    "parts", List.of(Map.of("text", m.getContent()))
            ));
        }

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "contents", contents,
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 900
                )
        );

        String requestJson = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + model + ":generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .timeout(HTTP_TIMEOUT)
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status == 429 || status == 500 || status == 502 || status == 503 || status == 504) {
            throw new RetryableException("HTTP " + status);
        }
        if (status != 200) {
            log.error("Gemini chat returned {}: {}", status, truncate(response.body(), 400));
            return null;
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode textNode = root.path("candidates")
                .path(0).path("content").path("parts").path(0).path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            log.warn("Gemini chat no text. Raw: {}", truncate(response.body(), 400));
            return null;
        }

        return textNode.asText().trim();
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    private static class RetryableException extends RuntimeException {
        RetryableException(String msg) { super(msg); }
    }
}