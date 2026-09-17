package com.setupbuilder.repository;

import com.setupbuilder.entity.BuildComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildComponentRepository extends JpaRepository<BuildComponent, Long> {

    List<BuildComponent> findByBuildId(Long buildId);

    void deleteByBuildId(Long buildId);
}