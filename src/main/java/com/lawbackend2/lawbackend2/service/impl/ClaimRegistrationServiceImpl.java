package com.lawbackend2.lawbackend2.service.impl;

import com.alibaba.excel.EasyExcel;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.listener.ClaimRegistrationExcelImportDTO;
import com.lawbackend2.lawbackend2.listener.ClaimRegistrationExcelListener;
import com.lawbackend2.lawbackend2.listener.DeclaredClaimsRegisterExcelImportDTO;
import com.lawbackend2.lawbackend2.listener.DeclaredClaimsRegisterExcelListener;
import com.lawbackend2.lawbackend2.repository.ClaimConfirmationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.util.ExcelImportUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ClaimRegistrationServiceImpl implements ClaimRegistrationService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClaimRegistrationServiceImpl.class);

    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final ClaimReviewRepository claimReviewRepository;
    private final ClaimConfirmationRepository claimConfirmationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CreditorInfoRepository creditorInfoRepository;

    @Autowired
    public ClaimRegistrationServiceImpl(ClaimRegistrationRepository claimRegistrationRepository,
                                      ClaimReviewRepository claimReviewRepository,
                                      ClaimConfirmationRepository claimConfirmationRepository,
                                      UserRepository userRepository,
                                      NotificationService notificationService,
                                      CreditorInfoRepository creditorInfoRepository) {
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimReviewRepository = claimReviewRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.creditorInfoRepository = creditorInfoRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimRegistration createClaim(ClaimRegistrationCreateRequest request, Long userId) {
        // 验证请求数据
        validateCreateRequest(request);

        // 自动创建或检查债权人
        createOrCheckCreditor(request, userId);

        ClaimRegistration claimRegistration = new ClaimRegistration();
        BeanUtils.copyProperties(request, claimRegistration);

        claimRegistration.setCreateUserId(userId);
        claimRegistration.setUpdateUserId(userId);
        claimRegistration.setHasCourtJudgment(request.getHasCourtJudgment() != null && request.getHasCourtJudgment() == 1);
        claimRegistration.setHasExecution(request.getHasExecution() != null && request.getHasExecution() == 1);
        claimRegistration.setHasCollateral(request.getHasCollateral() != null && request.getHasCollateral() == 1);

        if (claimRegistration.getRegistrationDate() == null) {
            claimRegistration.setRegistrationDate(LocalDateTime.now());
        }

        String claimNo = generateClaimNo();
        claimRegistration.setClaimNo(claimNo);

        ClaimRegistration saved = claimRegistrationRepository.save(claimRegistration);

        User user = userRepository.findById(userId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 完成了债权登记：%s", realName, saved.getCreditorName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "债权登记通知",
                content,
                "CLAIM_REGISTRATION",
                saved.getId(),
                "ClaimRegistration",
                userId,
                realName
        );

        logger.info("债权申报创建成功, claimId: {}, claimNo: {}", saved.getId(), claimNo);
        return saved;
    }

    /**
     * 根据债权人名称检查并自动创建债权人
     * 如果该案件下已存在相同名称的债权人，则不创建
     * 如果不存在，则自动创建新的债权人记录
     */
    private void createOrCheckCreditor(ClaimRegistrationCreateRequest request, Long userId) {
        String creditorName = request.getCreditorName();
        Long caseId = request.getCaseId();

        if (creditorName == null || creditorName.trim().isEmpty() || caseId == null) {
            return;
        }

        // 查询该案件下是否已存在相同名称的债权人
        List<CreditorInfo> existingCreditors = creditorInfoRepository.findAll((root, query, cb) -> {
            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("caseId"), caseId));
            predicates.add(cb.equal(root.get("creditorName"), creditorName));
            predicates.add(cb.equal(root.get("isDeleted"), false));
            return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        if (!existingCreditors.isEmpty()) {
            logger.info("该案件下已存在相同名称的债权人，无需创建, caseId: {}, creditorName: {}", caseId, creditorName);
            return;
        }

        // 创建新的债权人
        CreditorInfo creditorInfo = new CreditorInfo();
        creditorInfo.setCaseId(caseId);
        creditorInfo.setCreditorName(creditorName);
        creditorInfo.setCreditorType(request.getCreditorType());
        creditorInfo.setLegalRepresentative(request.getLegalRepresentative());
        creditorInfo.setIdNumber(request.getCreditCode());
        creditorInfo.setAddress(request.getServiceAddress());
        creditorInfo.setCreditorStatus(CreditorStatus.KNOWN);
        creditorInfo.setCreateUserId(userId);
        creditorInfo.setUpdateUserId(userId);

        CreditorInfo savedCreditor = creditorInfoRepository.save(creditorInfo);
        logger.info("自动创建债权人成功, creditorId: {}, caseId: {}, creditorName: {}",
                savedCreditor.getId(), caseId, creditorName);
    }

    @Override
    public ClaimRegistration getClaimById(Long claimId) {
        return claimRegistrationRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException("债权申报不存在"));
    }

    @Override
    public ClaimDetailResponse getClaimDetailById(Long claimId) {
        ClaimRegistration registration = getClaimById(claimId);
        
        ClaimDetailResponse response = new ClaimDetailResponse();
        BeanUtils.copyProperties(registration, response);
        
        Optional<ClaimReview> reviewOpt = claimReviewRepository.findFirstByClaimRegistrationIdAndIsDeletedFalseOrderByReviewRoundDesc(claimId);
        if (reviewOpt.isPresent()) {
            ClaimReview review = reviewOpt.get();
            ClaimDetailResponse.ClaimReviewInfo reviewInfo = new ClaimDetailResponse.ClaimReviewInfo();
            BeanUtils.copyProperties(review, reviewInfo);
            response.setReviewInfo(reviewInfo);
        } else {
            response.setReviewInfo(null);
        }
        
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimId);
        if (!confirmations.isEmpty()) {
            ClaimConfirmation confirmation = confirmations.get(0);
            ClaimDetailResponse.ClaimConfirmationInfo confirmationInfo = new ClaimDetailResponse.ClaimConfirmationInfo();
            BeanUtils.copyProperties(confirmation, confirmationInfo);
            response.setConfirmationInfo(confirmationInfo);
        } else {
            response.setConfirmationInfo(null);
        }
        
        return response;
    }

    @Override
    public List<ClaimRegistration> getClaimList(Integer pageNum, Integer pageSize, Long caseId, String registrationStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (registrationStatus == null || registrationStatus.trim().isEmpty()) {
            registrationStatus = "PENDING";
        }

        Page<ClaimRegistration> page;
        if (caseId != null && registrationStatus != null) {
            page = claimRegistrationRepository.findByCaseIdAndRegistrationStatusAndIsDeletedFalse(caseId, registrationStatus, pageable);
        } else if (caseId != null) {
            page = claimRegistrationRepository.findByCaseIdAndIsDeletedFalse(caseId, pageable);
        } else if (registrationStatus != null) {
            page = claimRegistrationRepository.findByRegistrationStatusAndIsDeletedFalse(registrationStatus, pageable);
        } else {
            page = claimRegistrationRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getClaimCount(Long caseId, String registrationStatus) {
        if (registrationStatus == null || registrationStatus.trim().isEmpty()) {
            registrationStatus = "PENDING";
        }

        if (caseId != null && registrationStatus != null) {
            return claimRegistrationRepository.findByCaseIdAndRegistrationStatusAndIsDeletedFalse(caseId, registrationStatus, Pageable.unpaged()).getTotalElements();
        } else if (caseId != null) {
            return claimRegistrationRepository.findByCaseIdAndIsDeletedFalse(caseId, Pageable.unpaged()).getTotalElements();
        } else if (registrationStatus != null) {
            return claimRegistrationRepository.findByRegistrationStatusAndIsDeletedFalse(registrationStatus, Pageable.unpaged()).getTotalElements();
        } else {
            return claimRegistrationRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimRegistration updateClaim(Long claimId, ClaimRegistrationUpdateRequest request) {
        ClaimRegistration claimRegistration = getClaimById(claimId);

        if (request.getCreditorName() != null) {
            claimRegistration.setCreditorName(request.getCreditorName());
        }
        if (request.getCreditorType() != null) {
            claimRegistration.setCreditorType(request.getCreditorType());
        }
        if (request.getCreditCode() != null) {
            claimRegistration.setCreditCode(request.getCreditCode());
        }
        if (request.getLegalRepresentative() != null) {
            claimRegistration.setLegalRepresentative(request.getLegalRepresentative());
        }
        if (request.getServiceAddress() != null) {
            claimRegistration.setServiceAddress(request.getServiceAddress());
        }
        if (request.getAgentName() != null) {
            claimRegistration.setAgentName(request.getAgentName());
        }
        if (request.getAgentPhone() != null) {
            claimRegistration.setAgentPhone(request.getAgentPhone());
        }
        if (request.getAgentIdCard() != null) {
            claimRegistration.setAgentIdCard(request.getAgentIdCard());
        }
        if (request.getAgentAddress() != null) {
            claimRegistration.setAgentAddress(request.getAgentAddress());
        }
        if (request.getAccountName() != null) {
            claimRegistration.setAccountName(request.getAccountName());
        }
        if (request.getCreditorBankAccount() != null) {
            claimRegistration.setCreditorBankAccount(request.getCreditorBankAccount());
        }
        if (request.getBankName() != null) {
            claimRegistration.setBankName(request.getBankName());
        }
        if (request.getPrincipal() != null) {
            claimRegistration.setPrincipal(request.getPrincipal());
        }
        if (request.getInterest() != null) {
            claimRegistration.setInterest(request.getInterest());
        }
        if (request.getPenalty() != null) {
            claimRegistration.setPenalty(request.getPenalty());
        }
        if (request.getOtherLosses() != null) {
            claimRegistration.setOtherLosses(request.getOtherLosses());
        }
        if (request.getTotalAmount() != null) {
            claimRegistration.setTotalAmount(request.getTotalAmount());
        }
        if (request.getHasCourtJudgment() != null) {
            claimRegistration.setHasCourtJudgment(request.getHasCourtJudgment() == 1);
        }
        if (request.getHasExecution() != null) {
            claimRegistration.setHasExecution(request.getHasExecution() == 1);
        }
        if (request.getHasCollateral() != null) {
            claimRegistration.setHasCollateral(request.getHasCollateral() == 1);
        }
        if (request.getClaimNature() != null) {
            claimRegistration.setClaimNature(request.getClaimNature());
        }
        if (request.getClaimType() != null) {
            claimRegistration.setClaimType(request.getClaimType());
        }
        if (request.getClaimFacts() != null) {
            claimRegistration.setClaimFacts(request.getClaimFacts());
        }
        if (request.getClaimIdentifier() != null) {
            claimRegistration.setClaimIdentifier(request.getClaimIdentifier());
        }
        if (request.getEvidenceList() != null) {
            claimRegistration.setEvidenceList(request.getEvidenceList());
        }
        if (request.getEvidenceMaterials() != null) {
            claimRegistration.setEvidenceMaterials(request.getEvidenceMaterials());
        }
        if (request.getEvidenceAttachments() != null) {
            claimRegistration.setEvidenceAttachments(request.getEvidenceAttachments());
        }
        if (request.getRegistrationDate() != null) {
            claimRegistration.setRegistrationDate(request.getRegistrationDate());
        }
        if (request.getRegistrationDeadline() != null) {
            claimRegistration.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getMaterialReceiver() != null) {
            claimRegistration.setMaterialReceiver(request.getMaterialReceiver());
        }
        if (request.getMaterialReceiveDate() != null) {
            claimRegistration.setMaterialReceiveDate(request.getMaterialReceiveDate());
        }
        if (request.getMaterialCompleteness() != null) {
            claimRegistration.setMaterialCompleteness(request.getMaterialCompleteness());
        }
        if (request.getRegistrationStatus() != null) {
            claimRegistration.setRegistrationStatus(request.getRegistrationStatus());
        }
        if (request.getRemarks() != null) {
            claimRegistration.setRemarks(request.getRemarks());
        }

        return claimRegistrationRepository.save(claimRegistration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClaim(Long claimId, Long userId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        
        // 级联删除：债权确认 → 债权审查
        // 删除相关的确认记录
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimId);
        for (ClaimConfirmation confirmation : confirmations) {
            confirmation.setIsDeleted(true);
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            logger.info("级联删除债权确认记录, confirmationId: {}, claimRegistrationId: {}", confirmation.getId(), claimId);
        }
        
        // 删除相关的审查记录
        List<ClaimReview> reviews = claimReviewRepository.findAllByClaimRegistrationIdAndIsDeletedFalse(claimId);
        for (ClaimReview review : reviews) {
            review.setIsDeleted(true);
            review.setUpdateUserId(userId);
            claimReviewRepository.save(review);
            logger.info("级联删除债权审查记录, reviewId: {}, claimRegistrationId: {}", review.getId(), claimId);
        }
        
        // 删除债权申报
        claimRegistration.setIsDeleted(true);
        claimRegistration.setUpdateUserId(userId);
        claimRegistrationRepository.save(claimRegistration);
        logger.info("债权申报删除成功, claimId: {}", claimId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRegistrationStatus(Long claimId, String status, Long userId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        String oldStatus = claimRegistration.getRegistrationStatus();
        claimRegistration.setRegistrationStatus(status);
        claimRegistration.setUpdateUserId(userId);
        claimRegistrationRepository.save(claimRegistration);

        handleStatusChange(claimRegistration, oldStatus, status, userId);

        logger.info("债权申报状态更新成功, claimId: {}, oldStatus: {}, newStatus: {}", claimId, oldStatus, status);
    }

    private void handleStatusChange(ClaimRegistration registration, String oldStatus, String newStatus, Long userId) {
        if ("REVIEWING".equals(newStatus)) {
            createOrUpdateReviewRecord(registration, userId);
        } else if ("CONFIRMING".equals(newStatus)) {
            createOrUpdateConfirmationRecord(registration, userId);
        } else if ("REVIEW_COMPLETED".equals(newStatus)) {
            updateReviewRecordStatus(registration.getId(), "COMPLETED", userId);
        } else if ("CONFIRMED".equals(newStatus)) {
            updateConfirmationRecordStatus(registration.getId(), "COMPLETED", userId);
        } else if ("REJECTED".equals(newStatus)) {
            // 处理驳回状态
            handleRejectStatus(registration.getId(), userId);
        }
    }

    private void handleRejectStatus(Long claimId, Long userId) {
        // 级联处理相关的审查和确认记录
        List<ClaimReview> reviews = claimReviewRepository.findAllByClaimRegistrationIdAndIsDeletedFalse(claimId);
        for (ClaimReview review : reviews) {
            review.setIsDeleted(true);
            review.setUpdateUserId(userId);
            claimReviewRepository.save(review);
            logger.info("级联处理驳回状态 - 删除审查记录, reviewId: {}, claimId: {}", review.getId(), claimId);
        }
        
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimId);
        for (ClaimConfirmation confirmation : confirmations) {
            confirmation.setIsDeleted(true);
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            logger.info("级联处理驳回状态 - 删除确认记录, confirmationId: {}, claimId: {}", confirmation.getId(), claimId);
        }
    }

    private void createOrUpdateReviewRecord(ClaimRegistration registration, Long userId) {
        Optional<ClaimReview> existingReview = claimReviewRepository.findFirstByClaimRegistrationIdAndIsDeletedFalseOrderByReviewRoundDesc(registration.getId());

        if (existingReview.isPresent()) {
            ClaimReview review = existingReview.get();
            review.setReviewStatus("IN_PROGRESS");
            review.setUpdateUserId(userId);
            claimReviewRepository.save(review);
            logger.info("更新审查记录状态为进行中, claimId: {}, reviewId: {}", registration.getId(), review.getId());
        } else {
            ClaimReview review = new ClaimReview();
            review.setClaimRegistrationId(registration.getId());
            review.setCaseId(registration.getCaseId());
            review.setCreditorName(registration.getCreditorName());
            review.setDeclaredPrincipal(registration.getPrincipal());
            review.setDeclaredInterest(registration.getInterest());
            review.setDeclaredPenalty(registration.getPenalty());
            review.setDeclaredOtherLosses(registration.getOtherLosses());
            review.setDeclaredTotalAmount(registration.getTotalAmount());
            review.setReviewRound(1);
            review.setReviewStatus("IN_PROGRESS");
            review.setReviewDate(LocalDateTime.now());
            review.setCreateUserId(userId);
            review.setUpdateUserId(userId);
            claimReviewRepository.save(review);
            logger.info("创建审查记录, claimId: {}, reviewId: {}", registration.getId(), review.getId());
        }
    }

    private void createOrUpdateConfirmationRecord(ClaimRegistration registration, Long userId) {
        List<ClaimConfirmation> existingConfirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(registration.getId());

        if (!existingConfirmations.isEmpty()) {
            // 更新第一个确认记录
            ClaimConfirmation confirmation = existingConfirmations.get(0);
            confirmation.setConfirmationStatus("IN_PROGRESS");
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            logger.info("更新确认记录状态为进行中, claimId: {}, confirmationId: {}", registration.getId(), confirmation.getId());
        } else {
            ClaimConfirmation confirmation = new ClaimConfirmation();
            confirmation.setClaimRegistrationId(registration.getId());
            confirmation.setCaseId(registration.getCaseId());
            confirmation.setCreditorName(registration.getCreditorName());
            confirmation.setFinalConfirmedAmount(registration.getTotalAmount());
            confirmation.setConfirmationStatus("IN_PROGRESS");
            confirmation.setCreateUserId(userId);
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            logger.info("创建确认记录, claimId: {}, confirmationId: {}", registration.getId(), confirmation.getId());
        }
    }

    private void updateReviewRecordStatus(Long claimId, String status, Long userId) {
        Optional<ClaimReview> reviewOpt = claimReviewRepository.findFirstByClaimRegistrationIdAndIsDeletedFalseOrderByReviewRoundDesc(claimId);
        if (reviewOpt.isPresent()) {
            ClaimReview review = reviewOpt.get();
            review.setReviewStatus(status);
            review.setUpdateUserId(userId);
            claimReviewRepository.save(review);
            logger.info("更新审查记录状态, claimId: {}, reviewId: {}, status: {}", claimId, review.getId(), status);
        }
    }

    private void updateConfirmationRecordStatus(Long claimId, String status, Long userId) {
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimId);
        if (!confirmations.isEmpty()) {
            // 更新第一个确认记录
            ClaimConfirmation confirmation = confirmations.get(0);
            confirmation.setConfirmationStatus(status);
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            logger.info("更新确认记录状态, claimId: {}, confirmationId: {}, status: {}", claimId, confirmation.getId(), status);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveMaterial(Long claimId, String receiver, String completeness, Long userId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        claimRegistration.setMaterialReceiver(receiver);
        claimRegistration.setMaterialReceiveDate(LocalDateTime.now());
        claimRegistration.setMaterialCompleteness(completeness);
        claimRegistration.setUpdateUserId(userId);
        claimRegistrationRepository.save(claimRegistration);
        logger.info("债权申报材料接收成功, claimId: {}, receiver: {}, completeness: {}", claimId, receiver, completeness);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectClaim(Long claimId, String rejectReason, Long userId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        String oldStatus = claimRegistration.getRegistrationStatus();
        
        // 设置状态为已驳回
        claimRegistration.setRegistrationStatus("REJECTED");
        
        // 保存驳回理由到备注字段
        String remarks = claimRegistration.getRemarks();
        String newRemarks = "驳回理由: " + rejectReason;
        if (remarks != null && !remarks.trim().isEmpty()) {
            newRemarks = remarks + "\n" + newRemarks;
        }
        claimRegistration.setRemarks(newRemarks);
        
        claimRegistration.setUpdateUserId(userId);
        claimRegistrationRepository.save(claimRegistration);
        
        // 处理状态变化
        handleStatusChange(claimRegistration, oldStatus, "REJECTED", userId);
        
        // 发送通知
        User user = userRepository.findById(userId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 驳回了债权申报：%s\n驳回理由：%s", realName, claimRegistration.getCreditorName(), rejectReason);
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "债权申报驳回通知",
                content,
                "CLAIM_REGISTRATION_REJECT",
                claimRegistration.getId(),
                "ClaimRegistration",
                userId,
                realName
        );
        
        logger.info("债权申报驳回成功, claimId: {}, creditorName: {}, rejectReason: {}", claimId, claimRegistration.getCreditorName(), rejectReason);
    }

    private String generateClaimNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = claimRegistrationRepository.count() + 1;
        return "CLAIM" + dateStr + String.format("%06d", count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResponse importFromExcel(MultipartFile file, Long caseId, Long userId) {
        ExcelImportResponse response = new ExcelImportResponse();
        response.setSuccessCount(0);
        response.setFailCount(0);
        response.setErrors(new ArrayList<>());

        try {
            List<Map<String, Object>> dataList = ExcelImportUtil.parseExcel(file);

            if (dataList.isEmpty()) {
                response.setMessage("Excel文件为空");
                return response;
            }

            // 打印数据列表大小和表头信息
            logger.info("Excel解析完成，共{}行数据", dataList.size());
            if (!dataList.isEmpty()) {
                Map<String, Object> firstRow = dataList.get(0);
                logger.info("表头信息: {}", firstRow.keySet());
                logger.info("第一行数据: {}", firstRow);
                logger.info("第一行债权人名称: {}", ExcelImportUtil.getStringValue(firstRow, "债权人名称"));
                
                // 验证表头是否包含必需的列
                boolean hasRequiredColumns = false;
                for (String header : firstRow.keySet()) {
                    if (header != null && !header.trim().isEmpty()) {
                        hasRequiredColumns = true;
                        break;
                    }
                }
                
                if (!hasRequiredColumns) {
                    response.setMessage("Excel文件表头格式不正确，无法识别列名");
                    logger.error("Excel文件表头格式不正确，无法识别列名");
                    return response;
                }
            }

            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> row = dataList.get(i);
                int rowNum = i + 2;

                // 打印当前行数据
                logger.info("处理行号: {}, 数据: {}", rowNum, row);
                logger.info("当前行债权人名称: {}", ExcelImportUtil.getStringValue(row, "债权人名称"));

                // 检查债权人字段是否为空，如果为空则跳过该行
                String creditorName = ExcelImportUtil.getStringValue(row, "债权人名称");
                if (creditorName == null || creditorName.trim().isEmpty() || "空".equals(creditorName)) {
                    logger.info("跳过空行，行号: {}, 债权人名称为空", rowNum);
                    continue;
                }

                try {
                    ClaimRegistrationCreateRequest request = buildCreateRequestFromRow(row, caseId);
                    validateCreateRequest(request);

                    ClaimRegistration claimRegistration = createClaim(request, userId);
                    response.setSuccessCount(response.getSuccessCount() + 1);

                    logger.info("Excel导入成功, 行号: {}, 债权人: {}, claimId: {}", rowNum, request.getCreditorName(), claimRegistration.getId());
                } catch (Exception e) {
                    response.setFailCount(response.getFailCount() + 1);
                    ExcelImportResponse.ImportError error = new ExcelImportResponse.ImportError(rowNum, e.getMessage(), creditorName);
                    response.getErrors().add(error);
                    logger.error("Excel导入失败, 行号: {}, 错误: {}, 债权人名称: {}", rowNum, e.getMessage(), creditorName);
                }
            }

            response.setMessage(String.format("导入完成，成功%d条，失败%d条", response.getSuccessCount(), response.getFailCount()));
        } catch (IOException e) {
            response.setMessage("Excel文件解析失败: " + e.getMessage());
            logger.error("Excel文件解析失败", e);
        }

        return response;
    }

    private ClaimRegistrationCreateRequest buildCreateRequestFromRow(Map<String, Object> row, Long caseId) {
        ClaimRegistrationCreateRequest request = new ClaimRegistrationCreateRequest();

        request.setCaseId(caseId);
        request.setCaseName(getValueFromRow(row, "案件名称"));
        request.setDebtor(getValueFromRow(row, "债务人"));

        request.setCreditorName(getValueFromRow(row, "债权人名称"));
        request.setCreditorType(getValueFromRow(row, "债权人类型"));
        request.setCreditCode(getValueFromRow(row, "统一社会信用代码"));
        request.setLegalRepresentative(getValueFromRow(row, "法定代表人"));
        request.setServiceAddress(getValueFromRow(row, "送达地址"));

        request.setAgentName(getValueFromRow(row, "代理人姓名"));
        request.setAgentPhone(getValueFromRow(row, "代理人电话"));
        request.setAgentIdCard(getValueFromRow(row, "代理人身份证"));
        request.setAgentAddress(getValueFromRow(row, "代理人地址"));

        request.setAccountName(getValueFromRow(row, "账户名称"));
        request.setCreditorBankAccount(getValueFromRow(row, "债权人银行账号"));
        request.setBankName(getValueFromRow(row, "开户行"));

        request.setPrincipal(getBigDecimalValueFromRow(row, "本金"));
        request.setInterest(getBigDecimalValueFromRow(row, "利息"));
        request.setPenalty(getBigDecimalValueFromRow(row, "违约金"));
        request.setOtherLosses(getBigDecimalValueFromRow(row, "其他损失"));
        request.setTotalAmount(getBigDecimalValueFromRow(row, "总金额"));

        Boolean hasCourtJudgment = getBooleanValueFromRow(row, "是否有法院判决");
        request.setHasCourtJudgment(hasCourtJudgment != null && hasCourtJudgment ? 1 : 0);

        Boolean hasExecution = getBooleanValueFromRow(row, "是否有执行");
        request.setHasExecution(hasExecution != null && hasExecution ? 1 : 0);

        Boolean hasCollateral = getBooleanValueFromRow(row, "是否有担保");
        request.setHasCollateral(hasCollateral != null && hasCollateral ? 1 : 0);

        request.setClaimNature(getValueFromRow(row, "债权性质"));
        request.setClaimType(getValueFromRow(row, "债权类型"));
        request.setClaimFacts(getValueFromRow(row, "债权事实"));
        request.setClaimIdentifier(getValueFromRow(row, "债权标识"));

        request.setEvidenceList(getValueFromRow(row, "证据清单"));
        request.setEvidenceMaterials(getValueFromRow(row, "证据材料"));
        request.setEvidenceAttachments(getValueFromRow(row, "证据附件"));

        request.setRegistrationDate(getLocalDateTimeValueFromRow(row, "登记日期"));
        request.setRegistrationDeadline(getLocalDateTimeValueFromRow(row, "登记截止日期"));

        request.setMaterialReceiver(getValueFromRow(row, "材料接收人"));
        request.setMaterialReceiveDate(getLocalDateTimeValueFromRow(row, "材料接收日期"));
        request.setMaterialCompleteness(getValueFromRow(row, "材料完整性"));

        request.setRemarks(getValueFromRow(row, "备注"));

        return request;
    }

    // 辅助方法：从行数据中获取字符串值，支持多种表头名称变体
    private String getValueFromRow(Map<String, Object> row, String key) {
        // 直接使用ExcelImportUtil的getStringValue方法，它已经支持包含key的表头查找
        String value = ExcelImportUtil.getStringValue(row, key);
        if (value != null) {
            return value;
        }
        
        // 尝试查找常见的变体
        String[] variants = getKeyVariants(key);
        for (String variant : variants) {
            value = ExcelImportUtil.getStringValue(row, variant);
            if (value != null) {
                return value;
            }
        }
        
        return null;
    }

    // 辅助方法：从行数据中获取BigDecimal值，支持多种表头名称变体
    private BigDecimal getBigDecimalValueFromRow(Map<String, Object> row, String key) {
        // 直接查找
        BigDecimal value = ExcelImportUtil.getBigDecimalValue(row, key);
        if (value != null) {
            return value;
        }
        
        // 尝试查找常见的变体
        String[] variants = getKeyVariants(key);
        for (String variant : variants) {
            value = ExcelImportUtil.getBigDecimalValue(row, variant);
            if (value != null) {
                return value;
            }
        }
        
        return null;
    }

    // 辅助方法：从行数据中获取Boolean值，支持多种表头名称变体
    private Boolean getBooleanValueFromRow(Map<String, Object> row, String key) {
        // 直接查找
        Boolean value = ExcelImportUtil.getBooleanValue(row, key);
        if (value != null) {
            return value;
        }
        
        // 尝试查找常见的变体
        String[] variants = getKeyVariants(key);
        for (String variant : variants) {
            value = ExcelImportUtil.getBooleanValue(row, variant);
            if (value != null) {
                return value;
            }
        }
        
        return null;
    }

    // 辅助方法：从行数据中获取LocalDateTime值，支持多种表头名称变体
    private LocalDateTime getLocalDateTimeValueFromRow(Map<String, Object> row, String key) {
        // 直接查找
        LocalDateTime value = ExcelImportUtil.getLocalDateTimeValue(row, key);
        if (value != null) {
            return value;
        }
        
        // 尝试查找常见的变体
        String[] variants = getKeyVariants(key);
        for (String variant : variants) {
            value = ExcelImportUtil.getLocalDateTimeValue(row, variant);
            if (value != null) {
                return value;
            }
        }
        
        return null;
    }

    // 辅助方法：获取关键字的常见变体
    private String[] getKeyVariants(String key) {
        switch (key) {
            case "案件名称":
                return new String[]{"案件", "案件名称"};
            case "债务人":
                return new String[]{"债务人", "债务人名称"};
            case "债权人名称":
                return new String[]{"债权人", "债权人姓名", "债权人名称"};
            case "债权人类型":
                return new String[]{"债权人类型", "债权人类别"};
            case "统一社会信用代码":
                return new String[]{"统一社会信用代码", "社会信用代码", "信用代码"};
            case "法定代表人":
                return new String[]{"法定代表人", "法人代表"};
            case "送达地址":
                return new String[]{"送达地址", "地址"};
            case "代理人姓名":
                return new String[]{"代理人姓名", "代理人"};
            case "代理人电话":
                return new String[]{"代理人电话", "代理人联系电话"};
            case "代理人身份证":
                return new String[]{"代理人身份证", "代理人身份证号"};
            case "代理人地址":
                return new String[]{"代理人地址", "代理人住址"};
            case "账户名称":
                return new String[]{"账户名称", "账户"};
            case "债权人银行账号":
                return new String[]{"债权人银行账号", "银行账号", "账号"};
            case "开户行":
                return new String[]{"开户行", "开户银行"};
            case "本金":
                return new String[]{"本金", "债权本金"};
            case "利息":
                return new String[]{"利息", "债权利息"};
            case "违约金":
                return new String[]{"违约金", "罚息"};
            case "其他损失":
                return new String[]{"其他损失", "其他费用"};
            case "总金额":
                return new String[]{"总金额", "债权金额", "金额", "申报金额（元）"};
            case "是否有法院判决":
                return new String[]{"是否有法院判决", "有法院判决", "法院判决"};
            case "是否有执行":
                return new String[]{"是否有执行", "有执行", "执行"};
            case "是否有担保":
                return new String[]{"是否有担保", "有担保", "担保"};
            case "债权性质":
                return new String[]{"债权性质", "性质"};
            case "债权类型":
                return new String[]{"债权类型", "类型"};
            case "债权事实":
                return new String[]{"债权事实", "事实"};
            case "债权标识":
                return new String[]{"债权标识", "标识"};
            case "证据清单":
                return new String[]{"证据清单", "证据"};
            case "证据材料":
                return new String[]{"证据材料", "材料"};
            case "证据附件":
                return new String[]{"证据附件", "附件"};
            case "登记日期":
                return new String[]{"登记日期", "日期"};
            case "登记截止日期":
                return new String[]{"登记截止日期", "截止日期"};
            case "材料接收人":
                return new String[]{"材料接收人", "接收人"};
            case "材料接收日期":
                return new String[]{"材料接收日期", "接收日期"};
            case "材料完整性":
                return new String[]{"材料完整性", "完整性"};
            case "备注":
                return new String[]{"备注", "说明"};
            default:
                return new String[]{key};
        }
    }

    private void validateCreateRequest(ClaimRegistrationCreateRequest request) {
        if (request.getCreditorName() == null || request.getCreditorName().trim().isEmpty()) {
            throw new BusinessException("债权人名称不能为空");
        }
        if (request.getCreditorType() == null || request.getCreditorType().trim().isEmpty()) {
            throw new BusinessException("债权人类型不能为空");
        }
        if (request.getClaimType() == null || request.getClaimType().trim().isEmpty()) {
            throw new BusinessException("债权类型不能为空");
        }
        // 如果总金额没有查到或者不大于0，设置默认为0
        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            request.setTotalAmount(BigDecimal.ZERO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResponse importFromExcelEasy(MultipartFile file, Long caseId, Long userId) {
        ExcelImportResponse response = new ExcelImportResponse();
        response.setSuccessCount(0);
        response.setFailCount(0);
        response.setErrors(new ArrayList<>());

        try {
            ClaimRegistrationExcelListener listener = new ClaimRegistrationExcelListener(caseId, userId);

            EasyExcel.read(file.getInputStream(), ClaimRegistrationExcelImportDTO.class, listener)
                    .sheet()
                    .doRead();

            List<ClaimRegistrationCreateRequest> successList = listener.getSuccessList();
            List<ExcelImportResponse.ImportError> errorList = listener.getErrorList();

            response.setFailCount(errorList.size());
            response.setErrors(errorList);

            for (ClaimRegistrationCreateRequest request : successList) {
                try {
                    ClaimRegistration claimRegistration = createClaim(request, userId);
                    listener.addSavedClaim(claimRegistration);
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception e) {
                    response.setFailCount(response.getFailCount() + 1);
                    ExcelImportResponse.ImportError error = new ExcelImportResponse.ImportError(
                            0,
                            "保存失败: " + e.getMessage(),
                            request.getCreditorName()
                    );
                    response.getErrors().add(error);
                    logger.error("保存债权申报失败: {}", e.getMessage(), e);
                }
            }

            response.setMessage(String.format("导入完成，成功%d条，失败%d条", response.getSuccessCount(), response.getFailCount()));
            logger.info("EasyExcel导入完成，成功{}条，失败{}条", response.getSuccessCount(), response.getFailCount());
        } catch (IOException e) {
            response.setMessage("Excel文件解析失败: " + e.getMessage());
            logger.error("Excel文件解析失败", e);
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResponse importFromDeclaredClaimsRegister(MultipartFile file, Long caseId, Long userId) {
        ExcelImportResponse response = new ExcelImportResponse();
        response.setSuccessCount(0);
        response.setFailCount(0);
        response.setErrors(new ArrayList<>());

        try {
            logger.info("开始导入已申报债权登记簿 - 文件名: {}, 大小: {}KB, 案件ID: {}, 用户ID: {}", 
                    file.getOriginalFilename(), file.getSize()/1024, caseId, userId);

            DeclaredClaimsRegisterExcelListener listener = new DeclaredClaimsRegisterExcelListener(this, caseId, userId);

            logger.debug("开始解析Excel文件");
            // 配置EasyExcel，设置表头行索引为0（默认），并添加更多的读取配置
            EasyExcel.read(file.getInputStream(), DeclaredClaimsRegisterExcelImportDTO.class, listener)
                    .sheet() // 读取第一个sheet
                    .headRowNumber(0) // 表头在第0行
                    .autoTrim(true) // 自动去除空格
                    .doRead();
            logger.debug("Excel文件解析完成");

            response.setMessage("已申报债权登记簿导入成功");
            logger.info("已申报债权登记簿导入完成 - 文件名: {}", file.getOriginalFilename());
        } catch (IOException e) {
            response.setMessage("Excel文件解析失败: " + e.getMessage());
            logger.error("Excel文件解析失败 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
        } catch (Exception e) {
            response.setMessage("导入失败: " + e.getMessage());
            logger.error("导入过程中发生异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
        }

        return response;
    }

    @Override
    public void exportToExcel(HttpServletResponse response, Long caseId, String registrationStatus) {
        try {
            List<ClaimRegistration> claimList = getClaimList(1, 10000, caseId, registrationStatus);

            List<ClaimRegistrationExcelDTO> excelDataList = new ArrayList<>();
            for (ClaimRegistration claim : claimList) {
                ClaimRegistrationExcelDTO dto = new ClaimRegistrationExcelDTO();
                BeanUtils.copyProperties(claim, dto);
                dto.setHasCourtJudgment(claim.getHasCourtJudgment() != null && claim.getHasCourtJudgment() ? "是" : "否");
                dto.setHasExecution(claim.getHasExecution() != null && claim.getHasExecution() ? "是" : "否");
                dto.setHasCollateral(claim.getHasCollateral() != null && claim.getHasCollateral() ? "是" : "否");
                excelDataList.add(dto);
            }

            String fileName = "债权登记表";
            if (caseId != null) {
                fileName += "_案件" + caseId;
            }
            if (registrationStatus != null) {
                fileName += "_" + registrationStatus;
            }
            fileName += "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), ClaimRegistrationExcelDTO.class)
                    .sheet("债权登记")
                    .doWrite(excelDataList);

            logger.info("导出Excel成功，共{}条数据", excelDataList.size());
        } catch (IOException e) {
            logger.error("导出Excel失败", e);
            throw new BusinessException("导出Excel失败: " + e.getMessage());
        }
    }
}
