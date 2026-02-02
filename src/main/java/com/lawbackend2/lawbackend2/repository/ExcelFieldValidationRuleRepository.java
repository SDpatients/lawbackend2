package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ExcelFieldValidationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelFieldValidationRuleRepository extends JpaRepository<ExcelFieldValidationRule, Long> {
    
    List<ExcelFieldValidationRule> findByFieldName(String fieldName);
    
    List<ExcelFieldValidationRule> findByIsActive(Boolean isActive);
    
    List<ExcelFieldValidationRule> findByFieldNameAndIsActive(String fieldName, Boolean isActive);
    
    @Query("SELECT r FROM ExcelFieldValidationRule r WHERE r.fieldName = :fieldName AND r.isActive = true ORDER BY r.priority ASC")
    List<ExcelFieldValidationRule> findByFieldNameAndIsActiveOrderByPriority(@Param("fieldName") String fieldName);
    
    @Query("SELECT r FROM ExcelFieldValidationRule r WHERE r.isActive = true ORDER BY r.priority ASC")
    List<ExcelFieldValidationRule> findAllActiveRulesOrderByPriority();
}
