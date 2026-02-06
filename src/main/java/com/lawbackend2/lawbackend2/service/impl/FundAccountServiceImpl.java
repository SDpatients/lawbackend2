package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundAccountBalanceRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.FundAccountSimpleResponse;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundAccountRepository;
import com.lawbackend2.lawbackend2.service.FundAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FundAccountServiceImpl implements FundAccountService {

    private final FundAccountRepository fundAccountRepository;

    public FundAccountServiceImpl(FundAccountRepository fundAccountRepository) {
        this.fundAccountRepository = fundAccountRepository;
    }

    @Override
    public Long createFundAccount(FundAccountCreateRequest request, Long userId) {
        FundAccount fundAccount = new FundAccount();
        BeanUtils.copyProperties(request, fundAccount);
        fundAccount.setCurrentBalance(request.getInitialBalance());
        fundAccount.setStatus("ACTIVE");
        fundAccount.setCreateUserId(userId);
        fundAccount.setUpdateUserId(userId);
        fundAccount.setAccountId(System.currentTimeMillis());

        FundAccount saved = fundAccountRepository.save(fundAccount);
        return saved.getId();
    }

    @Override
    public PageResult<FundAccount> getFundAccountList(Integer pageNum, Integer pageSize, Long caseId, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<FundAccount> page = fundAccountRepository.findByConditions(caseId, status, pageable);

        PageResult<FundAccount> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public FundAccount getFundAccountDetail(Long fundAccountId) {
        return fundAccountRepository.findById(fundAccountId)
                .orElseThrow(() -> new BusinessException("资金账户不存在"));
    }

    @Override
    public void updateFundAccount(Long fundAccountId, FundAccountUpdateRequest request, Long userId) {
        FundAccount fundAccount = getFundAccountDetail(fundAccountId);
        BeanUtils.copyProperties(request, fundAccount, "id", "initialBalance", "currentBalance", "status");
        fundAccount.setUpdateUserId(userId);
        fundAccountRepository.save(fundAccount);
    }

    @Override
    public void updateFundAccountBalance(Long fundAccountId, FundAccountBalanceRequest request, Long userId) {
        FundAccount fundAccount = getFundAccountDetail(fundAccountId);
        fundAccount.setCurrentBalance(request.getCurrentBalance());
        fundAccount.setUpdateUserId(userId);
        fundAccountRepository.save(fundAccount);
    }

    @Override
    public void updateFundAccountStatus(Long fundAccountId, FundAccountStatusRequest request, Long userId) {
        FundAccount fundAccount = getFundAccountDetail(fundAccountId);
        fundAccount.setStatus(request.getStatus());
        fundAccount.setUpdateUserId(userId);
        fundAccountRepository.save(fundAccount);
    }

    @Override
    public void deleteFundAccount(Long fundAccountId, Long userId) {
        FundAccount fundAccount = getFundAccountDetail(fundAccountId);
        fundAccount.setUpdateUserId(userId);
        fundAccountRepository.delete(fundAccount);
    }

    @Override
    public java.util.List<FundAccountSimpleResponse> getSimpleFundAccountListByCaseId(Long caseId) {
        java.util.List<FundAccount> fundAccounts = fundAccountRepository.findByCaseIdAndIsDeleted(caseId, false);
        return fundAccounts.stream().map(fundAccount -> {
            FundAccountSimpleResponse response = new FundAccountSimpleResponse();
            response.setId(fundAccount.getId());
            response.setAccountName(fundAccount.getAccountName());
            response.setBankAccount(fundAccount.getBankAccount());
            return response;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public FundAccount getFundAccountDetailByAccountName(String accountName) {
        FundAccount fundAccount = fundAccountRepository.findByAccountName(accountName);
        if (fundAccount == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("资金账户不存在");
        }
        return fundAccount;
    }
}
