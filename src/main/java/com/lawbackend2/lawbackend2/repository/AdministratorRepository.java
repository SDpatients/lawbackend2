package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    Page<Administrator> findByCaseId(Long caseId, Pageable pageable);
}
