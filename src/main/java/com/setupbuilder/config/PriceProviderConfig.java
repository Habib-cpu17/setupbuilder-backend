package com.setupbuilder.config;

import com.setupbuilder.pricing.ApifyPriceProvider;
import com.setupbuilder.pricing.MockPriceProvider;
import com.setupbuilder.pricing.PriceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceProviderConfig {

    /**
     * Active when pricing.provider=mock (or the property is missing entirely).
     */
    @Bean
    @ConditionalOnProperty(
            name = "pricing.provider",
            havingValue = "mock",
            matchIfMissing = true
    )
    public PriceProvider mockPriceProvider() {
        return new MockPriceProvider();
    }

    /**
     * Active only when pricing.provider=apify.
     */
    @Bean
    @ConditionalOnProperty(
            name = "pricing.provider",
            havingValue = "apify"
    )
    public PriceProvider apifyPriceProvider(
            @Value("${pricing.apify.token:}") String token,
            @Value("${pricing.apify.actor-id:}") String actorId
    ) {
        return new ApifyPriceProvider(token, actorId);
    }
}