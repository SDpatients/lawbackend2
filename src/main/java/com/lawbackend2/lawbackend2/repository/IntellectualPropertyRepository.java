package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.IntellectualProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntellectualPropertyRepository extends JpaRepository<IntellectualProperty, Long> {

    Optional<IntellectualProperty> findByIpNoAndIsDeleted(String ipNo, Boolean isDeleted);

    @Query("SELECT ip FROM IntellectualProperty ip WHERE ip.isDeleted = false " +
           "AND (:caseId IS NULL OR ip.caseId = :caseId) " +
           "AND (:ipType IS NULL OR ip.ipType = :ipType) " +
           "AND (:ipStatus IS NULL OR ip.ipStatus = :ipStatus) " +
           "AND (:managementStatus IS NULL OR ip.managementStatus = :managementStatus)")
    Page<IntellectualProperty> findByConditions(@Param("caseId") Long caseId,
                                          @Param("ipType") String ipType,
                                          @Param("ipStatus") String ipStatus,
                                          @Param("managementStatus") String managementStatus,
                                          Pageable pageable);

    List<IntellectualProperty> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    List<IntellectualProperty> findByPropertyIdAndIsDeleted(Long propertyId, Boolean isDeleted);
}