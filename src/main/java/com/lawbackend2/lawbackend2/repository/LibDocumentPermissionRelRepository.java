package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentPermissionRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibDocumentPermissionRelRepository extends JpaRepository<LibDocumentPermissionRel, Long>, JpaSpecificationExecutor<LibDocumentPermissionRel> {

    List<LibDocumentPermissionRel> findByDocumentId(Long documentId);

    List<LibDocumentPermissionRel> findByPermissionId(Long permissionId);

    @Query("SELECT dpr FROM LibDocumentPermissionRel dpr WHERE dpr.documentId = :documentId AND dpr.targetType = :targetType AND dpr.targetId = :targetId")
    List<LibDocumentPermissionRel> findByDocumentIdAndTarget(@Param("documentId") Long documentId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Query("SELECT dpr FROM LibDocumentPermissionRel dpr WHERE dpr.targetType = :targetType AND dpr.targetId = :targetId")
    List<LibDocumentPermissionRel> findByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Modifying
    @Query("DELETE FROM LibDocumentPermissionRel dpr WHERE dpr.documentId = :documentId")
    void deleteByDocumentId(@Param("documentId") Long documentId);
}
