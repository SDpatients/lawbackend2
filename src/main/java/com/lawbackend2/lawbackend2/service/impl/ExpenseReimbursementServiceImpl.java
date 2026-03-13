package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementItemCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementAttachmentResponse;
import com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementItemResponse;
import com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursement;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursementItem;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementAttachmentRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementItemRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.BankAccountTransactionService;
import com.lawbackend2.lawbackend2.service.ExpenseReimbursementService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseReimbursementServiceImpl implements ExpenseReimbursementService {

    private final ExpenseReimbursementRepository expenseReimbursementRepository;
    private final ExpenseReimbursementItemRepository expenseReimbursementItemRepository;
    private final ExpenseReimbursementAttachmentRepository expenseReimbursementAttachmentRepository;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final BankAccountTransactionService bankAccountTransactionService;
    private final FileRecordRepository fileRecordRepository;

    public ExpenseReimbursementServiceImpl(ExpenseReimbursementRepository expenseReimbursementRepository,
                                        ExpenseReimbursementItemRepository expenseReimbursementItemRepository,
                                        ExpenseReimbursementAttachmentRepository expenseReimbursementAttachmentRepository,
                                        BankruptCaseRepository bankruptCaseRepository,
                                        BankAccountRepository bankAccountRepository,
                                        UserRepository userRepository,
                                        BankAccountTransactionService bankAccountTransactionService,
                                        FileRecordRepository fileRecordRepository) {
        this.expenseReimbursementRepository = expenseReimbursementRepository;
        this.expenseReimbursementItemRepository = expenseReimbursementItemRepository;
        this.expenseReimbursementAttachmentRepository = expenseReimbursementAttachmentRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankAccountTransactionService = bankAccountTransactionService;
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    public Long createExpenseReimbursement(ExpenseReimbursementCreateRequest request, Long userId) {
        BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new BusinessException("案件不存在"));

        BankAccount bankAccount = bankAccountRepository.findById(request.getFundAccountId())
                .orElseThrow(() -> new BusinessException("银行账户不存在"));

        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("申请人不存在"));

        ExpenseReimbursement reimbursement = new ExpenseReimbursement();
        reimbursement.setReimbursementNumber(generateReimbursementNumber());
        reimbursement.setCaseId(request.getCaseId());
        reimbursement.setCaseName(bankruptCase.getCaseName());
        reimbursement.setApplicantId(applicant.getId());
        reimbursement.setApplicantName(applicant.getRealName());
        reimbursement.setFundAccountId(request.getFundAccountId());
        reimbursement.setFundAccountName(bankAccount.getAccountName());
        reimbursement.setBankName(bankAccount.getBankName());
        reimbursement.setBankAccount(bankAccount.getAccountNumber());
        reimbursement.setReimbursementDate(request.getReimbursementDate());
        reimbursement.setDescription(request.getDescription());
        reimbursement.setApprovalStatus("PENDING");
        reimbursement.setStatus("ACTIVE");
        reimbursement.setCreateUserId(userId);
        reimbursement.setUpdateUserId(userId);

        BigDecimal totalAmount = request.getItems().stream()
                .map(ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        reimbursement.setTotalAmount(totalAmount);

        ExpenseReimbursement saved = expenseReimbursementRepository.save(reimbursement);

        int sortOrder = 1;
        for (ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest itemRequest : request.getItems()) {
            ExpenseReimbursementItem item = new ExpenseReimbursementItem();
            item.setReimbursementId(saved.getId());
            item.setItemName(itemRequest.getItemName());
            item.setItemAmount(itemRequest.getItemAmount());
            item.setItemDescription(itemRequest.getItemDescription());
            item.setSortOrder(sortOrder++);
            expenseReimbursementItemRepository.save(item);
        }

        return saved.getId();
    }

    @Override
    public ExpenseReimbursementResponse getExpenseReimbursementDetail(Long reimbursementId) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        ExpenseReimbursementResponse response = new ExpenseReimbursementResponse();
        BeanUtils.copyProperties(reimbursement, response);

        List<ExpenseReimbursementItem> items = expenseReimbursementItemRepository.findByReimbursementId(reimbursementId);
        response.setItems(items.stream().map(item -> {
            ExpenseReimbursementItemResponse itemResponse = new ExpenseReimbursementItemResponse();
            BeanUtils.copyProperties(item, itemResponse);
            return itemResponse;
        }).collect(Collectors.toList()));

        List<ExpenseReimbursementAttachment> attachments = expenseReimbursementAttachmentRepository.findByReimbursementId(reimbursementId);
        response.setAttachments(attachments.stream().map(attachment -> {
            ExpenseReimbursementAttachmentResponse attachmentResponse = new ExpenseReimbursementAttachmentResponse();
            BeanUtils.copyProperties(attachment, attachmentResponse);
            return attachmentResponse;
        }).collect(Collectors.toList()));

        return response;
    }

    @Override
    public PageResult<ExpenseReimbursementResponse> getExpenseReimbursementList(Integer pageNum, Integer pageSize, Long caseId, Long applicantId, String approvalStatus, LocalDate reimbursementDate) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ExpenseReimbursement> page = expenseReimbursementRepository.findByConditions(caseId, applicantId, approvalStatus, reimbursementDate, pageable);

        PageResult<ExpenseReimbursementResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent().stream().map(reimbursement -> {
            ExpenseReimbursementResponse response = new ExpenseReimbursementResponse();
            BeanUtils.copyProperties(reimbursement, response);
            return response;
        }).collect(Collectors.toList()));

        return result;
    }

    @Override
    public void updateExpenseReimbursement(ExpenseReimbursementUpdateRequest request) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能修改");
        }

        BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new BusinessException("案件不存在"));

        BankAccount bankAccount = bankAccountRepository.findById(request.getFundAccountId())
                .orElseThrow(() -> new BusinessException("银行账户不存在"));

        reimbursement.setCaseId(request.getCaseId());
        reimbursement.setCaseName(bankruptCase.getCaseName());
        reimbursement.setFundAccountId(request.getFundAccountId());
        reimbursement.setFundAccountName(bankAccount.getAccountName());
        reimbursement.setBankName(bankAccount.getBankName());
        reimbursement.setBankAccount(bankAccount.getAccountNumber());
        reimbursement.setReimbursementDate(request.getReimbursementDate());
        reimbursement.setDescription(request.getDescription());

        expenseReimbursementRepository.save(reimbursement);
    }

    @Override
    public void deleteExpenseReimbursement(Long reimbursementId) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能删除");
        }

        expenseReimbursementRepository.delete(reimbursement);
    }

    @Override
    public void approveExpenseReimbursement(Long reimbursementId, ExpenseReimbursementApprovalRequest request, Long approverId) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批");
        }

        if (!"APPROVED".equals(request.getApprovalStatus()) && !"REJECTED".equals(request.getApprovalStatus())) {
            throw new BusinessException("审批状态不正确");
        }

        // 查询审批人信息
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new BusinessException("审批人不存在"));

        reimbursement.setApprovalStatus(request.getApprovalStatus());
        reimbursement.setApprovalOpinion(request.getApprovalOpinion());
        reimbursement.setApprovalTime(LocalDateTime.now());
        reimbursement.setApproverId(approverId);
        reimbursement.setApproverName(approver.getRealName());

        expenseReimbursementRepository.save(reimbursement);

        if ("APPROVED".equals(request.getApprovalStatus())) {
            createTransactionForApprovedReimbursement(reimbursement);
        }
    }

    private void createTransactionForApprovedReimbursement(ExpenseReimbursement reimbursement) {
        BankAccountTransactionCreateRequest request = new BankAccountTransactionCreateRequest();
        request.setAccountId(reimbursement.getFundAccountId());
        request.setTransactionType("OUT");
        request.setAmount(reimbursement.getTotalAmount());
        request.setTransactionDate(reimbursement.getReimbursementDate());
        request.setSummary("费用报销：" + reimbursement.getReimbursementNumber());
        request.setBusinessType("付款");
        request.setCounterpartyAccount(reimbursement.getBankAccount());
        request.setCounterpartyName(reimbursement.getApplicantName());
        request.setRelatedBusinessId(reimbursement.getId());
        request.setCaseId(reimbursement.getCaseId());
        request.setRemark(reimbursement.getDescription());

        bankAccountTransactionService.createTransaction(request, reimbursement.getApproverId());
    }

    @Override
    public Long addExpenseReimbursementItem(ExpenseReimbursementItemCreateRequest request) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(request.getReimbursementId())
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能添加明细");
        }

        ExpenseReimbursementItem item = new ExpenseReimbursementItem();
        item.setReimbursementId(request.getReimbursementId());
        item.setItemName(request.getItemName());
        item.setItemAmount(request.getItemAmount());
        item.setItemDescription(request.getItemDescription());
        item.setSortOrder(0);

        ExpenseReimbursementItem saved = expenseReimbursementItemRepository.save(item);

        BigDecimal totalAmount = expenseReimbursementItemRepository.findByReimbursementId(request.getReimbursementId())
                .stream()
                .map(ExpenseReimbursementItem::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        reimbursement.setTotalAmount(totalAmount);
        expenseReimbursementRepository.save(reimbursement);

        return saved.getId();
    }

    @Override
    public void deleteExpenseReimbursementItem(Long itemId) {
        ExpenseReimbursementItem item = expenseReimbursementItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("报销明细不存在"));

        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(item.getReimbursementId())
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能删除明细");
        }

        expenseReimbursementItemRepository.delete(item);

        BigDecimal totalAmount = expenseReimbursementItemRepository.findByReimbursementId(item.getReimbursementId())
                .stream()
                .map(ExpenseReimbursementItem::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        reimbursement.setTotalAmount(totalAmount);
        expenseReimbursementRepository.save(reimbursement);
    }

    @Override
    public Long uploadExpenseReimbursementAttachment(Long reimbursementId, String fileName, String filePath, Long fileSize, String fileType) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能上传附件");
        }

        ExpenseReimbursementAttachment attachment = new ExpenseReimbursementAttachment();
        attachment.setReimbursementId(reimbursementId);
        attachment.setFileName(fileName);
        attachment.setFilePath(filePath);
        attachment.setFileSize(fileSize);
        attachment.setFileType(fileType);
        attachment.setUploadTime(LocalDateTime.now());
        attachment.setSortOrder(0);

        ExpenseReimbursementAttachment saved = expenseReimbursementAttachmentRepository.save(attachment);
        return saved.getId();
    }

    @Override
    public void deleteExpenseReimbursementAttachment(Long attachmentId) {
        ExpenseReimbursementAttachment attachment = expenseReimbursementAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException("附件不存在"));

        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(attachment.getReimbursementId())
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能删除附件");
        }

        expenseReimbursementAttachmentRepository.delete(attachment);
    }

    @Override
    public ExpenseReimbursementAttachment getAttachmentById(Long attachmentId) {
        return expenseReimbursementAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException("附件不存在"));
    }

    private String generateReimbursementNumber() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "BX" + datePrefix;

        Long count = expenseReimbursementRepository.countByReimbursementNumberPrefix(prefix);
        int sequence = (count != null ? count.intValue() : 0) + 1;
        String sequenceStr = String.format("%04d", sequence);

        return prefix + sequenceStr;
    }

    @Override
    public Long linkAttachment(Long reimbursementId, Long fileId) {
        ExpenseReimbursement reimbursement = expenseReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new BusinessException("报销单不存在"));

        if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销单已审批，不能关联附件");
        }

        FileRecord fileRecord = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        ExpenseReimbursementAttachment attachment = new ExpenseReimbursementAttachment();
        attachment.setReimbursementId(reimbursementId);
        attachment.setFileName(fileRecord.getOriginalFileName());
        attachment.setFilePath(fileRecord.getFilePath());
        attachment.setFileSize(fileRecord.getFileSize());
        attachment.setFileType(fileRecord.getMimeType());
        attachment.setUploadTime(LocalDateTime.now());
        attachment.setSortOrder(0);

        ExpenseReimbursementAttachment saved = expenseReimbursementAttachmentRepository.save(attachment);
        return saved.getId();
    }
}
