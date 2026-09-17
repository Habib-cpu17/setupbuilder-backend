package com.setupbuilder.service;

import com.setupbuilder.dto.ComponentResponse;
import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.enums.ComponentCategory;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    @Transactional(readOnly = true)
    public Page<ComponentResponse> list(ComponentCategory category,
                                        String brand,
                                        String search,
                                        int page,
                                        int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Component> result;
        if (search != null && !search.isBlank()) {
            result = componentRepository.findByNameContainingIgnoreCaseAndActiveTrue(search, pageable);
        } else if (brand != null && !brand.isBlank()) {
            result = componentRepository.findByBrandIgnoreCaseAndActiveTrue(brand, pageable);
        } else if (category != null) {
            result = componentRepository.findByCategoryAndActiveTrue(category, pageable);
        } else {
            result = componentRepository.findByActiveTrue(pageable);
        }

        return result.map(ComponentResponse::from);
    }

    @Transactional(readOnly = true)
    public ComponentResponse getById(Long id) {
        Component c = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found: " + id));
        return ComponentResponse.from(c);
    }

    /** Used internally when building a build. */
    @Transactional(readOnly = true)
    public Component getEntity(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found: " + id));
    }
}