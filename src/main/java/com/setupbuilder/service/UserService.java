package com.setupbuilder.service;

import com.setupbuilder.entity.User;
import com.setupbuilder.entity.enums.UserRole;
import com.setupbuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Called on every authenticated request. Creates the local User row the
     * first time we see a given Firebase UID, and refreshes the email/name if
     * they've changed on the Firebase side.
     */
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
                    return dirty ? userRepository.save(existing) : existing;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .firebaseUid(firebaseUid)
                                .email(email)
                                .displayName(displayName)
                                .role(UserRole.USER)
                                .build()
                ));
    }
}