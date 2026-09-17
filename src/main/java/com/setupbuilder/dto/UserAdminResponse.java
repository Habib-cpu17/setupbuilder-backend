package com.setupbuilder.dto;

import com.setupbuilder.entity.User;

public record UserAdminResponse(
        Long id,
        String email,
        String displayName,
        String role,
        long buildCount
) {
    public static UserAdminResponse from(User u, long buildCount) {
        return new UserAdminResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getRole().name(),
                buildCount
        );
    }
}