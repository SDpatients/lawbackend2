package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibDocumentPermissionRepository extends JpaRepository<LibDocumentPermission, Long>, JpaSpecificationExecutor<LibDocumentPermission> {

    Optional<LibDocumentPermission> findByPermissionCode(String permissionCode);

    List<LibDocumentPermission> findByPermissionType(String permissionType);

    @Query("SELECT p FROM LibDocumentPermission p WHERE p.isDeleted = false ORDER BY p.sortOrder ASC")
    List<LibDocumentPermission> findAllActive();
}
