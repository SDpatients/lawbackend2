package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DocumentTemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文档模板字段 Repository 接口
 */
@Repository
public interface DocumentTemplateFieldRepository extends JpaRepository<DocumentTemplateField, Long> {

    /**
     * 根据模板 ID 查询字段列表
     */
    List<DocumentTemplateField> findByTemplateIdOrderBySortOrderAsc(Long templateId);

    /**
     * 根据模板 ID 和状态查询字段列表
     */
    List<DocumentTemplateField> findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(Long templateId, String status);

    /**
     * 根据模板 ID 删除字段
     */
    void deleteByTemplateId(Long templateId);

    /**
     * 根据模板 ID 批量删除字段（原生 SQL）
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM tb_document_template_field WHERE template_id = ?1", nativeQuery = true)
    void deleteByTemplateIdNative(Long templateId);
}
