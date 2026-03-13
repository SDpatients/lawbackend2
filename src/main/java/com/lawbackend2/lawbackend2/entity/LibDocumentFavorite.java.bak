package com.lawbackend2.lawbackend2.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_lib_document_favorite", indexes = {
    @Index(name = "idx_lib_fav_doc_id", columnList = "document_id"),
    @Index(name = "idx_lib_fav_user_id", columnList = "user_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_lib_fav_doc_user", columnNames = {"document_id", "user_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDocumentFavorite extends LibBaseEntity {

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "folder_name", length = 100)
    private String folderName;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}
