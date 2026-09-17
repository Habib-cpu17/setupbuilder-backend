package com.setupbuilder.controller;

import com.setupbuilder.dto.CommentRequest;
import com.setupbuilder.dto.CommentResponse;
import com.setupbuilder.entity.User;
import com.setupbuilder.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** Public: list comments on a build. */
    @GetMapping("/builds/{buildId}/comments")
    public ResponseEntity<Page<CommentResponse>> list(
            @PathVariable Long buildId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(commentService.listForBuild(buildId, page, size));
    }

    /** Authenticated: add a comment. */
    @PostMapping("/builds/{buildId}/comments")
    public ResponseEntity<CommentResponse> add(
            @PathVariable Long buildId,
            @Valid @RequestBody CommentRequest req,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(commentService.add(buildId, req, user));
    }

    /** Authenticated: delete own comment (or any, if admin). */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        commentService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}