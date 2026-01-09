package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    /**
     * 根据角色ID查询角色权限关系
     * @param roleId 角色ID
     * @return 角色权限关系列表
     */
    List<RolePermission> findByRoleId(Long roleId);
    
    /**
     * 根据权限ID查询角色权限关系
     * @param permId 权限ID
     * @return 角色权限关系列表
     */
    List<RolePermission> findByPermId(Long permId);
    
    /**
     * 根据角色ID列表查询权限ID列表
     * @param roleIds 角色ID列表
     * @return 权限ID列表
     */
    @Query("SELECT rp.permId FROM RolePermission rp WHERE rp.roleId IN :roleIds")
    List<Long> findPermIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    /**
     * 根据权限ID列表查询角色ID列表
     * @param permIds 权限ID列表
     * @return 角色ID列表
     */
    @Query("SELECT rp.roleId FROM RolePermission rp WHERE rp.permId IN :permIds")
    List<Long> findRoleIdsByPermIds(@Param("permIds") List<Long> permIds);
}
