package com.setupbuilder.dto;

import com.setupbuilder.entity.Build;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BuildResponse(
        Long id,
        String name,
        String description,
        Boolean isPublic,
        BigDecimal totalPrice,
        Long userId,
        String userDisplayName,
        List<BuildComponentResponse> components,
        List<String> compatibilityWarnings,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BuildResponse from(Build b, List<String> warnings) {
        return new BuildResponse(
                b.getId(),
                b.getName(),
                b.getDescription(),
                b.getIsPublic(),
                b.getTotalPrice(),
                b.getUser().getId(),
                b.getUser().getDisplayName(),
                b.getComponents().stream().map(BuildComponentResponse::from).toList(),
                warnings,
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}