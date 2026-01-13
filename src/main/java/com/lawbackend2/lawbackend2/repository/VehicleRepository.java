package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNoAndIsDeleted(String vehicleNo, Boolean isDeleted);

    @Query("SELECT v FROM Vehicle v WHERE v.isDeleted = false " +
           "AND (:caseId IS NULL OR v.caseId = :caseId) " +
           "AND (:vehicleType IS NULL OR v.vehicleType = :vehicleType) " +
           "AND (:vehicleStatus IS NULL OR v.vehicleStatus = :vehicleStatus) " +
           "AND (:managementStatus IS NULL OR v.managementStatus = :managementStatus)")
    Page<Vehicle> findByConditions(@Param("caseId") Long caseId,
                               @Param("vehicleType") String vehicleType,
                               @Param("vehicleStatus") String vehicleStatus,
                               @Param("managementStatus") String managementStatus,
                               Pageable pageable);

    List<Vehicle> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    List<Vehicle> findByPropertyIdAndIsDeleted(Long propertyId, Boolean isDeleted);
}