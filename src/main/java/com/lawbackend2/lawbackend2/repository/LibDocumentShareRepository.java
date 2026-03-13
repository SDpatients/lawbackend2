package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibDocumentShareRepository extends JpaRepository<LibDocumentShare, Long>, JpaSpecificationExecutor<LibDocumentShare> {

    Optional<LibDocumentShare> findByShareCode(String shareCode);

    List<LibDocumentShare> findByDocumentId(Long documentId);

    List<LibDocumentShare> findByCreateUserId(Long userId);

    @Modifying
    @Query("UPDATE LibDocumentShare s SET s.accessCount = s.accessCount + 1 WHERE s.id = :id")
    void incrementAccessCount(@Param("id") Long id);

    @Query("SELECT s FROM LibDocumentShare s WHERE s.isEnabled = true AND s.isDeleted = false AND (s.expireTime IS NULL OR s.expireTime > CURRENT_TIMESTAMP)")
    List<LibDocumentShare> findActiveShares();
}
