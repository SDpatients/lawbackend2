package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Long>, JpaSpecificationExecutor<DictionaryItem> {

    @Query("SELECT di FROM DictionaryItem di WHERE di.isDeleted = false AND di.categoryId = :categoryId ORDER BY di.sortOrder ASC, di.createTime DESC")
    List<DictionaryItem> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT di FROM DictionaryItem di WHERE di.isDeleted = false AND di.categoryId = :categoryId AND di.status = :status ORDER BY di.sortOrder ASC, di.createTime DESC")
    List<DictionaryItem> findByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") String status);

    @Query("SELECT COUNT(di) FROM DictionaryItem di WHERE di.isDeleted = false AND di.categoryId = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT di FROM DictionaryItem di WHERE di.isDeleted = false ORDER BY di.categoryId ASC, di.sortOrder ASC, di.createTime DESC")
    List<DictionaryItem> findAllActive();
}
