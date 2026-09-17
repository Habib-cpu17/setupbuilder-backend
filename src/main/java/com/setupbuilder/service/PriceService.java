package com.setupbuilder.service;

import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.PriceHistory;
import com.setupbuilder.pricing.PriceProvider;
import com.setupbuilder.repository.ComponentRepository;
import com.setupbuilder.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private final ComponentRepository componentRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PriceProvider priceProvider;

    /** Refresh a single component's price from the provider. Returns true if changed. */
    @Transactional
    public boolean refreshOne(Component component) {
        Optional<BigDecimal> fetched;
        try {
            fetched = priceProvider.fetchPrice(component);
        } catch (Exception e) {
            log.warn("Provider {} failed for component {}: {}",
                    priceProvider.name(), component.getId(), e.getMessage());
            return false;
        }

        if (fetched.isEmpty()) return false;

        BigDecimal newPrice = fetched.get();
        BigDecimal oldPrice = component.getFallbackPrice();

        // Treat near-identical prices as "no change" to avoid history noise
        if (oldPrice != null && oldPrice.compareTo(newPrice) == 0) {
            return false;
        }

        component.setFallbackPrice(newPrice);
        componentRepository.save(component);

        priceHistoryRepository.save(PriceHistory.builder()
                .component(component)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .source(priceProvider.name())
                .build());

        return true;
    }

    /** Refresh all active components. Returns the count of changed prices. */
    @Transactional
    public int refreshAll() {
        int changed = 0;
        for (Component c : componentRepository.findAll()) {
            if (Boolean.TRUE.equals(c.getActive()) && refreshOne(c)) {
                changed++;
            }
        }
        log.info("[pricing:{}] refreshed — {} of {} prices changed",
                priceProvider.name(), changed, componentRepository.count());
        return changed;
    }

    public String activeProvider() {
        return priceProvider.name();
    }
}