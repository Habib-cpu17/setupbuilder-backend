package com.setupbuilder.repository;

import com.setupbuilder.entity.Build;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long> {

    Page<Build> findByIsPublicTrue(Pageable pageable);

    Page<Build> findByUserId(Long userId, Pageable pageable);

    List<Build> findByUserId(Long userId);

    long countByUserId(Long userId);

    @Query("SELECT b FROM Build b WHERE b.curated = true ORDER BY b.curatedRank ASC NULLS LAST, b.updatedAt DESC")
    List<Build> findCuratedOrdered(Pageable pageable);

    @Query("SELECT MAX(b.curatedRank) FROM Build b WHERE b.curated = true")
    Integer findMaxCuratedRank();

    // also add pagination for admin (all builds)
    @Query("SELECT b FROM Build b ORDER BY b.updatedAt DESC")
    Page<Build> findAllOrdered(Pageable pageable);

    @Query("SELECT COUNT(b) FROM Build b WHERE b.user.id = :userId AND b.isPublic = true")
    long countPublicByUserId(@Param("userId") Long userId);

}