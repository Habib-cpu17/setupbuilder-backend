package com.setupbuilder.dto;

import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(@NotNull String role) {}