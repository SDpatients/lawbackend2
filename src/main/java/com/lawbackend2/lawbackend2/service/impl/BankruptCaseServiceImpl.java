package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.dto.response.UserCaseListResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import com.lawbackend2.lawbackend2.enums.CaseStatus;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.*;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BankruptCaseServiceImpl implements BankruptCaseService {

    private final BankruptCaseRepository bankruptCaseRepository;
    private final WorkTeamRepository workTeamRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final WorkTeamPermissionRepository workTeamPermissionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final com.lawbackend2.lawbackend2.service.CaseTaskService caseTaskService;
    private final ApprovalRepository approvalRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final CaseProcessStageRepository caseProcessStageRepository;
    private final DocumentDeliveryRepository documentDeliveryRepository;
    private final ArchiveRecordRepository archiveRecordRepository;
    private final CaseAnnouncementRepository caseAnnouncementRepository;
    private final AnnouncementViewRecordRepository announcementViewRecordRepository;
    private final FundReimbursementRepository fundReimbursementRepository;
    private final FundFlowRepository fundFlowRepository;
    private final FundOperationLogRepository fundOperationLogRepository;
    private final FundBudgetRepository fundBudgetRepository;
    private final EscrowManagementRepository escrowManagementRepository;
    private final FundAccountRepository fundAccountRepository;
    private final FundApprovalRepository fundApprovalRepository;
    private final DistributionDetailRepository distributionDetailRepository;
    private final DistributionExecutionRepository distributionExecutionRepository;
    private final CommonDebtRepository commonDebtRepository;
    private final ClaimConfirmationRepository claimConfirmationRepository;
    private final BankruptcyExpenseRepository bankruptcyExpenseRepository;
    private final AdministratorRepository administratorRepository;
    private final CreditorClaimRepository creditorClaimRepository;
    private final WorkPlanRepository workPlanRepository;
    private final DebtorEnterpriseRepository debtorEnterpriseRepository;
    private final CaseProgressRepository caseProgressRepository;
    private final CreditorInfoRepository creditorInfoRepository;
    private final CaseTaskRepository caseTaskRepository;
    private final CaseTaskSubmissionRepository caseTaskSubmissionRepository;
    private final WorkLogRepository workLogRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountTransactionRepository bankAccountTransactionRepository;
    private final ExpenseReimbursementRepository expenseReimbursementRepository;
    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final ClaimReviewRepository claimReviewRepository;

    public BankruptCaseServiceImpl(BankruptCaseRepository bankruptCaseRepository, 
                                 WorkTeamRepository workTeamRepository,
                                 WorkTeamMemberRepository workTeamMemberRepository,
                                 WorkTeamPermissionRepository workTeamPermissionRepository,
                                 UserRepository userRepository,
                                 UserRoleRepository userRoleRepository,
                                 RoleRepository roleRepository,
                                 com.lawbackend2.lawbackend2.service.CaseTaskService caseTaskService,
                                 ApprovalRepository approvalRepository,
                                 ApprovalHistoryRepository approvalHistoryRepository,
                                 CaseProcessStageRepository caseProcessStageRepository,
                                 DocumentDeliveryRepository documentDeliveryRepository,
                                 ArchiveRecordRepository archiveRecordRepository,
                                 CaseAnnouncementRepository caseAnnouncementRepository,
                                 AnnouncementViewRecordRepository announcementViewRecordRepository,
                                 FundReimbursementRepository fundReimbursementRepository,
                                 FundFlowRepository fundFlowRepository,
                                 FundOperationLogRepository fundOperationLogRepository,
                                 FundBudgetRepository fundBudgetRepository,
                                 EscrowManagementRepository escrowManagementRepository,
                                 FundAccountRepository fundAccountRepository,
                                 FundApprovalRepository fundApprovalRepository,
                                 DistributionDetailRepository distributionDetailRepository,
                                 DistributionExecutionRepository distributionExecutionRepository,
                                 CommonDebtRepository commonDebtRepository,
                                 ClaimConfirmationRepository claimConfirmationRepository,
                                 BankruptcyExpenseRepository bankruptcyExpenseRepository,
                                 AdministratorRepository administratorRepository,
                                 CreditorClaimRepository creditorClaimRepository,
                                 WorkPlanRepository workPlanRepository,
                                 DebtorEnterpriseRepository debtorEnterpriseRepository,
                                 CaseProgressRepository caseProgressRepository,
                                 CreditorInfoRepository creditorInfoRepository,
                                 CaseTaskRepository caseTaskRepository,
                                 CaseTaskSubmissionRepository caseTaskSubmissionRepository,
                                 WorkLogRepository workLogRepository,
                                 BankAccountRepository bankAccountRepository,
                                 BankAccountTransactionRepository bankAccountTransactionRepository,
                                 ExpenseReimbursementRepository expenseReimbursementRepository,
                                 ClaimRegistrationRepository claimRegistrationRepository,
                                 ClaimReviewRepository claimReviewRepository) {
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.workTeamRepository = workTeamRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.workTeamPermissionRepository = workTeamPermissionRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.caseTaskService = caseTaskService;
        this.approvalRepository = approvalRepository;
        this.approvalHistoryRepository = approvalHistoryRepository;
        this.caseProcessStageRepository = caseProcessStageRepository;
        this.documentDeliveryRepository = documentDeliveryRepository;
        this.archiveRecordRepository = archiveRecordRepository;
        this.caseAnnouncementRepository = caseAnnouncementRepository;
        this.announcementViewRecordRepository = announcementViewRecordRepository;
        this.fundReimbursementRepository = fundReimbursementRepository;
        this.fundFlowRepository = fundFlowRepository;
        this.fundOperationLogRepository = fundOperationLogRepository;
        this.fundBudgetRepository = fundBudgetRepository;
        this.escrowManagementRepository = escrowManagementRepository;
        this.fundAccountRepository = fundAccountRepository;
        this.fundApprovalRepository = fundApprovalRepository;
        this.distributionDetailRepository = distributionDetailRepository;
        this.distributionExecutionRepository = distributionExecutionRepository;
        this.commonDebtRepository = commonDebtRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
        this.bankruptcyExpenseRepository = bankruptcyExpenseRepository;
        this.administratorRepository = administratorRepository;
        this.creditorClaimRepository = creditorClaimRepository;
        this.workPlanRepository = workPlanRepository;
        this.debtorEnterpriseRepository = debtorEnterpriseRepository;
        this.caseProgressRepository = caseProgressRepository;
        this.creditorInfoRepository = creditorInfoRepository;
        this.caseTaskRepository = caseTaskRepository;
        this.caseTaskSubmissionRepository = caseTaskSubmissionRepository;
        this.workLogRepository = workLogRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountTransactionRepository = bankAccountTransactionRepository;
        this.expenseReimbursementRepository = expenseReimbursementRepository;
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimReviewRepository = claimReviewRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankruptCase createCase(CaseCreateRequest request, Long userId) {
        if (bankruptCaseRepository.findByCaseNumber(request.getCaseNumber()).isPresent()) {
            throw new BusinessException("案号已存在");
        }

        BankruptCase bankruptCase = new BankruptCase();
        BeanUtils.copyProperties(request, bankruptCase, "debtClaimDeadline");
        
        if (request.getDebtClaimDeadline() != null) {
            bankruptCase.setDebtClaimDeadline(request.getDebtClaimDeadline().atStartOfDay());
        }
        
        bankruptCase.setCreateUserId(userId);
        bankruptCase.setUpdateUserId(userId);
        bankruptCase.setIsSimplifiedTrial(request.getIsSimplifiedTrial() != null && request.getIsSimplifiedTrial() == 1);
        bankruptCase.setCaseStatus(CaseStatus.ONGOING.name());

        BankruptCase savedCase = bankruptCaseRepository.save(bankruptCase);
        
        caseTaskService.createTasksForCase(savedCase.getId());
        
        createInitialWorkTeam(savedCase.getId(), request.getMainResponsiblePerson(), request.getUndertakingPersonnel(), userId);

        return savedCase;
    }

    @Override
    public BankruptCase getCaseById(Long caseId) {
        return bankruptCaseRepository.findById(caseId)
                .orElseThrow(() -> new BusinessException("案件不存在"));
    }

    @Override
    public List<BankruptCase> getCaseList(Integer pageNum, Integer pageSize, String caseStatus, String caseProgress, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<BankruptCase> page;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (caseStatus != null && caseProgress != null) {
                page = bankruptCaseRepository.searchByKeywordAndStatusAndProgress(keyword.trim(), caseStatus, caseProgress, pageable);
            } else if (caseStatus != null) {
                page = bankruptCaseRepository.searchByKeywordAndCaseStatus(keyword.trim(), caseStatus, pageable);
            } else if (caseProgress != null) {
                page = bankruptCaseRepository.searchByKeywordAndCaseProgress(keyword.trim(), caseProgress, pageable);
            } else {
                page = bankruptCaseRepository.searchByKeyword(keyword.trim(), pageable);
            }
        } else {
            if (caseStatus != null && caseProgress != null) {
                page = bankruptCaseRepository.findByCaseStatusAndCaseProgress(caseStatus, caseProgress, pageable);
            } else if (caseStatus != null) {
                page = bankruptCaseRepository.findByCaseStatus(caseStatus, pageable);
            } else if (caseProgress != null) {
                page = bankruptCaseRepository.findByCaseProgress(caseProgress, pageable);
            } else {
                page = bankruptCaseRepository.findAll(pageable);
            }
        }

        return page.getContent();
    }

    @Override
    public Long getCaseCount(String caseStatus, String caseProgress, String keyword) {
        Pageable pageable = PageRequest.of(0, 1);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (caseStatus != null && caseProgress != null) {
                return bankruptCaseRepository.searchByKeywordAndStatusAndProgress(keyword.trim(), caseStatus, caseProgress, pageable).getTotalElements();
            } else if (caseStatus != null) {
                return bankruptCaseRepository.searchByKeywordAndCaseStatus(keyword.trim(), caseStatus, pageable).getTotalElements();
            } else if (caseProgress != null) {
                return bankruptCaseRepository.searchByKeywordAndCaseProgress(keyword.trim(), caseProgress, pageable).getTotalElements();
            } else {
                return bankruptCaseRepository.searchByKeyword(keyword.trim(), pageable).getTotalElements();
            }
        } else {
            if (caseStatus != null && caseProgress != null) {
                return bankruptCaseRepository.findByCaseStatusAndCaseProgress(caseStatus, caseProgress, Pageable.unpaged()).getTotalElements();
            } else if (caseStatus != null) {
                return bankruptCaseRepository.findByCaseStatus(caseStatus, Pageable.unpaged()).getTotalElements();
            } else if (caseProgress != null) {
                return bankruptCaseRepository.findByCaseProgress(caseProgress, Pageable.unpaged()).getTotalElements();
            } else {
                return bankruptCaseRepository.count();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankruptCase updateCase(Long caseId, CaseUpdateRequest request) {
        BankruptCase bankruptCase = getCaseById(caseId);

        if (request.getCaseName() != null) {
            bankruptCase.setCaseName(request.getCaseName());
        }
        if (request.getCaseReason() != null) {
            bankruptCase.setCaseReason(request.getCaseReason());
        }
        if (request.getRemarks() != null) {
            bankruptCase.setRemarks(request.getRemarks());
        }
        if (request.getFilingDate() != null) {
            bankruptCase.setFilingDate(request.getFilingDate());
        }
        if (request.getCaseProgress() != null) {
            bankruptCase.setCaseProgress(request.getCaseProgress());
        }
        if (request.getMainResponsiblePerson() != null) {
            bankruptCase.setMainResponsiblePerson(request.getMainResponsiblePerson());
        }
        if (request.getDesignatedInstitution() != null) {
            bankruptCase.setDesignatedInstitution(request.getDesignatedInstitution());
        }
        if (request.getAcceptanceCourt() != null) {
            bankruptCase.setAcceptanceCourt(request.getAcceptanceCourt());
        }
        if (request.getDebtClaimDeadline() != null) {
            // 将LocalDate转换为LocalDateTime（00:00:00）
            bankruptCase.setDebtClaimDeadline(request.getDebtClaimDeadline().atStartOfDay());
        }

        return bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCaseStatus(Long caseId, CaseStatusUpdateRequest request) {
        BankruptCase bankruptCase = getCaseById(caseId);
        bankruptCase.setCaseStatus(request.getCaseStatus());
        bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCaseProgress(Long caseId, CaseProgressUpdateRequest request) {
        BankruptCase bankruptCase = getCaseById(caseId);
        bankruptCase.setCaseProgress(request.getProgressStatus());
        bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCaseProgress(Long caseId, String caseProgress) {
        BankruptCase bankruptCase = getCaseById(caseId);
        bankruptCase.setCaseProgress(caseProgress);
        bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewCase(Long caseId, CaseReviewRequest request, Long userId) {
        log.info("案件审核, caseId: {}, reviewStatus: {}, reviewerId: {}", 
                  caseId, request.getReviewStatus(), userId);

        BankruptCase bankruptCase = getCaseById(caseId);

        if (!"PENDING".equals(bankruptCase.getReviewStatus())) {
            throw new BusinessException("案件当前状态不允许审核");
        }

        bankruptCase.setReviewStatus(request.getReviewStatus());
        bankruptCase.setReviewerId(userId);
        bankruptCase.setReviewOpinion(request.getReviewOpinion());
        bankruptCase.setReviewTime(java.time.LocalDateTime.now());
        bankruptCase.setReviewCount(bankruptCase.getReviewCount() + 1);

        if ("APPROVED".equals(request.getReviewStatus())) {
            bankruptCase.setCaseStatus(CaseStatus.COMPLETED.name());
            log.info("案件审核通过, caseId: {}", caseId);
        } else if ("REJECTED".equals(request.getReviewStatus())) {
            bankruptCase.setCaseStatus(CaseStatus.ONGOING.name());
            log.info("案件审核驳回, caseId: {}", caseId);
        }

        bankruptCaseRepository.save(bankruptCase);
        log.info("案件审核完成, caseId: {}, reviewStatus: {}", caseId, request.getReviewStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForReview(Long caseId, Long userId) {
        log.info("提交案件审核, caseId: {}, userId: {}", caseId, userId);

        BankruptCase bankruptCase = getCaseById(caseId);

        if (!"PENDING".equals(bankruptCase.getReviewStatus())) {
            throw new BusinessException("案件当前状态不允许提交审核");
        }

        bankruptCase.setReviewStatus("PENDING");
        bankruptCase.setCaseStatus(CaseStatus.AWAITING.name());
        bankruptCase.setUpdateUserId(userId);
        bankruptCaseRepository.save(bankruptCase);
        log.info("案件提交审核成功, caseId: {}", caseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawReview(Long caseId, Long userId) {
        log.info("撤销案件审核, caseId: {}, userId: {}", caseId, userId);

        BankruptCase bankruptCase = getCaseById(caseId);

        if (!"PENDING".equals(bankruptCase.getReviewStatus())) {
            throw new BusinessException("只有待审核状态的案件才能撤销审核");
        }

        bankruptCase.setReviewStatus(null);
        bankruptCase.setCaseStatus(CaseStatus.ONGOING.name());
        bankruptCase.setReviewerId(null);
        bankruptCase.setReviewOpinion(null);
        bankruptCase.setReviewTime(null);
        bankruptCase.setUpdateUserId(userId);
        bankruptCaseRepository.save(bankruptCase);
        log.info("案件撤销审核成功, caseId: {}", caseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmitForReview(Long caseId, Long userId) {
        log.info("重新提交案件审核, caseId: {}, userId: {}", caseId, userId);

        BankruptCase bankruptCase = getCaseById(caseId);

        if (!"REJECTED".equals(bankruptCase.getReviewStatus())) {
            throw new BusinessException("只有被驳回的案件才能重新提交审核");
        }

        bankruptCase.setReviewStatus("PENDING");
        bankruptCase.setCaseStatus(CaseStatus.AWAITING.name());
        bankruptCase.setReviewerId(null);
        bankruptCase.setReviewOpinion(null);
        bankruptCase.setReviewTime(null);
        bankruptCase.setUpdateUserId(userId);
        bankruptCaseRepository.save(bankruptCase);
        log.info("案件重新提交审核成功, caseId: {}", caseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchReview(CaseBatchReviewRequest request, Long userId) {
        log.info("批量审核案件, caseIds: {}, reviewStatus: {}, reviewerId: {}", 
                  request.getCaseIds(), request.getReviewStatus(), userId);

        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (Long caseId : request.getCaseIds()) {
            try {
                BankruptCase bankruptCase = getCaseById(caseId);

                if (!"PENDING".equals(bankruptCase.getReviewStatus())) {
                    log.warn("案件{}当前状态不允许审核，跳过", caseId);
                    failCount++;
                    errorMessages.add(String.format("案件%s: 当前状态不允许审核", caseId));
                    continue;
                }

                bankruptCase.setReviewStatus(request.getReviewStatus());
                bankruptCase.setReviewerId(userId);
                bankruptCase.setReviewOpinion(request.getReviewOpinion());
                bankruptCase.setReviewTime(java.time.LocalDateTime.now());
                bankruptCase.setReviewCount(bankruptCase.getReviewCount() + 1);

                if ("APPROVED".equals(request.getReviewStatus())) {
                    bankruptCase.setCaseStatus(CaseStatus.COMPLETED.name());
                } else if ("REJECTED".equals(request.getReviewStatus())) {
                    bankruptCase.setCaseStatus(CaseStatus.ONGOING.name());
                }

                bankruptCaseRepository.save(bankruptCase);
                successCount++;
                log.info("案件{}审核成功", caseId);
            } catch (BusinessException e) {
                log.error("案件{}审核失败，业务异常: {}", caseId, e.getMessage(), e);
                failCount++;
                errorMessages.add(String.format("案件%s: %s", caseId, e.getMessage()));
            } catch (Exception e) {
                log.error("案件{}审核失败，系统异常", caseId, e);
                failCount++;
                errorMessages.add(String.format("案件%s: 系统异常 - %s", caseId, e.getMessage()));
            }
        }

        log.info("批量审核完成, 成功: {}, 失败: {}", successCount, failCount);

        if (failCount > 0) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append(String.format("批量审核完成，成功%d个，失败%d个。失败详情：", successCount, failCount));
            for (int i = 0; i < errorMessages.size(); i++) {
                errorMsg.append("\n").append(i + 1).append(". ").append(errorMessages.get(i));
            }
            throw new BusinessException(errorMsg.toString());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeReview(Long caseId, Long userId) {
        log.info("撤销审核结果, caseId: {}, userId: {}", caseId, userId);

        BankruptCase bankruptCase = getCaseById(caseId);

        if (!"APPROVED".equals(bankruptCase.getReviewStatus()) && !"REJECTED".equals(bankruptCase.getReviewStatus())) {
            throw new BusinessException("只有已审核的案件才能撤销审核结果");
        }

        String oldReviewStatus = bankruptCase.getReviewStatus();
        bankruptCase.setReviewStatus("PENDING");
        bankruptCase.setCaseStatus(CaseStatus.AWAITING.name());
        bankruptCase.setReviewerId(null);
        bankruptCase.setReviewOpinion(null);
        bankruptCase.setReviewTime(null);
        bankruptCase.setUpdateUserId(userId);
        bankruptCaseRepository.save(bankruptCase);
        log.info("案件{}的审核结果已撤销，原状态: {}", caseId, oldReviewStatus);
    }

    @Override
    public BankruptCase getReviewStatus(Long caseId) {
        log.debug("查询案件审核状态, caseId: {}", caseId);
        BankruptCase bankruptCase = getCaseById(caseId);
        return bankruptCase;
    }

    @Override
    public List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> getCaseSimpleList(Long userId, Integer page, Integer size, String caseNumber) {
        // 检查用户是否为 ADMIN 角色
        boolean isAdmin = isUserAdmin(userId);
        
        if (isAdmin) {
            // ADMIN 用户可以查看所有案件
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> result;
            
            if (caseNumber != null && !caseNumber.isEmpty()) {
                result = bankruptCaseRepository.findAllByCaseNumberLike(caseNumber, pageable);
            } else {
                result = bankruptCaseRepository.findAllSimpleInfo(pageable);
            }
            
            return result.getContent();
        }
        
        // 非 ADMIN 用户，查询用户参与的所有案件 ID（通过工作组成员关系）
        List<Long> participatedCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
        
        // 查询用户创建的所有案件 ID
        List<BankruptCase> createdCases = bankruptCaseRepository.findByCreateUserId(userId, Pageable.unpaged()).getContent();
        List<Long> createdCaseIds = createdCases.stream()
                .map(BankruptCase::getId)
                .collect(Collectors.toList());
        
        // 合并并去重所有案件 ID
        Set<Long> allCaseIdsSet = participatedCaseIds.stream()
                .collect(Collectors.toSet());
        allCaseIdsSet.addAll(createdCaseIds);
        
        List<Long> allCaseIds = new ArrayList<>(allCaseIdsSet);
        
        if (allCaseIds.isEmpty()) {
            return List.of();
        }
        
        // 根据案件 ID 列表和案号条件查询案件简单信息，支持分页
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> result;
        
        if (caseNumber != null && !caseNumber.isEmpty()) {
            result = bankruptCaseRepository.findSimpleInfoByIdInAndCaseNumberLike(allCaseIds, caseNumber, pageable);
        } else {
            result = bankruptCaseRepository.findSimpleInfoByIdIn(allCaseIds, pageable);
        }
        
        return result.getContent();
    }

    @Override
    public Long getCaseSimpleCount(Long userId, String caseNumber) {
        // 检查用户是否为 ADMIN 角色
        boolean isAdmin = isUserAdmin(userId);
        
        if (isAdmin) {
            // ADMIN 用户可以查看所有案件
            if (caseNumber != null && !caseNumber.isEmpty()) {
                return bankruptCaseRepository.countByCaseNumberLike(caseNumber);
            } else {
                return bankruptCaseRepository.count();
            }
        }
        
        // 非 ADMIN 用户，查询用户参与的所有案件 ID（通过工作组成员关系）
        List<Long> participatedCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
        
        // 查询用户创建的所有案件 ID
        List<BankruptCase> createdCases = bankruptCaseRepository.findByCreateUserId(userId, Pageable.unpaged()).getContent();
        List<Long> createdCaseIds = createdCases.stream()
                .map(BankruptCase::getId)
                .collect(Collectors.toList());
        
        // 合并并去重所有案件 ID
        Set<Long> allCaseIdsSet = participatedCaseIds.stream()
                .collect(Collectors.toSet());
        allCaseIdsSet.addAll(createdCaseIds);
        
        List<Long> allCaseIds = new ArrayList<>(allCaseIdsSet);
        
        if (allCaseIds.isEmpty()) {
            return 0L;
        }
        
        // 根据案件 ID 列表和案号条件查询案件简单信息总数
        Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> result;
        
        if (caseNumber != null && !caseNumber.isEmpty()) {
            result = bankruptCaseRepository.findSimpleInfoByIdInAndCaseNumberLike(allCaseIds, caseNumber, Pageable.unpaged());
        } else {
            result = bankruptCaseRepository.findSimpleInfoByIdIn(allCaseIds, Pageable.unpaged());
        }
        
        return result.getTotalElements();
    }

    @Override
    public List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> getCaseSimpleInfoByCaseNumber(Integer page, Integer size, String caseNumber) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> result = bankruptCaseRepository.findSimpleInfoByCaseNumber(caseNumber, pageable);
        return result.getContent();
    }

    @Override
    public Long countByCaseNumberLike(String caseNumber) {
        return bankruptCaseRepository.countByCaseNumberLike(caseNumber);
    }

    @Override
    public List<BankruptCase> getUserCaseList(Long userId, Integer pageNum, Integer pageSize, String caseStatus, String caseNumber) {
        // 1. 查询用户参与的所有案件ID（通过工作组成员关系）
        List<Long> participatedCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
        
        // 2. 查询用户创建的所有案件ID
        List<BankruptCase> createdCases = bankruptCaseRepository.findByCreateUserId(userId, Pageable.unpaged()).getContent();
        List<Long> createdCaseIds = createdCases.stream()
                .map(BankruptCase::getId)
                .collect(Collectors.toList());
        
        // 3. 合并并去重所有案件ID
        Set<Long> allCaseIdsSet = participatedCaseIds.stream()
                .collect(Collectors.toSet());
        allCaseIdsSet.addAll(createdCaseIds);
        
        List<Long> allCaseIds = new ArrayList<>(allCaseIdsSet);
        
        if (allCaseIds.isEmpty()) {
            return List.of();
        }
        
        // 4. 根据案件ID列表和其他条件查询案件详情，支持分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BankruptCase> page;
        
        if (caseStatus != null && caseNumber != null && !caseNumber.isEmpty()) {
            page = bankruptCaseRepository.findByIdInAndCaseStatusAndCaseNumberLike(allCaseIds, caseStatus, caseNumber, pageable);
        } else if (caseStatus != null) {
            page = bankruptCaseRepository.findByIdInAndCaseStatus(allCaseIds, caseStatus, pageable);
        } else if (caseNumber != null && !caseNumber.isEmpty()) {
            page = bankruptCaseRepository.findByIdInAndCaseNumberLike(allCaseIds, caseNumber, pageable);
        } else {
            page = bankruptCaseRepository.findByIdIn(allCaseIds, pageable);
        }
        
        // 5. 批量查询创建者信息并设置 creatorName
        List<BankruptCase> caseList = page.getContent();
        if (!caseList.isEmpty()) {
            List<Long> creatorIds = caseList.stream()
                    .map(BankruptCase::getCreateUserId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            
            if (!creatorIds.isEmpty()) {
                List<User> users = userRepository.findAllById(creatorIds);
                Map<Long, String> userIdToNameMap = users.stream()
                        .collect(Collectors.toMap(User::getId, User::getRealName));
                
                caseList.forEach(c -> {
                    if (c.getCreateUserId() != null) {
                        c.setCreatorName(userIdToNameMap.get(c.getCreateUserId()));
                    }
                });
            }
        }
        
        // 6. 直接返回BankruptCase对象列表，包含案件所有信息
        return caseList;
    }

    @Override
    public Long getUserCaseCount(Long userId, String caseStatus, String caseNumber) {
        // 1. 查询用户参与的所有案件ID（通过工作组成员关系）
        List<Long> participatedCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
        
        // 2. 查询用户创建的所有案件ID
        List<BankruptCase> createdCases = bankruptCaseRepository.findByCreateUserId(userId, Pageable.unpaged()).getContent();
        List<Long> createdCaseIds = createdCases.stream()
                .map(BankruptCase::getId)
                .collect(Collectors.toList());
        
        // 3. 合并并去重所有案件ID
        Set<Long> allCaseIdsSet = participatedCaseIds.stream()
                .collect(Collectors.toSet());
        allCaseIdsSet.addAll(createdCaseIds);
        
        List<Long> allCaseIds = new ArrayList<>(allCaseIdsSet);
        
        if (allCaseIds.isEmpty()) {
            return 0L;
        }
        
        // 4. 根据案件ID列表和其他条件查询案件数量
        if (caseStatus != null && caseNumber != null && !caseNumber.isEmpty()) {
            return bankruptCaseRepository.countByIdInAndCaseStatusAndCaseNumberLike(allCaseIds, caseStatus, caseNumber);
        } else if (caseStatus != null) {
            return bankruptCaseRepository.countByIdInAndCaseStatus(allCaseIds, caseStatus);
        } else if (caseNumber != null && !caseNumber.isEmpty()) {
            return bankruptCaseRepository.countByIdInAndCaseNumberLike(allCaseIds, caseNumber);
        } else {
            return bankruptCaseRepository.countByIdIn(allCaseIds);
        }
    }

    @Override
    public List<BankruptCase> getCasesByReviewStatus(String reviewStatus, Integer pageNum, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BankruptCase> page;

        if (keyword != null && !keyword.isEmpty()) {
            page = bankruptCaseRepository.findByReviewStatusAndKeyword(reviewStatus, keyword, pageable);
        } else {
            page = bankruptCaseRepository.findByReviewStatus(reviewStatus, pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getCasesCountByReviewStatus(String reviewStatus, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return bankruptCaseRepository.findByReviewStatusAndKeyword(reviewStatus, keyword, Pageable.unpaged()).getTotalElements();
        } else {
            return bankruptCaseRepository.countByReviewStatus(reviewStatus);
        }
    }

    @Override
    public List<BankruptCase> getCasesByReviewerId(Long reviewerId, Integer pageNum, Integer pageSize, String reviewStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "reviewTime"));
        Page<BankruptCase> page;

        if (reviewStatus != null) {
            page = bankruptCaseRepository.findByReviewerIdAndReviewStatus(reviewerId, reviewStatus, pageable);
        } else {
            page = bankruptCaseRepository.findByReviewerId(reviewerId, pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getCasesCountByReviewerId(Long reviewerId, String reviewStatus) {
        if (reviewStatus != null) {
            return bankruptCaseRepository.countByReviewerIdAndReviewStatus(reviewerId, reviewStatus);
        } else {
            return bankruptCaseRepository.countByReviewerId(reviewerId);
        }
    }

    @Override
    public List<Object[]> getReviewStatusStatistics() {
        return bankruptCaseRepository.countByReviewStatusGroup();
    }

    @Override
    public com.lawbackend2.lawbackend2.dto.MyCaseStatisticsResponse getMyCaseStatistics(Long userId) {
        log.info("查询当前用户的案件统计数据, userId: {}", userId);

        com.lawbackend2.lawbackend2.dto.MyCaseStatisticsResponse response = 
            new com.lawbackend2.lawbackend2.dto.MyCaseStatisticsResponse();

        // 查询所有案件数量
        Long totalCount = bankruptCaseRepository.countByCreateUserId(userId);
        response.setTotalCases(totalCount);

        // 查询进行中案件数量
        List<Object[]> statusGroup = bankruptCaseRepository.countByUserIdAndStatusGroup(userId);
        long inProgressCount = 0;
        long completedCount = 0;
        
        for (Object[] row : statusGroup) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            
            CaseStatus caseStatus = CaseStatus.fromString(status);
            if (caseStatus != null) {
                // 进行中的状态
                if (caseStatus.isInProgress()) {
                    inProgressCount += count;
                }
                
                // 已结案的状态
                if (caseStatus.isFinished()) {
                    completedCount += count;
                }
            }
        }
        
        response.setInProgressCases(inProgressCount);
        response.setCompletedCases(completedCount);

        log.info("当前用户的案件统计数据: 总数={}, 进行中={}, 已结案={}", 
            response.getTotalCases(), response.getInProgressCases(), response.getCompletedCases());
        
        return response;
    }

    @Override
    public com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse getCaseRelatedData(Long caseId) {
        log.info("查询案件关联数据, caseId: {}", caseId);

        BankruptCase bankruptCase = getCaseById(caseId);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse response = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse();

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.CaseInfo caseInfo = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.CaseInfo();
        caseInfo.setId(bankruptCase.getId());
        caseInfo.setCaseNumber(bankruptCase.getCaseNumber());
        caseInfo.setCaseName(bankruptCase.getCaseName());
        caseInfo.setCaseStatus(bankruptCase.getCaseStatus());
        response.setCaseInfo(caseInfo);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ApprovalData approvalData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ApprovalData();
        approvalData.setApprovalCount(approvalRepository.findByCaseId(caseId).size());
        approvalData.setApprovalHistoryCount(approvalHistoryRepository.findByCaseId(caseId).size());
        response.setApprovalData(approvalData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ProcessData processData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ProcessData();
        processData.setProcessStageCount(caseProcessStageRepository.findByCaseId(caseId).size());
        response.setProcessData(processData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.DocumentData documentData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.DocumentData();
        documentData.setDocumentDeliveryCount((int) documentDeliveryRepository.findByCaseId(caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        response.setDocumentData(documentData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ArchiveData archiveData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ArchiveData();
        archiveData.setArchiveRecordCount(archiveRecordRepository.findByCaseId(caseId).size());
        response.setArchiveData(archiveData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.AnnouncementData announcementData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.AnnouncementData();
        announcementData.setAnnouncementCount((int) caseAnnouncementRepository.findByCaseId(caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        announcementData.setAnnouncementViewCount((int) announcementViewRecordRepository.findByCaseId(caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        response.setAnnouncementData(announcementData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.FundData fundData =
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.FundData();
        fundData.setFundReimbursementCount(fundReimbursementRepository.findByCaseId(caseId).size());
        fundData.setFundFlowCount(fundFlowRepository.findByCaseId(caseId).size());
        fundData.setFundOperationLogCount((int) fundOperationLogRepository.findByConditions(caseId, null, null,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        fundData.setFundBudgetCount(fundBudgetRepository.findByCaseId(caseId).size());
        fundData.setEscrowManagementCount(escrowManagementRepository.findByCaseId(caseId).size());
        fundData.setFundAccountCount(fundAccountRepository.findByCaseId(caseId).size());
        fundData.setFundApprovalCount(fundApprovalRepository.findByCaseId(caseId).size());
        fundData.setBankruptcyExpenseCount(bankruptcyExpenseRepository.findByCaseId(caseId).size());
        response.setFundData(fundData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.DistributionData distributionData =
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.DistributionData();
        distributionData.setDistributionDetailCount(distributionDetailRepository.findByCaseId(caseId).size());
        distributionData.setDistributionExecutionCount(distributionExecutionRepository.findByCaseId(caseId).size());
        response.setDistributionData(distributionData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.DebtData debtData =
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.DebtData();
        debtData.setCommonDebtCount(commonDebtRepository.findByCaseId(caseId).size());
        response.setDebtData(debtData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ClaimData claimData =
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.ClaimData();
        claimData.setClaimConfirmationCount((int) claimConfirmationRepository.findByCaseId(caseId,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        claimData.setCreditorClaimCount((int) creditorClaimRepository.findByCaseId(caseId,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        claimData.setCreditorInfoCount((int) creditorInfoRepository.findByCaseId(caseId,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        claimData.setClaimRegistrationCount((int) claimRegistrationRepository.findByCaseId(caseId,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        claimData.setClaimReviewCount((int) claimReviewRepository.findByCaseId(caseId,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        response.setClaimData(claimData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.WorkData workData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.WorkData();
        workData.setAdministratorCount((int) administratorRepository.findByCaseId(caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        workData.setWorkTeamCount((int) workTeamRepository.findByConditions(caseId, null, null, null, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        workData.setWorkPlanCount(workPlanRepository.findByCaseId(caseId).size());
        workData.setWorkLogCount((int) workLogRepository.findByConditions(caseId, null, null, null, null, null, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        workData.setCaseProgressCount((int) caseProgressRepository.findByCaseId(caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        response.setWorkData(workData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.EnterpriseData enterpriseData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.EnterpriseData();
        enterpriseData.setDebtorEnterpriseCount((int) debtorEnterpriseRepository.findByCaseId(caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        response.setEnterpriseData(enterpriseData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.TaskData taskData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.TaskData();
        taskData.setCaseTaskCount(caseTaskRepository.findByCaseId(caseId).size());
        taskData.setCaseTaskSubmissionCount(caseTaskSubmissionRepository.findByCaseId(caseId).size());
        response.setTaskData(taskData);

        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.AccountData accountData = 
            new com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse.AccountData();
        accountData.setBankAccountCount((int) bankAccountRepository.findByConditions(null, null, null, caseId, 
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        response.setAccountData(accountData);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCase(Long caseId) {
        log.info("开始硬删除案件及其关联数据, caseId: {}", caseId);

        BankruptCase bankruptCase = getCaseById(caseId);

        approvalHistoryRepository.deleteByCaseId(caseId);
        approvalRepository.deleteByCaseId(caseId);
        caseProcessStageRepository.deleteByCaseId(caseId);
        documentDeliveryRepository.deleteByCaseId(caseId);
        archiveRecordRepository.deleteByCaseId(caseId);
        caseAnnouncementRepository.deleteByCaseId(caseId);
        announcementViewRecordRepository.deleteByCaseId(caseId);
        fundReimbursementRepository.deleteByCaseId(caseId);
        fundFlowRepository.deleteByCaseId(caseId);
        fundOperationLogRepository.deleteByCaseId(caseId);
        fundBudgetRepository.deleteByCaseId(caseId);
        escrowManagementRepository.deleteByCaseId(caseId);
        fundAccountRepository.deleteByCaseId(caseId);
        fundApprovalRepository.deleteByCaseId(caseId);
        distributionDetailRepository.deleteByCaseId(caseId);
        distributionExecutionRepository.deleteByCaseId(caseId);
        commonDebtRepository.deleteByCaseId(caseId);
        claimConfirmationRepository.deleteByCaseId(caseId);
        bankruptcyExpenseRepository.deleteByCaseId(caseId);
        administratorRepository.deleteByCaseId(caseId);
        creditorClaimRepository.deleteByCaseId(caseId);
        workTeamPermissionRepository.deleteByCaseId(caseId);
        workTeamMemberRepository.deleteByCaseId(caseId);
        workTeamRepository.deleteByCaseId(caseId);
        workPlanRepository.deleteByCaseId(caseId);
        debtorEnterpriseRepository.deleteByCaseId(caseId);
        caseProgressRepository.deleteByCaseId(caseId);
        creditorInfoRepository.deleteByCaseId(caseId);
        caseTaskSubmissionRepository.deleteByCaseId(caseId);
        caseTaskRepository.deleteByCaseId(caseId);
        workLogRepository.deleteByCaseId(caseId);
        bankAccountTransactionRepository.deleteByCaseId(caseId);
        bankAccountRepository.deleteByCaseId(caseId);
        expenseReimbursementRepository.deleteByCaseId(caseId);
        claimRegistrationRepository.deleteByCaseId(caseId);
        claimReviewRepository.deleteByCaseId(caseId);

        bankruptCaseRepository.delete(bankruptCase);

        log.info("案件及其关联数据硬删除成功，caseId: {}", caseId);
    }

    /**
     * 创建案件时自动创建初始工作团队
     * @param caseId 案件 ID
     * @param mainResponsiblePerson 主要负责人姓名
     * @param undertakingPersonnel 承办人员姓名
     * @param createUserId 创建人 ID
     */
    private void createInitialWorkTeam(Long caseId, String mainResponsiblePerson, String undertakingPersonnel, Long createUserId) {
        try {
            // 1. 查询用户 ID
            Long mainResponsibleUserId = findUserIdByRealName(mainResponsiblePerson);
            Long undertakingUserId = findUserIdByRealName(undertakingPersonnel);
            
            if (mainResponsibleUserId == null) {
                log.warn("未找到主要负责人：{}", mainResponsiblePerson);
                mainResponsibleUserId = createUserId; // 如果找不到，使用创建人
            }
            
            if (undertakingUserId == null) {
                log.warn("未找到承办人员：{}", undertakingPersonnel);
                undertakingUserId = createUserId; // 如果找不到，使用创建人
            }
            
            // 2. 创建工作团队
            WorkTeam workTeam = new WorkTeam();
            workTeam.setTeamName("初始团队");
            workTeam.setTeamLeaderId(undertakingUserId); // 负责人默认为承办人员
            workTeam.setCaseId(caseId);
            workTeam.setTeamDescription("此为系统在新增案件后自动创建的工作团队");
            workTeam.setCreateUserId(createUserId);
            workTeam.setUpdateUserId(createUserId);
            
            WorkTeam savedTeam = workTeamRepository.save(workTeam);
            log.info("创建初始工作团队成功，teamId: {}, caseId: {}", savedTeam.getId(), caseId);
            
            // 3. 添加团队成员（主要负责人、承办人员、创建人）
            java.util.Set<Long> memberUserIds = new java.util.HashSet<>();
            memberUserIds.add(mainResponsibleUserId);
            memberUserIds.add(undertakingUserId);
            memberUserIds.add(createUserId);
            
            for (Long userId : memberUserIds) {
                WorkTeamMember member = new WorkTeamMember();
                member.setTeamId(savedTeam.getId());
                member.setCaseId(caseId);
                member.setUserId(userId);
                
                // 设置角色为 LEADER
                member.setTeamRole("LEADER");
                
                // 设置权限级别为 ADMIN
                member.setPermissionLevel("ADMIN");
                member.setIsActive(1);
                member.setCreateUserId(createUserId);
                member.setUpdateUserId(createUserId);
                
                WorkTeamMember savedMember = workTeamMemberRepository.save(member);
                log.info("添加工作团队成员成功，memberId: {}, userId: {}, role: {}", 
                        savedMember.getId(), userId, member.getTeamRole());
                
                // 4. 为成员分配权限
                assignFullPermissionsToMember(savedMember.getId(), createUserId);
            }
            
            log.info("初始工作团队创建完成，teamId: {}, 成员数：{}", savedTeam.getId(), memberUserIds.size());
            
        } catch (Exception e) {
            log.error("创建初始工作团队失败，caseId: {}, error: {}", caseId, e.getMessage(), e);
            // 不抛出异常，避免影响案件创建
        }
    }
    
    /**
     * 根据真实姓名查询用户 ID
     */
    private Long findUserIdByRealName(String realName) {
        if (realName == null || realName.trim().isEmpty()) {
            return null;
        }
        
        try {
            java.util.List<User> users = userRepository.findAll();
            for (User user : users) {
                if (realName.equals(user.getRealName()) && !user.getIsDeleted()) {
                    return user.getId();
                }
            }
        } catch (Exception e) {
            log.error("根据姓名查询用户失败，realName: {}, error: {}", realName, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 为团队成员分配完整权限
     */
    private void assignFullPermissionsToMember(Long teamMemberId, Long createUserId) {
        // 定义所有模块类型
        String[] moduleTypes = {
            "CASE_TASK", "CLAIM_REGISTRATION", "CLAIM_REVIEW", "CLAIM_CONFIRMATION",
            "WORK_PLAN", "WORK_LOG", "CASE_PROGRESS", "DEBTOR_ENTERPRISE",
            "CREDITOR_INFO", "BANK_ACCOUNT", "FUND_ACCOUNT", "FUND_OPERATION",
            "CASE_ANNOUNCEMENT", "DOCUMENT_DELIVERY", "ARCHIVE_RECORD", "CASE_FILE"
        };
        
        // 定义所有权限类型
        String[] permissionTypes = {"VIEW", "CREATE", "EDIT", "DELETE", "EXPORT", "APPROVE"};
        
        for (String moduleType : moduleTypes) {
            for (String permissionType : permissionTypes) {
                WorkTeamPermission permission = new WorkTeamPermission();
                permission.setTeamMemberId(teamMemberId);
                permission.setModuleType(moduleType);
                permission.setPermissionType(permissionType);
                permission.setIsAllowed(1); // 允许
                permission.setCreateUserId(createUserId);
                permission.setUpdateUserId(createUserId);
                
                workTeamPermissionRepository.save(permission);
            }
        }
        
        log.info("为团队成员分配完整权限成功，teamMemberId: {}", teamMemberId);
    }

    /**
     * 检查用户是否为 ADMIN 角色
     */
    private boolean isUserAdmin(Long userId) {
        try {
            List<Long> roleIds = userRoleRepository.findByUserId(userId).stream()
                    .map(ur -> ur.getRoleId())
                    .collect(Collectors.toList());
            
            if (roleIds.isEmpty()) {
                return false;
            }
            
            List<Role> roles = roleRepository.findAllById(roleIds);
            return roles.stream()
                    .anyMatch(role -> "ADMIN".equals(role.getRoleCode()));
        } catch (Exception e) {
            log.error("检查用户角色失败，userId: {}, error: {}", userId, e.getMessage(), e);
            return false;
        }
    }
}
