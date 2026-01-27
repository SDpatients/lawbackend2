package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.BankAccountTransactionRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.BankAccountTransactionService;
import com.lawbackend2.lawbackend2.common.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class BankAccountTransactionServiceImpl implements BankAccountTransactionService {

    private final BankAccountTransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public BankAccountTransactionServiceImpl(BankAccountTransactionRepository transactionRepository,
                                               BankAccountRepository bankAccountRepository,
                                               UserRoleRepository userRoleRepository,
                                               RoleRepository roleRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Long createTransaction(BankAccountTransactionCreateRequest request, Long userId) {
        BankAccount bankAccount = bankAccountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BusinessException("银行账户不存在"));

        BankAccountTransaction transaction = new BankAccountTransaction();
        BeanUtils.copyProperties(request, transaction);
        transaction.setStatus("ACTIVE");
        transaction.setCreateUserId(userId);
        transaction.setUpdateUserId(userId);

        if (request.getCaseId() == null && bankAccount.getCaseId() != null) {
            transaction.setCaseId(bankAccount.getCaseId());
        }

        BankAccountTransaction saved = transactionRepository.save(transaction);
        return saved.getId();
    }

    @Override
    public PageResult<BankAccountTransactionResponse> getTransactionList(Integer pageNum, Integer pageSize,
                                                                           Long accountId, String transactionType,
                                                                           String businessType, LocalDate startDate,
                                                                           LocalDate endDate, Long caseId, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate", "createTime"));

        boolean isAdmin = isAdminOrSuperAdmin(userId);
        Page<BankAccountTransactionResponse> page = transactionRepository.findTransactionsWithDetails(
                accountId, transactionType, businessType, startDate, endDate, caseId, pageable);

        PageResult<BankAccountTransactionResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public BankAccountTransaction getTransactionDetail(Long transactionId, Long userId) {
        BankAccountTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("交易记录不存在"));

        BankAccount bankAccount = bankAccountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new BusinessException("关联银行账户不存在"));

        checkPermission(bankAccount, userId);
        return transaction;
    }

    @Override
    public void updateTransaction(Long transactionId, BankAccountTransactionUpdateRequest request, Long userId) {
        BankAccountTransaction transaction = getTransactionDetail(transactionId, userId);

        if (request.getTransactionType() != null) {
            transaction.setTransactionType(request.getTransactionType());
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getTransactionDate() != null) {
            transaction.setTransactionDate(request.getTransactionDate());
        }
        if (request.getSummary() != null) {
            transaction.setSummary(request.getSummary());
        }
        if (request.getBusinessType() != null) {
            transaction.setBusinessType(request.getBusinessType());
        }
        if (request.getCounterpartyAccount() != null) {
            transaction.setCounterpartyAccount(request.getCounterpartyAccount());
        }
        if (request.getCounterpartyName() != null) {
            transaction.setCounterpartyName(request.getCounterpartyName());
        }
        if (request.getBalanceAfter() != null) {
            transaction.setBalanceAfter(request.getBalanceAfter());
        }
        if (request.getAttachmentId() != null) {
            transaction.setAttachmentId(request.getAttachmentId());
        }
        if (request.getRelatedBusinessId() != null) {
            transaction.setRelatedBusinessId(request.getRelatedBusinessId());
        }
        if (request.getRemark() != null) {
            transaction.setRemark(request.getRemark());
        }

        transaction.setUpdateUserId(userId);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new BusinessException("交易记录不存在");
        }
        transactionRepository.deleteById(transactionId);
    }

    private void checkPermission(BankAccount bankAccount, Long userId) {
        if (isAdminOrSuperAdmin(userId)) {
            return;
        }

        if (bankAccount.getCreateUserId().equals(userId)) {
            return;
        }

        throw new BusinessException("无权访问该交易记录");
    }

    private boolean isAdminOrSuperAdmin(Long userId) {
        java.util.List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null && ("ADMIN".equals(role.getRoleCode()) || "SUPER_ADMIN".equals(role.getRoleCode()))) {
                return true;
            }
        }
        return false;
    }
}
