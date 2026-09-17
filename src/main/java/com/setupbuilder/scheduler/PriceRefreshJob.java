package com.setupbuilder.scheduler;

import com.setupbuilder.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "pricing.refresh.enabled", havingValue = "true", matchIfMissing = true)
public class PriceRefreshJob {

    private final PriceService priceService;

    @Scheduled(cron = "${pricing.refresh.cron}")
    public void refresh() {
        log.info("Running scheduled price refresh…");
        try {
            int changed = priceService.refreshAll();
            log.info("Scheduled price refresh done. {} prices updated.", changed);
        } catch (Exception e) {
            log.error("Scheduled price refresh failed", e);
        }
    }
}