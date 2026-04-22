package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LibDocumentFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibDocumentFolderRepository extends JpaRepository<LibDocumentFolder, Long>, JpaSpecificationExecutor<LibDocumentFolder> {

    Optional<LibDocumentFolder> findByFolderPath(String folderPath);

    boolean existsByFolderPath(String folderPath);

    boolean existsByParentIdAndFolderName(Long parentId, String folderName);

    List<LibDocumentFolder> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<LibDocumentFolder> findByParentIdIsNullOrderBySortOrderAsc();

    @Query("SELECT f FROM LibDocumentFolder f WHERE f.folderName LIKE %:keyword%")
    List<LibDocumentFolder> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(f) FROM LibDocumentFolder f WHERE f.parentId = :parentId")
    Long countByParentId(@Param("parentId") Long parentId);

    @Query("SELECT f FROM LibDocumentFolder f WHERE f.isPublic = true ORDER BY f.createTime DESC")
    List<LibDocumentFolder> findPublicFolders();

    @Query("SELECT f FROM LibDocumentFolder f ORDER BY f.folderLevel ASC, f.sortOrder ASC, f.createTime ASC")
    List<LibDocumentFolder> findAllOrderByLevelAndSort();

    @Query("SELECT f FROM LibDocumentFolder f WHERE f.folderLevel = :level ORDER BY f.sortOrder ASC, f.createTime ASC")
    List<LibDocumentFolder> findByFolderLevelOrderBySortOrder(@Param("level") Integer level);

    @Query("SELECT f.id FROM LibDocumentFolder f WHERE f.parentId = :parentId")
    List<Long> findIdsByParentId(@Param("parentId") Long parentId);

    @Query(value = "WITH RECURSIVE folder_tree AS (" +
            "SELECT id FROM tb_lib_document_folder WHERE id = :folderId " +
            "UNION ALL " +
            "SELECT f.id FROM tb_lib_document_folder f " +
            "INNER JOIN folder_tree ft ON f.parent_id = ft.id " +
            ") SELECT id FROM folder_tree", nativeQuery = true)
    List<Long> findAllDescendantIds(@Param("folderId") Long folderId);

    @Query("SELECT f FROM LibDocumentFolder f WHERE f.parentId IN :parentIds ORDER BY f.sortOrder ASC")
    List<LibDocumentFolder> findByParentIdInOrderBySortOrder(@Param("parentIds") List<Long> parentIds);

    @Modifying
    @Query("UPDATE LibDocumentFolder f SET f.sortOrder = :sortOrder WHERE f.id = :id")
    void updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    @Query("SELECT MAX(f.sortOrder) FROM LibDocumentFolder f WHERE f.parentId = :parentId")
    Integer findMaxSortOrderByParentId(@Param("parentId") Long parentId);
}
