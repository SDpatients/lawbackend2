package com.lawbackend2.lawbackend2.enums;

import lombok.Getter;

@Getter
public enum SensitiveDataType {
    USER_MOBILE("用户手机号"),
    USER_PHONE("用户联系电话"),
    CREDITOR_ID_NUMBER("债权人身份证号"),
    CREDITOR_CONTACT_PHONE("债权人联系电话"),
    BANK_ACCOUNT_NUMBER("银行账号"),
    AGENT_PHONE("代理人联系电话"),
    AGENT_ID_CARD("代理人身份证号"),
    CREDITOR_BANK_ACCOUNT("债权人银行账号");

    private final String description;

    SensitiveDataType(String description) {
        this.description = description;
    }
}