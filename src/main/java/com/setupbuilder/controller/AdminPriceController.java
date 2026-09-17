package com.setupbuilder.controller;

import com.setupbuilder.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/prices")
@RequiredArgsConstructor
public class AdminPriceController {

    private final PriceService priceService;

    /** Returns which provider is active. */
    @GetMapping("/provider")
    public ResponseEntity<Map<String, String>> provider() {
        return ResponseEntity.ok(Map.of("provider", priceService.activeProvider()));
    }

    /** Manually trigger a full refresh. */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh() {
        int changed = priceService.refreshAll();
        return ResponseEntity.ok(Map.of(
                "provider", priceService.activeProvider(),
                "changed", changed
        ));
    }
}