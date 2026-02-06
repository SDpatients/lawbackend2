package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

/**
 * 资金账户简单响应DTO
 */
@Data
public class FundAccountSimpleResponse {

    /**
     * 资金账户ID
     */
    private Long id;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 银行账号
     */
    private String bankAccount;
}
