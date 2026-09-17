package com.setupbuilder.dto;

import com.setupbuilder.entity.enums.ComponentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;

public record ComponentAdminRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull ComponentCategory category,
        @Size(max = 100) String brand,
        @Size(max = 100) String model,
        @Size(max = 500) String imageUrl,
        Map<String, String> specs,
        BigDecimal fallbackPrice,
        Boolean active
) {}