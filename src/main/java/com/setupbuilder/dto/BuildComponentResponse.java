package com.setupbuilder.dto;

import com.setupbuilder.entity.BuildComponent;

import java.math.BigDecimal;

public record BuildComponentResponse(
        Long id,
        ComponentResponse component,
        Integer quantity,
        BigDecimal priceAtTimeOfBuild,
        BigDecimal lineTotal
) {
    public static BuildComponentResponse from(BuildComponent bc) {
        BigDecimal unit = bc.getPriceAtTimeOfBuild() != null
                ? bc.getPriceAtTimeOfBuild()
                : (bc.getComponent().getFallbackPrice() != null
                ? bc.getComponent().getFallbackPrice()
                : BigDecimal.ZERO);
        BigDecimal line = unit.multiply(BigDecimal.valueOf(bc.getQuantity()));
        return new BuildComponentResponse(
                bc.getId(),
                ComponentResponse.from(bc.getComponent()),
                bc.getQuantity(),
                unit,
                line
        );
    }
}