package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ExcelImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExcelImportHistoryRepository extends JpaRepository<ExcelImportHistory, Long> {
    
    List<ExcelImportHistory> findByTemplateId(Long templateId);
    
    List<ExcelImportHistory> findByImportedBy(Long importedBy);
    
    List<ExcelImportHistory> findByImportStatus(String importStatus);
    
    @Query("SELECT h FROM ExcelImportHistory h WHERE h.importedBy = :importedBy AND h.importedTime BETWEEN :startTime AND :endTime")
    List<ExcelImportHistory> findByImportedByAndTimeRange(@Param("importedBy") Long importedBy, 
                                                         @Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT h FROM ExcelImportHistory h WHERE h.importStatus = :status ORDER BY h.importedTime DESC")
    List<ExcelImportHistory> findByImportStatusOrderByImportedTimeDesc(@Param("status") String status);
}
