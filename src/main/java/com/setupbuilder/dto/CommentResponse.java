package com.setupbuilder.dto;

import com.setupbuilder.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long userId,
        String userDisplayName,
        String userAvatarUrl,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getContent(),
                c.getUser().getId(),
                c.getUser().getDisplayName() != null
                        ? c.getUser().getDisplayName()
                        : c.getUser().getEmail(),
                c.getUser().getAvatarUrl(),
                c.getCreatedAt()
        );
    }
}