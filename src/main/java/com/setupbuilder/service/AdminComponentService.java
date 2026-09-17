package com.setupbuilder.service;

import com.setupbuilder.dto.ComponentAdminRequest;
import com.setupbuilder.dto.ComponentResponse;
import com.setupbuilder.entity.Component;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AdminComponentService {

    private final ComponentRepository componentRepository;

    @Transactional(readOnly = true)
    public Page<ComponentResponse> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return componentRepository.findAll(pageable).map(ComponentResponse::from);
    }

    @Transactional
    public ComponentResponse create(ComponentAdminRequest req) {
        Component c = new Component();
        apply(c, req, true);
        return ComponentResponse.from(componentRepository.save(c));
    }

    @Transactional
    public ComponentResponse update(Long id, ComponentAdminRequest req) {
        Component c = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found: " + id));
        apply(c, req, false);
        return ComponentResponse.from(componentRepository.save(c));
    }

    @Transactional
    public void softDelete(Long id) {
        Component c = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found: " + id));
        c.setActive(false);
        componentRepository.save(c);
    }

    @Transactional
    public ComponentResponse setActive(Long id, boolean active) {
        Component c = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found: " + id));
        c.setActive(active);
        return ComponentResponse.from(componentRepository.save(c));
    }

    private void apply(Component c, ComponentAdminRequest req, boolean isCreate) {
        c.setName(req.name().trim());
        c.setCategory(req.category());
        c.setBrand(req.brand());
        c.setModel(req.model());
        c.setImageUrl(req.imageUrl());
        c.setSpecs(req.specs() == null ? new HashMap<>() : new HashMap<>(req.specs()));
        c.setFallbackPrice(req.fallbackPrice() == null ? BigDecimal.ZERO : req.fallbackPrice());
        if (isCreate) {
            c.setActive(req.active() == null || req.active());
        } else if (req.active() != null) {
            c.setActive(req.active());
        }
    }
}