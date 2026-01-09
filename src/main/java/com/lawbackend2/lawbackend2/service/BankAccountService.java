package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.common.PageResult;

public interface BankAccountService {

    Long createBankAccount(BankAccountCreateRequest request);

    PageResult<BankAccount> getBankAccountList(Integer pageNum, Integer pageSize, String accountType, String status, String accountName);

    BankAccount getBankAccountDetail(Long accountId);

    void updateBankAccount(Long accountId, BankAccountUpdateRequest request);

    void updateBankAccountPassword(Long accountId, BankAccountPasswordRequest request);

    void updateBankAccountStatus(Long accountId, BankAccountStatusRequest request);

    void deleteBankAccount(Long accountId);
}
