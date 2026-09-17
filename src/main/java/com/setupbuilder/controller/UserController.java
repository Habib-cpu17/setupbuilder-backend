package com.setupbuilder.controller;

import com.setupbuilder.dto.PublicProfileResponse;
import com.setupbuilder.dto.UpdateProfileRequest;
import com.setupbuilder.entity.User;
import com.setupbuilder.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;

    /** Public profile view — anyone can see it. */
    @GetMapping("/{id}")
    public ResponseEntity<PublicProfileResponse> getProfile(
            @PathVariable Long id,
            Authentication auth
    ) {
        User requester = auth != null ? (User) auth.getPrincipal() : null;
        return ResponseEntity.ok(profileService.getProfile(id, requester));
    }

    /** Own profile view — full info. */
    @GetMapping("/me/profile")
    public ResponseEntity<PublicProfileResponse> getMyProfile(Authentication auth) {
        User me = (User) auth.getPrincipal();
        return ResponseEntity.ok(profileService.getProfile(me.getId(), me));
    }

    /** Update own profile. */
    @PutMapping("/me/profile")
    public ResponseEntity<PublicProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest req,
            Authentication auth
    ) {
        User me = (User) auth.getPrincipal();
        return ResponseEntity.ok(profileService.updateOwn(me, req));
    }
}