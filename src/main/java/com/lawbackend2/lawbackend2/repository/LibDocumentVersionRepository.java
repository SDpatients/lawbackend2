package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibDocumentVersionRepository extends JpaRepository<LibDocumentVersion, Long>, JpaSpecificationExecutor<LibDocumentVersion> {

    List<LibDocumentVersion> findByDocumentIdOrderByVersionNumberDesc(Long documentId);

    Optional<LibDocumentVersion> findByDocumentIdAndVersionNumber(Long documentId, Integer versionNumber);

    Optional<LibDocumentVersion> findFirstByDocumentIdOrderByVersionNumberDesc(Long documentId);

    @Query("SELECT v FROM LibDocumentVersion v WHERE v.documentId = :documentId ORDER BY v.versionNumber DESC")
    List<LibDocumentVersion> findActiveVersionsByDocumentId(@Param("documentId") Long documentId);

    @Query("SELECT MAX(v.versionNumber) FROM LibDocumentVersion v WHERE v.documentId = :documentId")
    Integer findMaxVersionNumber(@Param("documentId") Long documentId);

    @Query("SELECT COUNT(v) FROM LibDocumentVersion v WHERE v.documentId = :documentId")
    Long countByDocumentId(@Param("documentId") Long documentId);

    @Modifying
    @Query("DELETE FROM LibDocumentVersion v WHERE v.documentId = :documentId")
    void deleteByDocumentId(@Param("documentId") Long documentId);
}
