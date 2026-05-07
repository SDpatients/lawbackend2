package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DictionaryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryCategoryRepository extends JpaRepository<DictionaryCategory, Long>, JpaSpecificationExecutor<DictionaryCategory> {

    Optional<DictionaryCategory> findByCategoryCode(String categoryCode);

    boolean existsByCategoryCode(String categoryCode);

    @Query("SELECT dc FROM DictionaryCategory dc WHERE dc.isDeleted = false ORDER BY dc.sortOrder ASC, dc.createTime DESC")
    List<DictionaryCategory> findAllActive();

    @Query("SELECT dc FROM DictionaryCategory dc WHERE dc.isDeleted = false AND dc.status = :status ORDER BY dc.sortOrder ASC, dc.createTime DESC")
    List<DictionaryCategory> findByStatus(@Param("status") String status);

    @Query("SELECT dc FROM DictionaryCategory dc WHERE dc.isDeleted = false AND (dc.categoryCode LIKE %:keyword% OR dc.categoryName LIKE %:keyword%) ORDER BY dc.sortOrder ASC, dc.createTime DESC")
    List<DictionaryCategory> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(dc) FROM DictionaryCategory dc WHERE dc.isDeleted = false")
    Long countActive();
}
