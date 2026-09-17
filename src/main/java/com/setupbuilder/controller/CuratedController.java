package com.setupbuilder.controller;

import com.setupbuilder.dto.BuildResponse;
import com.setupbuilder.service.CuratedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CuratedController {

    private final CuratedService curatedService;

    /** Public — landing page pulls from this. */
    @GetMapping("/api/curated")
    public ResponseEntity<List<BuildResponse>> list() {
        return ResponseEntity.ok(curatedService.listCurated());
    }

    /** Admin actions. */
    @PostMapping("/api/admin/curated/{buildId}")
    public ResponseEntity<Void> add(@PathVariable Long buildId) {
        curatedService.add(buildId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/curated/{buildId}")
    public ResponseEntity<Void> remove(@PathVariable Long buildId) {
        curatedService.remove(buildId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/curated/{buildId}/rank")
    public ResponseEntity<Void> setRank(
            @PathVariable Long buildId,
            @RequestBody Map<String, Integer> body
    ) {
        curatedService.setRank(buildId, body.get("rank"));
        return ResponseEntity.noContent().build();
    }
}