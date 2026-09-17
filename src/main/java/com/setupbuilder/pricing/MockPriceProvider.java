package com.setupbuilder.pricing;

import com.setupbuilder.entity.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Random;

@Slf4j
public class MockPriceProvider implements PriceProvider {

    private final Random random = new Random();

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public Optional<BigDecimal> fetchPrice(Component component) {
        if (component.getFallbackPrice() == null) return Optional.empty();

        double factor = 0.95 + random.nextDouble() * 0.10;
        BigDecimal newPrice = component.getFallbackPrice()
                .multiply(BigDecimal.valueOf(factor))
                .setScale(2, RoundingMode.HALF_UP);

        log.debug("[mock] {} → {} SAR", component.getName(), newPrice);
        return Optional.of(newPrice);
    }
}