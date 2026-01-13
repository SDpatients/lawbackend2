package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.AdministratorCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffUpdateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Administrator;
import com.lawbackend2.lawbackend2.entity.AdministratorStaff;

import java.util.List;

public interface AdministratorService {

    Administrator createAdministrator(AdministratorCreateRequest request, Long userId);

    Administrator getAdministratorById(Long administratorId);

    List<Administrator> getAdministratorList(Integer pageNum, Integer pageSize, Long caseId);

    List<Administrator> getAdministratorList(Integer pageNum, Integer pageSize, Long caseId, String administratorName);

    Long getAdministratorCount(Long caseId);

    Long getAdministratorCount(Long caseId, String administratorName);

    Administrator updateAdministrator(Long administratorId, AdministratorUpdateRequest request);

    void deleteAdministrator(Long administratorId);

    AdministratorStaff createAdministratorStaff(AdministratorStaffCreateRequest request, Long userId);

    List<AdministratorStaff> getAdministratorStaffList(Long administratorId);

    AdministratorStaff getAdministratorStaffById(Long staffId);

    AdministratorStaff updateAdministratorStaff(Long staffId, AdministratorStaffUpdateRequest request);

    void deleteAdministratorStaff(Long staffId);
}
