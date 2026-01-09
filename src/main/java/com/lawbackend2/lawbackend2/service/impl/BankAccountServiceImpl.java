package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.service.BankAccountService;
import com.lawbackend2.lawbackend2.common.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final com.lawbackend2.lawbackend2.util.PasswordUtil passwordUtil;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, com.lawbackend2.lawbackend2.util.PasswordUtil passwordUtil) {
        this.bankAccountRepository = bankAccountRepository;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public Long createBankAccount(BankAccountCreateRequest request) {
        if (bankAccountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new BusinessException("银行账号已存在");
        }

        BankAccount bankAccount = new BankAccount();
        BeanUtils.copyProperties(request, bankAccount);
        bankAccount.setPassword(passwordUtil.encode(request.getPassword()));
        bankAccount.setStatus("ACTIVE");

        BankAccount saved = bankAccountRepository.save(bankAccount);
        return saved.getId();
    }

    @Override
    public PageResult<BankAccount> getBankAccountList(Integer pageNum, Integer pageSize, String accountType, String status, String accountName) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BankAccount> page = bankAccountRepository.findByConditions(accountType, status, accountName, pageable);

        PageResult<BankAccount> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public BankAccount getBankAccountDetail(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("银行账户不存在"));
    }

    @Override
    public void updateBankAccount(Long accountId, BankAccountUpdateRequest request) {
        BankAccount bankAccount = getBankAccountDetail(accountId);
        bankAccount.setAccountName(request.getAccountName());
        bankAccount.setCurrentBalance(request.getCurrentBalance());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void updateBankAccountPassword(Long accountId, BankAccountPasswordRequest request) {
        BankAccount bankAccount = getBankAccountDetail(accountId);

        if (!passwordUtil.matches(request.getOldPassword(), bankAccount.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        bankAccount.setPassword(passwordUtil.encode(request.getNewPassword()));
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void updateBankAccountStatus(Long accountId, BankAccountStatusRequest request) {
        BankAccount bankAccount = getBankAccountDetail(accountId);
        bankAccount.setStatus(request.getStatus());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBankAccount(Long accountId) {
        if (!bankAccountRepository.existsById(accountId)) {
            throw new BusinessException("银行账户不存在");
        }
        bankAccountRepository.deleteById(accountId);
    }
}
