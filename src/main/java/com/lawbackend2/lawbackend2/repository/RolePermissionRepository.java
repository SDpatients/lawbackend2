package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);
    
    List<RolePermission> findByPermId(Long permId);
    
    @Query("SELECT rp.permId FROM RolePermission rp WHERE rp.roleId IN :roleIds")
    List<Long> findPermIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    @Query("SELECT rp.roleId FROM RolePermission rp WHERE rp.permId IN :permIds")
    List<Long> findRoleIdsByPermIds(@Param("permIds") List<Long> permIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.permId = :permId")
    void deleteByPermId(@Param("permId") Long permId);
}
