package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.DebtorCreateRequest;
import com.lawbackend2.lawbackend2.dto.DebtorEnterpriseResponse;
import com.lawbackend2.lawbackend2.dto.DebtorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.DebtorEnterprise;

import java.util.List;

public interface DebtorEnterpriseService {

    DebtorEnterprise createDebtor(DebtorCreateRequest request, Long userId);

    DebtorEnterprise getDebtorById(Long debtorId);

    DebtorEnterpriseResponse getDebtorByIdWithCaseInfo(Long debtorId);

    List<DebtorEnterprise> getDebtorList(Integer pageNum, Integer pageSize, Long caseId, String enterpriseName);

    Long getDebtorCount(Long caseId, String enterpriseName);

    DebtorEnterprise updateDebtor(Long debtorId, DebtorUpdateRequest request);

    void deleteDebtor(Long debtorId);

    PageResult<DebtorEnterpriseResponse> getDebtorListWithCaseInfo(Integer pageNum, Integer pageSize, Long caseId, String enterpriseName, String unifiedSocialCreditCode, String legalRepresentative);
}
