package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundAccountBalanceRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundAccount;

/**
 * 基金账户服务接口
 */
public interface FundAccountService {

    /**
     * 创建基金账户
     * @param request 创建请求
     * @param userId 用户ID
     * @return 基金账户ID
     */
    Long createFundAccount(FundAccountCreateRequest request, Long userId);

    /**
     * 获取基金账户列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param caseId 案件ID
     * @param status 状态
     * @return 分页结果
     */
    PageResult<FundAccount> getFundAccountList(Integer pageNum, Integer pageSize, Long caseId, String status);

    /**
     * 获取基金账户详情
     * @param fundAccountId 基金账户ID
     * @return 基金账户详情
     */
    FundAccount getFundAccountDetail(Long fundAccountId);

    /**
     * 更新基金账户
     * @param fundAccountId 基金账户ID
     * @param request 更新请求
     */
    void updateFundAccount(Long fundAccountId, FundAccountUpdateRequest request);

    /**
     * 更新基金账户余额
     * @param fundAccountId 基金账户ID
     * @param request 余额更新请求
     */
    void updateFundAccountBalance(Long fundAccountId, FundAccountBalanceRequest request);

    /**
     * 更新基金账户状态
     * @param fundAccountId 基金账户ID
     * @param request 状态更新请求
     */
    void updateFundAccountStatus(Long fundAccountId, FundAccountStatusRequest request);

    /**
     * 删除基金账户
     * @param fundAccountId 基金账户ID
     */
    void deleteFundAccount(Long fundAccountId);
}
