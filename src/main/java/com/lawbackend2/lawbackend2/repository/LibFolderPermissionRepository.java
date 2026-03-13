package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibFolderPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibFolderPermissionRepository extends JpaRepository<LibFolderPermission, Long>, JpaSpecificationExecutor<LibFolderPermission> {

    List<LibFolderPermission> findByFolderIdAndIsDeletedFalse(Long folderId);

    List<LibFolderPermission> findByPermissionId(Long permissionId);

    @Query("SELECT fp FROM LibFolderPermission fp WHERE fp.folderId = :folderId AND fp.permissionId = :permissionId AND fp.targetType = :targetType AND fp.targetId = :targetId AND fp.isDeleted = false")
    LibFolderPermission findByFolderPermissionTarget(@Param("folderId") Long folderId, @Param("permissionId") Long permissionId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Query("SELECT fp FROM LibFolderPermission fp WHERE fp.targetType = :targetType AND fp.targetId = :targetId AND fp.isDeleted = false")
    List<LibFolderPermission> findByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Modifying
    @Query("UPDATE LibFolderPermission fp SET fp.isDeleted = true WHERE fp.folderId = :folderId")
    void softDeleteByFolderId(@Param("folderId") Long folderId);
}
