package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ClaimDetailResponse;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationStatsResponse;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationUpdateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelImportResponse;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ClaimRegistrationService {
    ClaimRegistration createClaim(ClaimRegistrationCreateRequest request, Long userId);

    ClaimRegistration getClaimById(Long claimId);

    ClaimDetailResponse getClaimDetailById(Long claimId);

    List<ClaimRegistration> getClaimList(Integer pageNum, Integer pageSize, Long caseId, String registrationStatus);

    Long getClaimCount(Long caseId, String registrationStatus);

    ClaimRegistration updateClaim(Long claimId, ClaimRegistrationUpdateRequest request);

    void deleteClaim(Long claimId, Long userId);

    void updateRegistrationStatus(Long claimId, String status, Long userId);

    void receiveMaterial(Long claimId, String receiver, String completeness, Long userId);

    void rejectClaim(Long claimId, String rejectReason, Long userId);

    ExcelImportResponse importFromExcel(MultipartFile file, Long caseId, Long userId);

    ExcelImportResponse importFromExcelEasy(MultipartFile file, Long caseId, Long userId);

    ExcelImportResponse importFromDeclaredClaimsRegister(MultipartFile file, Long caseId, Long userId);

    void exportToExcel(HttpServletResponse response, Long caseId, String registrationStatus);

    ClaimRegistrationStatsResponse getClaimRegistrationStats(Long caseId);
}
