package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);

    @Query("SELECT r FROM Role r WHERE r.status = :status AND r.isDeleted = false ORDER BY r.sortOrder ASC")
    List<Role> findByStatus(@Param("status") String status);

    @Query("SELECT r FROM Role r WHERE r.isDeleted = false ORDER BY r.sortOrder ASC")
    List<Role> findAllActive();

    @Query("SELECT r FROM Role r WHERE r.roleName LIKE %:keyword% OR r.roleDesc LIKE %:keyword%")
    List<Role> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT r.roleCode FROM Role r WHERE r.id IN :roleIds AND r.isDeleted = false")
    List<String> findRoleCodesByIds(@Param("roleIds") List<Long> roleIds);
}
