package com.setupbuilder.service;

import com.setupbuilder.dto.BuildComponentRequest;
import com.setupbuilder.dto.BuildRequest;
import com.setupbuilder.dto.BuildResponse;
import com.setupbuilder.entity.Build;
import com.setupbuilder.entity.BuildComponent;
import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.User;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.BuildComponentRepository;
import com.setupbuilder.repository.BuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildService {

    private final BuildRepository buildRepository;
    private final BuildComponentRepository buildComponentRepository;
    private final ComponentService componentService;
    private final CompatibilityService compatibilityService;

    // ---------- CREATE ----------

    @Transactional
    public BuildResponse create(BuildRequest req, User owner) {
        Build build = Build.builder()
                .name(req.name())
                .description(req.description())
                .isPublic(Boolean.TRUE.equals(req.isPublic()))
                .user(owner)
                .totalPrice(BigDecimal.ZERO)
                .build();

        build = buildRepository.save(build);

        List<BuildComponent> lines = buildLines(req.components(), build);
        build.getComponents().addAll(lines);
        build.setTotalPrice(computeTotal(lines));

        build = buildRepository.save(build);

        List<String> warnings = compatibilityService.check(build.getComponents());
        return BuildResponse.from(build, warnings);
    }

    // ---------- UPDATE ----------

    @Transactional
    public BuildResponse update(Long id, BuildRequest req, User requester) {
        Build build = buildRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + id));
        assertOwnerOrAdmin(build, requester);

        build.setName(req.name());
        build.setDescription(req.description());
        build.setIsPublic(Boolean.TRUE.equals(req.isPublic()));

        // wipe and re-add components
        build.getComponents().clear();
        buildComponentRepository.deleteByBuildId(build.getId());

        List<BuildComponent> lines = buildLines(req.components(), build);
        build.getComponents().addAll(lines);
        build.setTotalPrice(computeTotal(lines));

        build = buildRepository.save(build);

        List<String> warnings = compatibilityService.check(build.getComponents());
        return BuildResponse.from(build, warnings);
    }

    // ---------- READ ----------

    @Transactional(readOnly = true)
    public BuildResponse getById(Long id, User requester) {
        Build build = buildRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + id));

        boolean isOwner = requester != null && build.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester != null && requester.getRole().name().equals("ADMIN");

        if (!Boolean.TRUE.equals(build.getIsPublic()) && !isOwner && !isAdmin) {
            throw new AccessDeniedException("This build is private.");
        }

        List<String> warnings = compatibilityService.check(build.getComponents());
        return BuildResponse.from(build, warnings);
    }

    @Transactional(readOnly = true)
    public Page<BuildResponse> listMyBuilds(User owner, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return buildRepository.findByUserId(owner.getId(), pageable)
                .map(b -> BuildResponse.from(b, List.of())); // no warnings for list views
    }

    @Transactional(readOnly = true)
    public Page<BuildResponse> listPublicBuilds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return buildRepository.findByIsPublicTrue(pageable)
                .map(b -> BuildResponse.from(b, List.of()));
    }

    // ---------- DELETE ----------

    @Transactional
    public void delete(Long id, User requester) {
        Build build = buildRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + id));
        assertOwnerOrAdmin(build, requester);
        buildRepository.delete(build);
    }

    // ---------- helpers ----------

    private List<BuildComponent> buildLines(List<BuildComponentRequest> reqs, Build build) {
        List<BuildComponent> lines = new ArrayList<>();
        for (BuildComponentRequest r : reqs) {
            Component c = componentService.getEntity(r.componentId());
            BigDecimal priceSnapshot = c.getFallbackPrice() != null ? c.getFallbackPrice() : BigDecimal.ZERO;
            lines.add(BuildComponent.builder()
                    .build(build)
                    .component(c)
                    .quantity(r.qty())
                    .priceAtTimeOfBuild(priceSnapshot)
                    .build());
        }
        return lines;
    }

    private BigDecimal computeTotal(List<BuildComponent> lines) {
        return lines.stream()
                .map(bc -> {
                    BigDecimal unit = bc.getPriceAtTimeOfBuild() != null
                            ? bc.getPriceAtTimeOfBuild()
                            : BigDecimal.ZERO;
                    return unit.multiply(BigDecimal.valueOf(bc.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void assertOwnerOrAdmin(Build build, User requester) {
        boolean isOwner = build.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().name().equals("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You do not own this build.");
        }
    }

    @Transactional(readOnly = true)
    public List<String> checkCompatibility(List<BuildComponentRequest> reqs) {
        List<BuildComponent> lines = new ArrayList<>();
        for (BuildComponentRequest r : reqs) {
            Component c = componentService.getEntity(r.componentId());
            lines.add(BuildComponent.builder()
                    .component(c)
                    .quantity(r.qty())
                    .build());
        }
        return compatibilityService.check(lines);
    }

    @Transactional(readOnly = true)
    public Page<BuildResponse> listAllForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return buildRepository.findAllOrdered(pageable)
                .map(b -> BuildResponse.from(b, List.of()));
    }
}