package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Optional<Property> findByPropertyNoAndIsDeleted(String propertyNo, Boolean isDeleted);

    @Query("SELECT p FROM Property p WHERE p.isDeleted = false " +
           "AND (:caseId IS NULL OR p.caseId = :caseId) " +
           "AND (:propertyType IS NULL OR p.propertyType = :propertyType) " +
           "AND (:propertyStatus IS NULL OR p.propertyStatus = :propertyStatus) " +
           "AND (:managementStatus IS NULL OR p.managementStatus = :managementStatus)")
    Page<Property> findByConditions(@Param("caseId") Long caseId,
                                  @Param("propertyType") String propertyType,
                                  @Param("propertyStatus") String propertyStatus,
                                  @Param("managementStatus") String managementStatus,
                                  Pageable pageable);

    List<Property> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
}