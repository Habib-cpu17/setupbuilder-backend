package com.setupbuilder.pricing;

import com.setupbuilder.entity.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ApifyPriceProvider implements PriceProvider {

    private final String token;
    private final String actorId;

    @Override
    public String name() {
        return "apify";
    }

    @Override
    public Optional<BigDecimal> fetchPrice(Component component) {
        if (token == null || token.isBlank() || actorId == null || actorId.isBlank()) {
            log.warn("Apify provider not configured — skipping {}", component.getName());
            return Optional.empty();
        }

        // TODO: real Apify call here
        return Optional.empty();
    }
}