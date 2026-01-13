package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.RealEstate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {

    Optional<RealEstate> findByEstateNoAndIsDeleted(String estateNo, Boolean isDeleted);

    @Query("SELECT re FROM RealEstate re WHERE re.isDeleted = false " +
           "AND (:caseId IS NULL OR re.caseId = :caseId) " +
           "AND (:estateType IS NULL OR re.estateType = :estateType) " +
           "AND (:estateStatus IS NULL OR re.estateStatus = :estateStatus) " +
           "AND (:managementStatus IS NULL OR re.managementStatus = :managementStatus)")
    Page<RealEstate> findByConditions(@Param("caseId") Long caseId,
                                    @Param("estateType") String estateType,
                                    @Param("estateStatus") String estateStatus,
                                    @Param("managementStatus") String managementStatus,
                                    Pageable pageable);

    List<RealEstate> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    List<RealEstate> findByPropertyIdAndIsDeleted(Long propertyId, Boolean isDeleted);
}