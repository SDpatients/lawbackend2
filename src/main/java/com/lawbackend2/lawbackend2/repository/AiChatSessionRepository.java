package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AiChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {

    Optional<AiChatSession> findByCaseIdAndUserId(Long caseId, Long userId);
}
