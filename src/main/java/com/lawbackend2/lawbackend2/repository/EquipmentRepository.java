package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional<Equipment> findByEquipmentNoAndIsDeleted(String equipmentNo, Boolean isDeleted);

    @Query("SELECT e FROM Equipment e WHERE e.isDeleted = false " +
           "AND (:caseId IS NULL OR e.caseId = :caseId) " +
           "AND (:equipmentType IS NULL OR e.equipmentType = :equipmentType) " +
           "AND (:equipmentStatus IS NULL OR e.equipmentStatus = :equipmentStatus) " +
           "AND (:managementStatus IS NULL OR e.managementStatus = :managementStatus)")
    Page<Equipment> findByConditions(@Param("caseId") Long caseId,
                                 @Param("equipmentType") String equipmentType,
                                 @Param("equipmentStatus") String equipmentStatus,
                                 @Param("managementStatus") String managementStatus,
                                 Pageable pageable);

    List<Equipment> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    List<Equipment> findByPropertyIdAndIsDeleted(Long propertyId, Boolean isDeleted);
}