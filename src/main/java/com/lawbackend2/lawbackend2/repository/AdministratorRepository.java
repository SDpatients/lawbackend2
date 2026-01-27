package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    Page<Administrator> findByCaseId(Long caseId, Pageable pageable);

    @Query("SELECT a FROM Administrator a WHERE (:caseId IS NULL OR a.caseId = :caseId) AND (:administratorName IS NULL OR a.administratorName LIKE %:administratorName%)")
    Page<Administrator> findByConditions(@Param("caseId") Long caseId, @Param("administratorName") String administratorName, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Administrator a WHERE (:caseId IS NULL OR a.caseId = :caseId) AND (:administratorName IS NULL OR a.administratorName LIKE %:administratorName%)")
    Long countByConditions(@Param("caseId") Long caseId, @Param("administratorName") String administratorName);
    void deleteByCaseId(Long caseId);
}
