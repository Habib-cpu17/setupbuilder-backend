package com.setupbuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setupbuilder.dto.AiPriceEstimateResponse;
import com.setupbuilder.dto.BuildComponentResponse;
import com.setupbuilder.dto.BuildResponse;
import com.setupbuilder.dto.GeminiRawEstimate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiPriceService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    @Value("${gemini.api.models}")
    private String modelsCsv;

    @Value("${gemini.api.retries:3}")
    private int retries;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AiPriceEstimateResponse estimatePrice(BuildResponse build) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key is not set — returning unavailable");
            return unavailable(build.totalPrice());
        }

        List<String> models = Arrays.stream(modelsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        String prompt = buildPrompt(build);

        for (String model : models) {
            for (int attempt = 1; attempt <= retries; attempt++) {
                try {
                    AiPriceEstimateResponse result = tryModel(model, prompt, build);
                    if (result != null) {
                        if (attempt > 1 || !model.equals(models.get(0))) {
                            log.info("AI estimate succeeded with model={} on attempt {}", model, attempt);
                        }
                        return result;
                    }
                } catch (RetryableException e) {
                    long waitMs = 600L * attempt; // small backoff: 600ms, 1200ms, 1800ms
                    log.warn("Model {} attempt {}/{} retryable ({}). Waiting {}ms…",
                            model, attempt, retries, e.getMessage(), waitMs);
                    sleep(waitMs);
                } catch (Exception e) {
                    log.warn("Model {} attempt {}/{} failed: {}",
                            model, attempt, retries, e.getMessage());
                    break; // non-retryable for this model — go to next model
                }
            }
            log.warn("Giving up on model {}. Trying next in chain…", model);
        }

        log.error("All Gemini models failed for build {}", build.id());
        return unavailable(build.totalPrice());
    }

    /**
     * @return parsed response, or null if the model returned no usable text
     * @throws RetryableException when the API responds with 429 or 503
     */
    private AiPriceEstimateResponse tryModel(String model, String prompt, BuildResponse build)
            throws Exception {

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.2
                )
        );

        String requestJson = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + model + ":generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        // Retry on transient failures
        if (status == 429 || status == 503 || status == 500 || status == 502 || status == 504) {
            throw new RetryableException("HTTP " + status);
        }

        if (status != 200) {
            log.error("Gemini API returned {}: {}", status, response.body());
            return null;
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode textNode = root.path("candidates")
                .path(0).path("content").path("parts").path(0).path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            log.warn("Gemini returned no usable text. Raw: {}", response.body());
            return null;
        }

        String jsonText = textNode.asText()
                .replaceAll("^```json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("```\\s*$", "")
                .trim();

        GeminiRawEstimate raw = objectMapper.readValue(jsonText, GeminiRawEstimate.class);

        Integer pctDiff = null;
        BigDecimal dbTotal = build.totalPrice() == null ? BigDecimal.ZERO : build.totalPrice();
        if (raw.estimatedTotal() != null && dbTotal.compareTo(BigDecimal.ZERO) > 0) {
            pctDiff = raw.estimatedTotal()
                    .subtract(dbTotal)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(dbTotal, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        return new AiPriceEstimateResponse(
                raw.estimatedTotal(),
                raw.marketCondition() == null ? "stable" : raw.marketCondition(),
                raw.explanation() == null ? "" : raw.explanation(),
                dbTotal,
                pctDiff
        );
    }

    private AiPriceEstimateResponse unavailable(BigDecimal dbTotal) {
        return new AiPriceEstimateResponse(
                null,
                "unavailable",
                "The AI service is busy right now. Please try again in a few seconds.",
                dbTotal == null ? BigDecimal.ZERO : dbTotal,
                null
        );
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Marker for errors that are worth retrying. */
    private static class RetryableException extends RuntimeException {
        RetryableException(String msg) { super(msg); }
    }

    private String buildPrompt(BuildResponse build) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a PC parts pricing expert for the Saudi Arabian market.\n\n");
        sb.append("Estimate the CURRENT total market price in Saudi Riyals (SAR) for this exact PC build.\n");
        sb.append("Base your estimate on real prices from Saudi retailers such as Amazon.sa, Noon, Jarir, Extra, and Falcon Games.\n");
        sb.append("Account for typical Saudi market availability and 15% VAT.\n\n");
        sb.append("BUILD COMPONENTS:\n");
        for (BuildComponentResponse c : build.components()) {
            sb.append("- ").append(c.component().category().name()).append(": ")
                    .append(c.component().name());
            if (c.component().brand() != null) sb.append(" (").append(c.component().brand()).append(")");
            sb.append(" — stored price: ").append(c.component().price()).append(" SAR\n");
        }
        sb.append("\nSTORED TOTAL: ").append(build.totalPrice()).append(" SAR\n\n");
        sb.append("Respond ONLY with a JSON object matching this exact schema (no markdown, no extra text):\n");
        sb.append("{\n");
        sb.append("  \"estimatedTotal\": <number, the estimated current total price in SAR>,\n");
        sb.append("  \"marketCondition\": \"stable\" | \"rising\" | \"falling\",\n");
        sb.append("  \"explanation\": \"<one or two concise sentences about the estimate and market trend>\"\n");
        sb.append("}\n");
        return sb.toString();
    }
}