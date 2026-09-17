package com.setupbuilder.repository;

import com.setupbuilder.entity.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    Page<PriceHistory> findByComponentIdOrderByCreatedAtDesc(Long componentId, Pageable pageable);
}