package com.setupbuilder.controller;

import com.setupbuilder.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Map<String, Object> body = new HashMap<>();
        body.put("id", user.getId());
        body.put("email", user.getEmail());
        body.put("displayName", user.getDisplayName());
        body.put("role", user.getRole().name());
        body.put("firebaseUid", user.getFirebaseUid());

        return ResponseEntity.ok(body);
    }
}