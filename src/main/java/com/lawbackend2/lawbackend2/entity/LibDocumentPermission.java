package com.lawbackend2.lawbackend2.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_lib_document_permission", indexes = {
    @Index(name = "idx_lib_perm_type", columnList = "permission_type"),
    @Index(name = "idx_lib_perm_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_lib_perm_code", columnNames = {"permission_code"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDocumentPermission extends LibBaseEntity {

    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    @Column(name = "permission_code", nullable = false, length = 50)
    private String permissionCode;

    @Column(name = "permission_type", nullable = false, length = 20)
    private String permissionType;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}
