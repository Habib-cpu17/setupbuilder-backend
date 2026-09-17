package com.setupbuilder.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PublicProfileResponse(
        Long id,
        String displayName,
        String email,          // null when viewing someone else
        String role,
        String avatarUrl,
        String bannerUrl,
        String bio,
        String location,
        String websiteUrl,
        LocalDateTime createdAt,
        Stats stats,
        List<String> achievements,
        List<BuildResponse> builds
) {
    public record Stats(
            long totalBuilds,
            long publicBuilds,
            long totalComments,
            long commentsReceived
    ) {}
}