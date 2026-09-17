package com.setupbuilder.controller;

import com.setupbuilder.dto.RoleUpdateRequest;
import com.setupbuilder.dto.UserAdminResponse;
import com.setupbuilder.entity.User;
import com.setupbuilder.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping
    public ResponseEntity<Page<UserAdminResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return ResponseEntity.ok(service.list(page, size));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserAdminResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest req,
            Authentication auth
    ) {
        User me = (User) auth.getPrincipal();
        return ResponseEntity.ok(service.updateRole(id, req, me));
    }
}