package com.setupbuilder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BuildRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        Boolean isPublic,
        @NotEmpty @Valid List<BuildComponentRequest> components
) {}