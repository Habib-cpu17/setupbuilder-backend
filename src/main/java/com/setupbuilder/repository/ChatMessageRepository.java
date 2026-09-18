package com.setupbuilder.repository;

import com.setupbuilder.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    long countBySessionId(Long sessionId);

    List<ChatMessage> findBySessionIdOrderByCreatedAtDesc(Long sessionId, Pageable pageable);
}