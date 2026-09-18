package com.setupbuilder.service;

import com.setupbuilder.dto.ChatMessageResponse;
import com.setupbuilder.dto.ChatSessionResponse;
import com.setupbuilder.entity.ChatMessage;
import com.setupbuilder.entity.ChatSession;
import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.User;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.ChatMessageRepository;
import com.setupbuilder.repository.ChatSessionRepository;
import com.setupbuilder.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ComponentRepository componentRepository;
    private final GeminiChatService geminiChatService;

    private static final int MAX_HISTORY_MESSAGES = 20;     // keep last N messages for context
    private static final int MAX_MESSAGES_PER_SESSION = 200; // hard cap
    private static final int MAX_TITLE_LENGTH = 60;

    @Transactional
    public ChatSessionResponse createSession(User user) {
        ChatSession session = ChatSession.builder()
                .user(user)
                .title("New chat")
                .build();
        return ChatSessionResponse.from(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> listSessions(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(user.getId(), pageable)
                .map(ChatSessionResponse::from);
    }

    @Transactional
    public void deleteSession(Long id, User user) {
        ChatSession session = sessionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        sessionRepository.delete(session);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> listMessages(Long sessionId, User user) {
        sessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long sessionId, String content, User user) {
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        long count = messageRepository.countBySessionId(sessionId);
        if (count >= MAX_MESSAGES_PER_SESSION) {
            throw new IllegalArgumentException(
                    "This conversation has reached its limit. Start a new chat.");
        }

        // 1. Save user message
        ChatMessage userMsg = ChatMessage.builder()
                .session(session)
                .role("user")
                .content(content.trim())
                .build();
        messageRepository.save(userMsg);

        // 2. Update session title on first user message
        if (session.getTitle() == null || session.getTitle().equals("New chat")) {
            session.setTitle(deriveTitle(content));
        }
        sessionRepository.save(session);

        // 3. Load recent history (oldest → newest)
        List<ChatMessage> all = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        List<ChatMessage> history = all.size() <= MAX_HISTORY_MESSAGES
                ? all
                : all.subList(all.size() - MAX_HISTORY_MESSAGES, all.size());

        // 4. Build system prompt with catalog
        String systemPrompt = buildSystemPrompt();

        // 5. Call Gemini
        String reply = geminiChatService.chat(systemPrompt, history);
        if (reply == null || reply.isBlank()) {
            reply = "I'm having trouble reaching the AI service right now. Please try again in a moment.";
        }

        // 6. Save and return model message
        ChatMessage assistantMsg = ChatMessage.builder()
                .session(session)
                .role("model")
                .content(reply)
                .build();
        messageRepository.save(assistantMsg);

        return ChatMessageResponse.from(assistantMsg);
    }

    // ─── helpers ───

    private String deriveTitle(String firstMessage) {
        String t = firstMessage.trim().replaceAll("\\s+", " ");
        return t.length() <= MAX_TITLE_LENGTH ? t : t.substring(0, MAX_TITLE_LENGTH) + "…";
    }

    private String buildSystemPrompt() {
        List<Component> components = componentRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("You are the Setup Builder AI — a friendly, expert PC building assistant ")
                .append("for the Saudi Arabian market.\n\n");

        sb.append("YOUR ROLE:\n");
        sb.append("- Help users plan PC builds based on budget, use case, and preferences\n");
        sb.append("- Explain component compatibility (CPU sockets, RAM type, PSU wattage)\n");
        sb.append("- Compare components and explain tradeoffs\n");
        sb.append("- Suggest upgrades or downgrades to hit a budget target\n");
        sb.append("- Answer general PC building questions\n\n");

        sb.append("STYLE:\n");
        sb.append("- Friendly but concise\n");
        sb.append("- Use bullet points for lists of components\n");
        sb.append("- ALWAYS quote prices in SAR (Saudi Riyals)\n");
        sb.append("- Reference specific model names from the catalog below\n");
        sb.append("- When recommending a build, always include total estimated cost in SAR\n");
        sb.append("- If you don't know something, say so honestly\n\n");

        sb.append("CATALOG (use these exact components when recommending):\n");
        sb.append("Format: category | name | price SAR | key specs\n");
        for (Component c : components) {
            sb.append("- ").append(c.getCategory().name()).append(" | ")
                    .append(c.getName()).append(" | ")
                    .append(c.getFallbackPrice() != null ? c.getFallbackPrice().toPlainString() : "?")
                    .append(" SAR");
            Map<String, String> specs = c.getSpecs();
            if (specs != null && !specs.isEmpty()) {
                sb.append(" | ");
                int i = 0;
                for (Map.Entry<String, String> e : specs.entrySet()) {
                    if (i++ > 0) sb.append(", ");
                    sb.append(e.getKey()).append("=").append(e.getValue());
                    if (i >= 4) break;
                }
            }
            sb.append("\n");
        }

        sb.append("\nWhen the user asks for a build recommendation, provide 6-7 components ")
                .append("(CPU, Motherboard, RAM, GPU, Storage, PSU, Case) with the total in SAR. ")
                .append("If they don't specify a budget, ask.\n");

        sb.append("\nIf the user asks you to look something up that isn't in the catalog, ")
                .append("suggest they browse the components page on the site.\n");

        return sb.toString();
    }
}