package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DocumentDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentDeliveryRepository extends JpaRepository<DocumentDelivery, Long>, JpaSpecificationExecutor<DocumentDelivery> {

    Optional<DocumentDelivery> findByCaseIdAndDocumentName(Long caseId, String documentName);

    Page<DocumentDelivery> findByCaseId(Long caseId, Pageable pageable);

    Page<DocumentDelivery> findBySendStatus(String sendStatus, Pageable pageable);

    Page<DocumentDelivery> findByDocumentType(String documentType, Pageable pageable);

    Page<DocumentDelivery> findByRecipientType(String recipientType, Pageable pageable);

    Page<DocumentDelivery> findByDeliveryMethod(String deliveryMethod, Pageable pageable);

    Page<DocumentDelivery> findByCaseIdAndSendStatus(Long caseId, String sendStatus, Pageable pageable);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.caseId = :caseId AND d.isDeleted = false")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.sendStatus = :sendStatus AND d.isDeleted = false")
    Long countBySendStatus(@Param("sendStatus") String sendStatus);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.caseId = :caseId AND d.sendStatus = :sendStatus AND d.isDeleted = false")
    Long countByCaseIdAndSendStatus(@Param("caseId") Long caseId, @Param("sendStatus") String sendStatus);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.deliveryMethod = :deliveryMethod AND d.isDeleted = false")
    Long countByDeliveryMethod(@Param("deliveryMethod") String deliveryMethod);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.recipientType = :recipientType AND d.isDeleted = false")
    Long countByRecipientType(@Param("recipientType") String recipientType);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.documentType = :documentType AND d.isDeleted = false")
    Long countByDocumentType(@Param("documentType") String documentType);

    @Query("SELECT d.sendStatus, COUNT(d) FROM DocumentDelivery d WHERE d.isDeleted = false GROUP BY d.sendStatus")
    List<Object[]> countBySendStatusGroup();

    @Query("SELECT d.documentType, COUNT(d) FROM DocumentDelivery d WHERE d.documentType IS NOT NULL AND d.isDeleted = false GROUP BY d.documentType")
    List<Object[]> countByDocumentTypeGroup();

    @Query("SELECT d.deliveryMethod, COUNT(d) FROM DocumentDelivery d WHERE d.deliveryMethod IS NOT NULL AND d.isDeleted = false GROUP BY d.deliveryMethod")
    List<Object[]> countByDeliveryMethodGroup();

    @Query("SELECT d.recipientType, COUNT(d) FROM DocumentDelivery d WHERE d.recipientType IS NOT NULL AND d.isDeleted = false GROUP BY d.recipientType")
    List<Object[]> countByRecipientTypeGroup();

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE DATE(d.createTime) = :date AND d.isDeleted = false")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE YEAR(d.createTime) = :year AND MONTH(d.createTime) = :month AND d.isDeleted = false")
    Long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE YEAR(d.createTime) = :year AND d.isDeleted = false")
    Long countByCreatedAtYear(@Param("year") int year);

    @Query("SELECT COUNT(d) FROM DocumentDelivery d WHERE d.createTime BETWEEN :startDate AND :endDate AND d.isDeleted = false")
    Long countByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d.sendStatus, COUNT(d) FROM DocumentDelivery d WHERE d.createTime BETWEEN :startDate AND :endDate AND d.isDeleted = false GROUP BY d.sendStatus")
    List<Object[]> countBySendStatusGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM DocumentDelivery d WHERE d.isDeleted = false ORDER BY d.createTime DESC")
    List<DocumentDelivery> findAllActive();

    @Query("SELECT d FROM DocumentDelivery d WHERE d.caseId = :caseId AND d.isDeleted = false ORDER BY d.createTime DESC")
    List<DocumentDelivery> findByCaseIdOrderByCreateTimeDesc(@Param("caseId") Long caseId);

    @Query("SELECT d FROM DocumentDelivery d WHERE d.sendStatus = :sendStatus AND d.isDeleted = false ORDER BY d.createTime DESC")
    List<DocumentDelivery> findBySendStatusOrderByCreateTimeDesc(@Param("sendStatus") String sendStatus);

    @Query("SELECT d FROM DocumentDelivery d WHERE d.recipientName LIKE %:keyword% AND d.isDeleted = false ORDER BY d.createTime DESC")
    List<DocumentDelivery> findByRecipientNameContaining(@Param("keyword") String keyword);

    @Query("SELECT d FROM DocumentDelivery d WHERE " +
            "(:caseId IS NULL OR d.caseId = :caseId) AND " +
            "(:caseNumber IS NULL OR d.caseNumber LIKE %:caseNumber%) AND " +
            "(:documentType IS NULL OR d.documentType = :documentType) AND " +
            "(:recipientType IS NULL OR d.recipientType = :recipientType) AND " +
            "(:deliveryMethod IS NULL OR d.deliveryMethod = :deliveryMethod) AND " +
            "(:sendStatus IS NULL OR d.sendStatus = :sendStatus) AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "d.isDeleted = false ORDER BY d.createTime DESC")
    Page<DocumentDelivery> findByConditions(@Param("caseId") Long caseId,
                                       @Param("caseNumber") String caseNumber,
                                       @Param("documentType") String documentType,
                                       @Param("recipientType") String recipientType,
                                       @Param("deliveryMethod") String deliveryMethod,
                                       @Param("sendStatus") String sendStatus,
                                       @Param("status") String status,
                                       Pageable pageable);

    @Query("SELECT d FROM DocumentDelivery d WHERE " +
            "(:documentType IS NULL OR d.documentType LIKE %:documentType%) AND " +
            "(:status IS NULL OR d.status LIKE %:status%) AND " +
            "(:caseNumber IS NULL OR d.caseNumber LIKE %:caseNumber%) AND " +
            "(:sendStatus IS NULL OR d.sendStatus = :sendStatus) AND " +
            "d.isDeleted = false ORDER BY d.createTime DESC")
    Page<DocumentDelivery> findByFuzzyConditions(@Param("documentType") String documentType,
                                             @Param("status") String status,
                                             @Param("caseNumber") String caseNumber,
                                             @Param("sendStatus") String sendStatus,
                                             Pageable pageable);
    void deleteByCaseId(Long caseId);

    @Query(value = "SELECT d.abbreviation FROM tb_document_delivery d WHERE d.abbreviation IS NOT NULL AND d.is_deleted = false ORDER BY d.create_time DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLatestAbbreviation();

    @Query(value = "SELECT d.abbreviation FROM tb_document_delivery d WHERE d.case_id = :caseId AND d.abbreviation IS NOT NULL AND d.is_deleted = false ORDER BY d.create_time DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLatestAbbreviationByCaseId(@Param("caseId") Long caseId);

    @Query(value = "SELECT MAX(CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(d.document_number, '破管字第', -1), '号', 1) AS UNSIGNED)) FROM tb_document_delivery d WHERE d.document_number LIKE :prefix AND d.is_deleted = false", nativeQuery = true)
    Long findMaxDocumentNumberByPrefix(@Param("prefix") String prefix);
}
