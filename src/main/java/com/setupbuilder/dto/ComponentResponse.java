package com.setupbuilder.dto;

import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.enums.ComponentCategory;

import java.math.BigDecimal;
import java.util.Map;

public record ComponentResponse(
        Long id,
        String name,
        ComponentCategory category,
        String brand,
        String model,
        String imageUrl,
        Map<String, String> specs,
        BigDecimal price
) {
    public static ComponentResponse from(Component c) {
        return new ComponentResponse(
                c.getId(),
                c.getName(),
                c.getCategory(),
                c.getBrand(),
                c.getModel(),
                c.getImageUrl(),
                c.getSpecs(),
                c.getFallbackPrice()  // for now, fallback = price
        );
    }
}