package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DocumentExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentExportHistoryRepository extends JpaRepository<DocumentExportHistory, Long> {

    List<DocumentExportHistory> findByExportedByOrderByExportedTimeDesc(Long exportedBy);

    List<DocumentExportHistory> findByTemplateIdAndExportedByOrderByExportedTimeDesc(Long templateId, Long exportedBy);

    List<DocumentExportHistory> findByExportStatusAndExportedByOrderByExportedTimeDesc(String exportStatus, Long exportedBy);
}
