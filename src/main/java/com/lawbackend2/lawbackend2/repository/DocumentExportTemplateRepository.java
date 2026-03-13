package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DocumentExportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文档导出模板 Repository 接口
 */
@Repository
public interface DocumentExportTemplateRepository extends JpaRepository<DocumentExportTemplate, Long> {

    /**
     * 根据模板编码查询
     */
    Optional<DocumentExportTemplate> findByTemplateCode(String templateCode);

    /**
     * 根据类型和状态查询模板列表
     */
    List<DocumentExportTemplate> findByTemplateTypeAndStatusAndIsDeletedFalse(String templateType, String status);

    /**
     * 查询所有有效模板
     */
    List<DocumentExportTemplate> findByStatusAndIsDeletedFalseOrderByCreateTimeDesc(String status);

    /**
     * 查询某类型的默认模板
     */
    List<DocumentExportTemplate> findByIsDefaultTrueAndTemplateTypeAndStatusAndIsDeletedFalse(String templateType, String status);

    /**
     * 检查模板编码是否存在
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * 根据描述模糊查询
     */
    List<DocumentExportTemplate> findByStatusAndIsDeletedFalseAndDescriptionContainingOrderByCreateTimeDesc(String status, String description);

    /**
     * 根据分类查询模板
     */
    List<DocumentExportTemplate> findByCategoryAndStatusAndIsDeletedFalse(String category, String status);
}
