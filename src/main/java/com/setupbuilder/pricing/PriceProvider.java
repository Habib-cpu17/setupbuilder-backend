package com.setupbuilder.pricing;

import com.setupbuilder.entity.Component;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceProvider {

    /** Human-readable name, e.g. "mock", "apify". */
    String name();

    /**
     * Returns the current price for the component, or empty if unavailable.
     * Throw nothing — return Optional.empty() on failure.
     */
    Optional<BigDecimal> fetchPrice(Component component);
}