package com.setupbuilder.service;

import com.setupbuilder.entity.User;
import com.setupbuilder.entity.enums.UserRole;
import com.setupbuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.admin.emails:}")
    private String adminEmailsRaw;

    @Transactional
    public User upsertFromFirebase(String firebaseUid, String email, String displayName) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .map(existing -> {
                    boolean dirty = false;

                    if (email != null && !email.equals(existing.getEmail())) {
                        existing.setEmail(email);
                        dirty = true;
                    }
                    if (displayName != null && !displayName.equals(existing.getDisplayName())) {
                        existing.setDisplayName(displayName);
                        dirty = true;
                    }

                    // Auto-promote if this email is in the configured admin list
                    if (email != null
                            && isConfiguredAdmin(email)
                            && existing.getRole() != UserRole.ADMIN) {
                        existing.setRole(UserRole.ADMIN);
                        dirty = true;
                        log.info("Promoted {} to ADMIN (matched app.admin.emails)", email);
                    }

                    return dirty ? userRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    UserRole role = (email != null && isConfiguredAdmin(email))
                            ? UserRole.ADMIN
                            : UserRole.USER;

                    if (role == UserRole.ADMIN) {
                        log.info("New user {} created as ADMIN (matched app.admin.emails)", email);
                    }

                    return userRepository.save(
                            User.builder()
                                    .firebaseUid(firebaseUid)
                                    .email(email)
                                    .displayName(displayName)
                                    .role(role)
                                    .build()
                    );
                });
    }

    private boolean isConfiguredAdmin(String email) {
        if (adminEmailsRaw == null || adminEmailsRaw.isBlank()) return false;
        return Arrays.stream(adminEmailsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .anyMatch(s -> s.equalsIgnoreCase(email));
    }
}