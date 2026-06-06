package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    List<AiChatMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);

    List<AiChatMessage> findByCaseIdOrderByTimestampAsc(Long caseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiChatMessage m WHERE m.sessionId = :sessionId")
    void deleteBySessionId(Long sessionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiChatMessage m WHERE m.caseId = :caseId")
    void deleteByCaseId(Long caseId);
}
