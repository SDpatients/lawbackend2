package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DocumentTemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTemplateFieldRepository extends JpaRepository<DocumentTemplateField, Long> {

    List<DocumentTemplateField> findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(Long templateId, String status);

    List<DocumentTemplateField> findByTemplateIdAndIsDeletedFalse(Long templateId);

    void deleteByTemplateId(Long templateId);

    @Modifying
    @Query(value = "DELETE FROM tb_document_template_field WHERE template_id = ?1", nativeQuery = true)
    void deleteByTemplateIdNative(Long templateId);

    boolean existsByTemplateIdAndFieldNameAndIsDeletedFalse(Long templateId, String fieldName);
}
