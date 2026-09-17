package com.setupbuilder.dto;

import java.math.BigDecimal;

/**
 * The exact shape of the JSON the AI is told to return.
 * Kept separate from AiPriceEstimateResponse so Jackson doesn't choke on missing fields.
 */
public record GeminiRawEstimate(
        BigDecimal estimatedTotal,
        String marketCondition,
        String explanation
) {}