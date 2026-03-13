package com.lawbackend2.lawbackend2.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_lib_folder_permission", indexes = {
    @Index(name = "idx_lib_fp_folder_id", columnList = "folder_id"),
    @Index(name = "idx_lib_fp_permission_id", columnList = "permission_id"),
    @Index(name = "idx_lib_fp_target", columnList = "target_type, target_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_lib_fp_unique", columnNames = {"folder_id", "permission_id", "target_type", "target_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibFolderPermission extends LibBaseEntity {

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "is_inherit")
    @Builder.Default
    private Boolean isInherit = true;
}
