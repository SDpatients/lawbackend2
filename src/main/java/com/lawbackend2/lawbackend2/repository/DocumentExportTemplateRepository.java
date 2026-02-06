package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DocumentExportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentExportTemplateRepository extends JpaRepository<DocumentExportTemplate, Long> {

    Optional<DocumentExportTemplate> findByTemplateCode(String templateCode);

    List<DocumentExportTemplate> findByTemplateTypeAndStatusAndIsDeletedFalse(String templateType, String status);

    List<DocumentExportTemplate> findByStatusAndIsDeletedFalseOrderByCreateTimeDesc(String status);

    List<DocumentExportTemplate> findByIsDefaultTrueAndTemplateTypeAndStatusAndIsDeletedFalse(String templateType, String status);

    boolean existsByTemplateCode(String templateCode);
}
