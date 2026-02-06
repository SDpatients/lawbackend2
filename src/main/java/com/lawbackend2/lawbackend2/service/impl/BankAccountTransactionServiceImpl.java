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
    @Transactional(rollbackFor = Exception.class)
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

        BigDecimal currentBalance = bankAccount.getCurrentBalance() != null ? bankAccount.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO;
        BigDecimal balanceAfter;

        if ("IN".equals(request.getTransactionType())) {
            balanceAfter = currentBalance.add(amount);
            bankAccount.setCurrentBalance(balanceAfter);
        } else if ("OUT".equals(request.getTransactionType())) {
            balanceAfter = currentBalance.subtract(amount);
            if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("账户余额不足，无法完成流出交易");
            }
            bankAccount.setCurrentBalance(balanceAfter);
        } else {
            throw new BusinessException("无效的交易类型，必须是 IN(流入) 或 OUT(流出)");
        }

        transaction.setBalanceAfter(balanceAfter);

        BankAccountTransaction saved = transactionRepository.save(transaction);
        bankAccountRepository.save(bankAccount);

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
    @Transactional(rollbackFor = Exception.class)
    public void updateTransaction(Long transactionId, BankAccountTransactionUpdateRequest request, Long userId) {
        BankAccountTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("交易记录不存在"));

        BankAccount bankAccount = bankAccountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new BusinessException("关联银行账户不存在"));

        checkPermission(bankAccount, userId);

        String oldTransactionType = transaction.getTransactionType();
        BigDecimal oldAmount = transaction.getAmount() != null ? transaction.getAmount() : BigDecimal.ZERO;

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
        if (request.getAttachmentId() != null) {
            transaction.setAttachmentId(request.getAttachmentId());
        }
        if (request.getRelatedBusinessId() != null) {
            transaction.setRelatedBusinessId(request.getRelatedBusinessId());
        }
        if (request.getRemark() != null) {
            transaction.setRemark(request.getRemark());
        }

        boolean typeChanged = request.getTransactionType() != null && !request.getTransactionType().equals(oldTransactionType);
        boolean amountChanged = request.getAmount() != null && !request.getAmount().equals(oldAmount);

        if (typeChanged || amountChanged) {
            BigDecimal currentBalance = bankAccount.getCurrentBalance() != null ? bankAccount.getCurrentBalance() : BigDecimal.ZERO;

            if ("IN".equals(oldTransactionType)) {
                currentBalance = currentBalance.subtract(oldAmount);
            } else if ("OUT".equals(oldTransactionType)) {
                currentBalance = currentBalance.add(oldAmount);
            }

            String newTransactionType = typeChanged ? request.getTransactionType() : oldTransactionType;
            BigDecimal newAmount = amountChanged ? request.getAmount() : oldAmount;

            BigDecimal balanceAfter;
            if ("IN".equals(newTransactionType)) {
                balanceAfter = currentBalance.add(newAmount);
                bankAccount.setCurrentBalance(balanceAfter);
            } else if ("OUT".equals(newTransactionType)) {
                balanceAfter = currentBalance.subtract(newAmount);
                if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("账户余额不足，无法完成流出交易");
                }
                bankAccount.setCurrentBalance(balanceAfter);
            } else {
                throw new BusinessException("无效的交易类型，必须是 IN(流入) 或 OUT(流出)");
            }

            transaction.setBalanceAfter(balanceAfter);
            bankAccountRepository.save(bankAccount);
        }

        transaction.setUpdateUserId(userId);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(Long transactionId, Long userId) {
        BankAccountTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("交易记录不存在"));

        BankAccount bankAccount = bankAccountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new BusinessException("关联银行账户不存在"));

        checkPermission(bankAccount, userId);

        BigDecimal currentBalance = bankAccount.getCurrentBalance() != null ? bankAccount.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal amount = transaction.getAmount() != null ? transaction.getAmount() : BigDecimal.ZERO;

        if ("IN".equals(transaction.getTransactionType())) {
            currentBalance = currentBalance.subtract(amount);
        } else if ("OUT".equals(transaction.getTransactionType())) {
            currentBalance = currentBalance.add(amount);
        }

        bankAccount.setCurrentBalance(currentBalance);
        bankAccountRepository.save(bankAccount);

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
