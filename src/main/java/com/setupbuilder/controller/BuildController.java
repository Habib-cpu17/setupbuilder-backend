package com.setupbuilder.controller;

import com.setupbuilder.dto.BuildRequest;
import com.setupbuilder.dto.BuildResponse;
import com.setupbuilder.entity.User;
import com.setupbuilder.service.BuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.setupbuilder.dto.AiPriceEstimateResponse;
import com.setupbuilder.service.AiPriceService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;
    private final AiPriceService aiPriceService;

    // --- public browse ---
    @GetMapping("/public")
    public ResponseEntity<Page<BuildResponse>> listPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(buildService.listPublicBuilds(page, size));
    }

    // --- authenticated ---
    @PostMapping
    public ResponseEntity<BuildResponse> create(@Valid @RequestBody BuildRequest req,
                                                Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(buildService.create(req, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildResponse> get(@PathVariable Long id, Authentication auth) {
        User user = auth != null ? (User) auth.getPrincipal() : null;
        return ResponseEntity.ok(buildService.getById(id, user));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<BuildResponse>> mine(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(buildService.listMyBuilds(user, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody BuildRequest req,
                                                Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(buildService.update(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        buildService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, List<String>>> check(@Valid @RequestBody BuildRequest req) {
        return ResponseEntity.ok(Map.of("warnings", buildService.checkCompatibility(req.components())));
    }

    // BuildController — append this method
    @GetMapping("/admin/all")
    public ResponseEntity<Page<BuildResponse>> allForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return ResponseEntity.ok(buildService.listAllForAdmin(page, size));
    }

    @PostMapping("/{id}/ai-estimate")
    public ResponseEntity<AiPriceEstimateResponse> estimatePriceWithAi(
            @PathVariable Long id,
            Authentication auth) {
        User user = auth != null ? (User) auth.getPrincipal() : null;
        BuildResponse build = buildService.getById(id, user);
        return ResponseEntity.ok(aiPriceService.estimatePrice(build));
    }

}