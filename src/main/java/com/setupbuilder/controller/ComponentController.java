package com.setupbuilder.controller;

import com.setupbuilder.dto.ComponentResponse;
import com.setupbuilder.entity.enums.ComponentCategory;
import com.setupbuilder.service.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/components")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentService componentService;

    /**
     * Public list endpoint.
     * Example: GET /api/components?category=CPU&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<ComponentResponse>> list(
            @RequestParam(required = false) ComponentCategory category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(componentService.list(category, brand, search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(componentService.getById(id));
    }
}