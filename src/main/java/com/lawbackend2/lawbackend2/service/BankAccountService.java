package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.common.PageResult;

public interface BankAccountService {

    Long createBankAccount(BankAccountCreateRequest request, Long userId);

    PageResult<BankAccount> getBankAccountList(Integer pageNum, Integer pageSize, String accountType, String status, String accountName, Long caseId, Long userId);

    BankAccount getBankAccountDetail(Long accountId, Long userId);

    void updateBankAccount(Long accountId, BankAccountUpdateRequest request, Long userId);

    void updateBankAccountPassword(Long accountId, BankAccountPasswordRequest request, Long userId);

    void updateBankAccountStatus(Long accountId, BankAccountStatusRequest request, Long userId);

    void deleteBankAccount(Long accountId);
}
