package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibDocumentFavoriteRepository extends JpaRepository<LibDocumentFavorite, Long>, JpaSpecificationExecutor<LibDocumentFavorite> {

    Optional<LibDocumentFavorite> findByDocumentIdAndUserId(Long documentId, Long userId);

    boolean existsByDocumentIdAndUserId(Long documentId, Long userId);

    @Query("SELECT f FROM LibDocumentFavorite f LEFT JOIN FETCH LibDocument d ON f.documentId = d.id WHERE f.userId = :userId ORDER BY f.createTime DESC")
    Page<LibDocumentFavorite> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId, Pageable pageable);

    List<LibDocumentFavorite> findByUserIdAndFolderName(Long userId, String folderName);

    @Query("SELECT DISTINCT f.folderName FROM LibDocumentFavorite f WHERE f.userId = :userId AND f.folderName IS NOT NULL")
    List<String> findDistinctFolderNamesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM LibDocumentFavorite f WHERE f.documentId = :documentId")
    void deleteByDocumentId(@Param("documentId") Long documentId);
}
