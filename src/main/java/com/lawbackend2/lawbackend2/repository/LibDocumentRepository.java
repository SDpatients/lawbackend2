package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocument;
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
public interface LibDocumentRepository extends JpaRepository<LibDocument, Long>, JpaSpecificationExecutor<LibDocument> {

    Optional<LibDocument> findByDocumentCode(String documentCode);

    boolean existsByDocumentCode(String documentCode);

    Page<LibDocument> findByFolderIdAndIsDeletedFalseOrderByCreateTimeDesc(Long folderId, Pageable pageable);

    List<LibDocument> findByFolderIdAndIsDeletedFalseOrderByCreateTimeDesc(Long folderId);

    @Query("SELECT d FROM LibDocument d WHERE d.folderId IS NULL AND d.isDeleted = false ORDER BY d.createTime DESC")
    Page<LibDocument> findRootDocuments(Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE (d.folderId IS NULL OR d.folderId = 0) AND d.isDeleted = false AND (d.isPublic = true OR d.createUserId = :userId) ORDER BY d.createTime DESC")
    Page<LibDocument> findRootDocumentsByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.documentType = :type AND d.isDeleted = false ORDER BY d.createTime DESC")
    Page<LibDocument> findByDocumentType(@Param("type") String type, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.documentType = :type AND d.isDeleted = false AND (d.isPublic = true OR d.createUserId = :userId) ORDER BY d.createTime DESC")
    Page<LibDocument> findByDocumentTypeAndUser(@Param("type") String type, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE (d.documentName LIKE %:keyword% OR d.description LIKE %:keyword% OR d.tags LIKE %:keyword%) AND d.isDeleted = false")
    Page<LibDocument> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE (d.documentName LIKE %:keyword% OR d.description LIKE %:keyword% OR d.tags LIKE %:keyword%) AND d.isDeleted = false AND (d.isPublic = true OR d.createUserId = :userId)")
    Page<LibDocument> searchByKeywordAndUser(@Param("keyword") String keyword, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.createUserId = :userId AND d.isDeleted = false ORDER BY d.createTime DESC")
    Page<LibDocument> findByCreateUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isPublic = true AND d.isDeleted = false ORDER BY d.createTime DESC")
    Page<LibDocument> findPublicDocuments(Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isLocked = true AND d.isDeleted = false")
    List<LibDocument> findLockedDocuments();

    @Query("SELECT d FROM LibDocument d WHERE d.lockedBy = :userId AND d.isLocked = true AND d.isDeleted = false")
    List<LibDocument> findLockedByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE LibDocument d SET d.viewCount = d.viewCount + 1 WHERE d.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE LibDocument d SET d.downloadCount = d.downloadCount + 1 WHERE d.id = :id")
    void incrementDownloadCount(@Param("id") Long id);

    @Query("SELECT COUNT(d) FROM LibDocument d WHERE d.folderId = :folderId AND d.isDeleted = false")
    Long countByFolderId(@Param("folderId") Long folderId);

    @Query("SELECT d FROM LibDocument d WHERE d.folderId = :folderId AND d.documentName = :name AND d.isDeleted = false")
    Optional<LibDocument> findByFolderIdAndDocumentName(@Param("folderId") Long folderId, @Param("name") String name);

    @Query("SELECT d FROM LibDocument d WHERE d.status = :status AND d.isDeleted = false ORDER BY d.createTime DESC")
    Page<LibDocument> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT COUNT(d) FROM LibDocument d WHERE d.isDeleted = false")
    Long countAllDocuments();

    @Query("SELECT COALESCE(SUM(d.fileSize), 0) FROM LibDocument d WHERE d.isDeleted = false")
    Long sumTotalFileSize();

    @Query("SELECT COUNT(d) FROM LibDocument d WHERE d.createTime >= :startTime AND d.isDeleted = false")
    Long countDocumentsSince(@Param("startTime") java.time.LocalDateTime startTime);

    @Query("SELECT COALESCE(SUM(d.viewCount), 0) FROM LibDocument d WHERE d.isDeleted = false")
    Long sumTotalViewCount();

    @Query("SELECT d.documentType, COUNT(d), COALESCE(SUM(d.fileSize), 0) FROM LibDocument d WHERE d.isDeleted = false GROUP BY d.documentType")
    List<Object[]> getDocumentTypeStats();

    @Query("SELECT FUNCTION('DATE_FORMAT', d.createTime, '%Y-%m') as month, COUNT(d) as count FROM LibDocument d WHERE d.createTime >= :startTime AND d.isDeleted = false GROUP BY FUNCTION('DATE_FORMAT', d.createTime, '%Y-%m') ORDER BY month")
    List<Object[]> countDocumentsByMonth(@Param("startTime") java.time.LocalDateTime startTime);

    @Query("SELECT d FROM LibDocument d WHERE d.isDeleted = false AND (d.isPublic = true OR d.createUserId = :userId) ORDER BY d.createTime DESC")
    Page<LibDocument> findRecentDocuments(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isDeleted = false AND d.isPublic = true ORDER BY d.createTime DESC")
    Page<LibDocument> findRecentPublicDocuments(Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isDeleted = false AND (d.isPublic = true OR d.createUserId = :userId) ORDER BY d.viewCount DESC, d.downloadCount DESC")
    Page<LibDocument> findPopularDocuments(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isDeleted = false AND d.isPublic = true ORDER BY d.viewCount DESC, d.downloadCount DESC")
    Page<LibDocument> findPopularPublicDocuments(Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isDeleted = false AND d.createTime >= :startTime AND (d.isPublic = true OR d.createUserId = :userId) ORDER BY d.viewCount DESC, d.downloadCount DESC")
    Page<LibDocument> findPopularDocumentsByTimeRange(@Param("startTime") java.time.LocalDateTime startTime, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM LibDocument d WHERE d.isDeleted = false AND d.createTime >= :startTime AND d.isPublic = true ORDER BY d.viewCount DESC, d.downloadCount DESC")
    Page<LibDocument> findPopularPublicDocumentsByTimeRange(@Param("startTime") java.time.LocalDateTime startTime, Pageable pageable);
}
