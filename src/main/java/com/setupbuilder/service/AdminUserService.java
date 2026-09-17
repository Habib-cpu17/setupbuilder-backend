package com.setupbuilder.service;

import com.setupbuilder.dto.RoleUpdateRequest;
import com.setupbuilder.dto.UserAdminResponse;
import com.setupbuilder.entity.User;
import com.setupbuilder.entity.enums.UserRole;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.BuildRepository;
import com.setupbuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final BuildRepository buildRepository;

    @Transactional(readOnly = true)
    public Page<UserAdminResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable)
                .map(u -> UserAdminResponse.from(u, buildRepository.countByUserId(u.getId())));
    }

    @Transactional
    public UserAdminResponse updateRole(Long userId, RoleUpdateRequest req, User requester) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        UserRole newRole;
        try {
            newRole = UserRole.valueOf(req.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + req.role());
        }

        // Safety: prevent an admin from demoting themselves (avoids lock-out).
        if (target.getId().equals(requester.getId()) && newRole != UserRole.ADMIN) {
            throw new AccessDeniedException("You cannot change your own role.");
        }

        target.setRole(newRole);
        userRepository.save(target);
        return UserAdminResponse.from(target, buildRepository.countByUserId(target.getId()));
    }
}