package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    List<AiChatMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);

    List<AiChatMessage> findByCaseIdOrderByTimestampAsc(Long caseId);
}
