package com.setupbuilder.controller;

import com.setupbuilder.dto.ComponentAdminRequest;
import com.setupbuilder.dto.ComponentResponse;
import com.setupbuilder.service.AdminComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/components")
@RequiredArgsConstructor
public class AdminComponentController {

    private final AdminComponentService service;

    @GetMapping
    public ResponseEntity<Page<ComponentResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return ResponseEntity.ok(service.listAll(page, size));
    }

    @PostMapping
    public ResponseEntity<ComponentResponse> create(@Valid @RequestBody ComponentAdminRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComponentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ComponentAdminRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ComponentResponse> setActive(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean active = Boolean.TRUE.equals(body.get("active"));
        return ResponseEntity.ok(service.setActive(id, active));
    }
}