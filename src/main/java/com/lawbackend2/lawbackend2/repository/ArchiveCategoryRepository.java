package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ArchiveCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchiveCategoryRepository extends JpaRepository<ArchiveCategory, Long> {

    List<ArchiveCategory> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<ArchiveCategory> findByStatusOrderBySortOrderAsc(String status);

    @Query("SELECT c FROM ArchiveCategory c WHERE c.parentId IS NULL ORDER BY c.sortOrder ASC")
    List<ArchiveCategory> findRootCategories();

    @Query("SELECT c FROM ArchiveCategory c WHERE c.categoryCode = :categoryCode")
    ArchiveCategory findByCategoryCode(@Param("categoryCode") String categoryCode);

    List<ArchiveCategory> findByLevelAndStatusOrderBySortOrderAsc(Integer level, String status);
}
