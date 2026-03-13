package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibDocumentOperationLogRepository extends JpaRepository<LibDocumentOperationLog, Long>, JpaSpecificationExecutor<LibDocumentOperationLog> {

    Page<LibDocumentOperationLog> findByDocumentIdOrderByCreateTimeDesc(Long documentId, Pageable pageable);

    Page<LibDocumentOperationLog> findByFolderIdOrderByCreateTimeDesc(Long folderId, Pageable pageable);

    Page<LibDocumentOperationLog> findByCreateUserId(Long userId, Pageable pageable);

    Page<LibDocumentOperationLog> findByOperationType(String operationType, Pageable pageable);

    @Query("SELECT l FROM LibDocumentOperationLog l WHERE l.documentId = :documentId AND l.isDeleted = false ORDER BY l.createTime DESC")
    List<LibDocumentOperationLog> findActiveLogsByDocumentId(@Param("documentId") Long documentId);

    @Query("SELECT l FROM LibDocumentOperationLog l WHERE l.folderId = :folderId AND l.isDeleted = false ORDER BY l.createTime DESC")
    List<LibDocumentOperationLog> findActiveLogsByFolderId(@Param("folderId") Long folderId);

    @Query("SELECT FUNCTION('DATE_FORMAT', l.createTime, '%Y-%m') as month, COUNT(l) as count FROM LibDocumentOperationLog l WHERE l.operationType = 'VIEW' AND l.createTime >= :startTime AND l.isDeleted = false GROUP BY FUNCTION('DATE_FORMAT', l.createTime, '%Y-%m') ORDER BY month")
    List<Object[]> countViewsByMonth(@Param("startTime") java.time.LocalDateTime startTime);
}
