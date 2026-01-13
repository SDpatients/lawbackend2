package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpensePaymentRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptcyExpense;

public interface BankruptcyExpenseService {

    Long createBankruptcyExpense(BankruptcyExpenseCreateRequest request);

    PageResult<BankruptcyExpense> getBankruptcyExpenseList(Integer pageNum, Integer pageSize, Long caseId, String expenseType, String approvalStatus, String paymentStatus);

    BankruptcyExpense getBankruptcyExpenseDetail(Long expenseId);

    void updateBankruptcyExpense(Long expenseId, BankruptcyExpenseUpdateRequest request);

    void approveBankruptcyExpense(Long expenseId, BankruptcyExpenseApprovalRequest request);

    void payBankruptcyExpense(Long expenseId, BankruptcyExpensePaymentRequest request);

    void deleteBankruptcyExpense(Long expenseId);
}