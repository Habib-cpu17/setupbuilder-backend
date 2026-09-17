package com.setupbuilder.service;

import com.setupbuilder.dto.BuildResponse;
import com.setupbuilder.dto.PublicProfileResponse;
import com.setupbuilder.dto.UpdateProfileRequest;
import com.setupbuilder.entity.Build;
import com.setupbuilder.entity.User;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.BuildRepository;
import com.setupbuilder.repository.CommentRepository;
import com.setupbuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final BuildRepository buildRepository;
    private final CommentRepository commentRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public PublicProfileResponse getProfile(Long userId, User requester) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        boolean isSelf = requester != null && requester.getId().equals(userId);
        boolean isAdmin = requester != null && requester.getRole().name().equals("ADMIN");

        long totalBuilds = buildRepository.countByUserId(userId);
        long publicBuilds = buildRepository.countPublicByUserId(userId);
        long totalComments = commentRepository.findByUserIdOrderByCreatedAtDesc(
                userId, org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
        long commentsReceived = commentRepository.countByBuildOwnerId(userId);

        PublicProfileResponse.Stats stats = new PublicProfileResponse.Stats(
                totalBuilds, publicBuilds, totalComments, commentsReceived
        );

        List<String> achievements = computeAchievements(user, stats);

        // Load builds — owner sees all; others only see public ones
        List<Build> rawBuilds = isSelf || isAdmin
                ? buildRepository.findByUserId(userId)
                : buildRepository.findByUserId(userId).stream()
                .filter(b -> Boolean.TRUE.equals(b.getIsPublic()))
                .toList();

        List<BuildResponse> builds = rawBuilds.stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .map(b -> BuildResponse.from(b, List.of()))
                .toList();

        return new PublicProfileResponse(
                user.getId(),
                user.getDisplayName(),
                isSelf || isAdmin ? user.getEmail() : null,
                user.getRole().name(),
                user.getAvatarUrl(),
                user.getBannerUrl(),
                user.getBio(),
                user.getLocation(),
                user.getWebsiteUrl(),
                user.getCreatedAt(),
                stats,
                achievements,
                builds
        );
    }

    @Transactional
    public PublicProfileResponse updateOwn(User user, UpdateProfileRequest req) {
        User u = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (req.displayName() != null) u.setDisplayName(req.displayName().trim());
        if (req.bio() != null) u.setBio(req.bio().isBlank() ? null : req.bio().trim());
        if (req.location() != null) u.setLocation(req.location().isBlank() ? null : req.location().trim());
        if (req.websiteUrl() != null) u.setWebsiteUrl(req.websiteUrl().isBlank() ? null : req.websiteUrl().trim());

        // Avatar — delete the old file if the URL changed
        if (req.avatarUrl() != null) {
            String incoming = req.avatarUrl().isBlank() ? null : req.avatarUrl().trim();
            if (incoming == null && u.getAvatarUrl() != null) {
                fileStorageService.deleteByUrl(u.getAvatarUrl());
            }
            u.setAvatarUrl(incoming);
        }

        // Banner — same logic
        if (req.bannerUrl() != null) {
            String incoming = req.bannerUrl().isBlank() ? null : req.bannerUrl().trim();
            if (incoming == null && u.getBannerUrl() != null) {
                fileStorageService.deleteByUrl(u.getBannerUrl());
            }
            u.setBannerUrl(incoming);
        }

        userRepository.save(u);
        return getProfile(u.getId(), u);
    }

    private List<String> computeAchievements(User user, PublicProfileResponse.Stats s) {
        List<String> out = new ArrayList<>();

        if (s.totalBuilds() >= 1) out.add("FIRST_BUILD");
        if (s.totalBuilds() >= 5) out.add("BUILDER");
        if (s.totalBuilds() >= 10) out.add("ARCHITECT");
        if (s.totalBuilds() >= 25) out.add("ENTHUSIAST");

        if (s.publicBuilds() >= 1) out.add("PUBLIC_VOICE");
        if (s.publicBuilds() >= 5) out.add("COMMUNITY_SHARER");

        if (s.totalComments() >= 10) out.add("COMMENTATOR");
        if (s.commentsReceived() >= 10) out.add("WELL_KNOWN");

        if ("ADMIN".equals(user.getRole().name())) out.add("SITE_ADMIN");

        return out;
    }
}