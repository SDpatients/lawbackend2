package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByInventoryNoAndIsDeleted(String inventoryNo, Boolean isDeleted);

    @Query("SELECT i FROM Inventory i WHERE i.isDeleted = false " +
           "AND (:caseId IS NULL OR i.caseId = :caseId) " +
           "AND (:inventoryType IS NULL OR i.inventoryType = :inventoryType) " +
           "AND (:inventoryStatus IS NULL OR i.inventoryStatus = :inventoryStatus) " +
           "AND (:managementStatus IS NULL OR i.managementStatus = :managementStatus)")
    Page<Inventory> findByConditions(@Param("caseId") Long caseId,
                                 @Param("inventoryType") String inventoryType,
                                 @Param("inventoryStatus") String inventoryStatus,
                                 @Param("managementStatus") String managementStatus,
                                 Pageable pageable);

    List<Inventory> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    List<Inventory> findByPropertyIdAndIsDeleted(Long propertyId, Boolean isDeleted);
}