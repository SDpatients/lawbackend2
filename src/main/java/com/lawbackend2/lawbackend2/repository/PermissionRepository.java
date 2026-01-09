package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermCode(String permCode);

    boolean existsByPermCode(String permCode);

    @Query("SELECT p FROM Permission p WHERE p.parentId = :parentId AND p.status = :status AND p.isDeleted = false ORDER BY p.sortOrder ASC")
    List<Permission> findByParentIdAndStatus(@Param("parentId") Long parentId, @Param("status") String status);

    @Query("SELECT p FROM Permission p WHERE p.parentId = 0 AND p.status = :status AND p.isDeleted = false ORDER BY p.sortOrder ASC")
    List<Permission> findRootPermissions(@Param("status") String status);

    @Query("SELECT p FROM Permission p WHERE p.status = :status AND p.isDeleted = false ORDER BY p.sortOrder ASC")
    List<Permission> findByStatus(@Param("status") String status);

    @Query("SELECT p FROM Permission p WHERE p.permType = :permType AND p.status = :status AND p.isDeleted = false ORDER BY p.sortOrder ASC")
    List<Permission> findByPermTypeAndStatus(@Param("permType") String permType, @Param("status") String status);

    @Query("SELECT p FROM Permission p WHERE p.permName LIKE %:keyword% OR p.permCode LIKE %:keyword%")
    List<Permission> searchByKeyword(@Param("keyword") String keyword);
}
