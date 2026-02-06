package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import com.lawbackend2.lawbackend2.common.PageResult;

import java.time.LocalDate;

public interface BankAccountTransactionService {

    Long createTransaction(BankAccountTransactionCreateRequest request, Long userId);

    PageResult<BankAccountTransactionResponse> getTransactionList(Integer pageNum, Integer pageSize, Long accountId, String transactionType, String businessType, LocalDate startDate, LocalDate endDate, Long caseId, Long userId);

    BankAccountTransaction getTransactionDetail(Long transactionId, Long userId);

    void updateTransaction(Long transactionId, BankAccountTransactionUpdateRequest request, Long userId);

    void deleteTransaction(Long transactionId, Long userId);
}
