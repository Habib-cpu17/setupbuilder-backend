package com.setupbuilder.controller;

import com.setupbuilder.dto.ChatMessageRequest;
import com.setupbuilder.dto.ChatMessageResponse;
import com.setupbuilder.dto.ChatSessionResponse;
import com.setupbuilder.entity.User;
import com.setupbuilder.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponse> create(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(chatService.createSession(user));
    }

    @GetMapping("/sessions")
    public ResponseEntity<Page<ChatSessionResponse>> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(chatService.listSessions(user, page, size));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        chatService.deleteSession(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> messages(
            @PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(chatService.listMessages(id, user));
    }

    @PostMapping("/sessions/{id}/messages")
    public ResponseEntity<ChatMessageResponse> send(
            @PathVariable Long id,
            @Valid @RequestBody ChatMessageRequest req,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        ChatMessageResponse reply = chatService.sendMessage(id, req.content(), user);
        return ResponseEntity.ok(reply);
    }
}