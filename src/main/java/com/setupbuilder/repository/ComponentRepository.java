package com.setupbuilder.repository;

import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.enums.ComponentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    Page<Component> findByCategoryAndActiveTrue(ComponentCategory category, Pageable pageable);

    Page<Component> findByActiveTrue(Pageable pageable);

    Page<Component> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    Page<Component> findByBrandIgnoreCaseAndActiveTrue(String brand, Pageable pageable);
}