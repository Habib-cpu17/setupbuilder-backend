package com.setupbuilder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BuildComponentRequest(
        @NotNull Long componentId,
        @Min(1) Integer quantity
) {
    public int qty() {
        return quantity == null ? 1 : quantity;
    }
}