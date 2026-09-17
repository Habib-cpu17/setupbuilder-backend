package com.setupbuilder.repository;

import com.setupbuilder.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByBuildIdOrderByCreatedAtDesc(Long buildId, Pageable pageable);

    long countByBuildId(Long buildId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.build.user.id = :userId")
    long countByBuildOwnerId(@Param("userId") Long userId);

    Page<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}