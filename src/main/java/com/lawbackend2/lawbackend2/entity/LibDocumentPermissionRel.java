package com.lawbackend2.lawbackend2.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_lib_document_permission_rel", indexes = {
    @Index(name = "idx_lib_dpr_doc_id", columnList = "document_id"),
    @Index(name = "idx_lib_dpr_perm_id", columnList = "permission_id"),
    @Index(name = "idx_lib_dpr_target", columnList = "target_type, target_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_lib_dpr_unique", columnNames = {"document_id", "permission_id", "target_type", "target_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDocumentPermissionRel extends LibBaseEntity {

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;
}
