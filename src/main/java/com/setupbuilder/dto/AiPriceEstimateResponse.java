package com.setupbuilder.dto;

import java.math.BigDecimal;

/**
 * Returned to the frontend. Contains both the AI estimate and a comparison
 * against the stored database total.
 */
public record AiPriceEstimateResponse(
        BigDecimal estimatedTotal,
        String marketCondition,   // "stable" | "rising" | "falling" | "unavailable"
        String explanation,
        BigDecimal databaseTotal,
        Integer percentDifference // signed: +12 means AI says 12% higher than DB
) {}