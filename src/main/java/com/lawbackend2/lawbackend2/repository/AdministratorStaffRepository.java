package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AdministratorStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdministratorStaffRepository extends JpaRepository<AdministratorStaff, Long> {

    List<AdministratorStaff> findByAdministratorId(Long administratorId);
}
