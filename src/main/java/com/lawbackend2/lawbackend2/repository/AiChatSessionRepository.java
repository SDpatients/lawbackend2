package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AiChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {

    List<AiChatSession> findByCaseIdAndUserIdOrderByCreateTimeDesc(Long caseId, Long userId);

    Optional<AiChatSession> findByIdAndCaseIdAndUserId(Long id, Long caseId, Long userId);

    int countByCaseIdAndUserId(Long caseId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiChatSession s WHERE s.id = :sessionId")
    void deleteSessionById(Long sessionId);
}
