package com.lawbackend2.lawbackend2.enums;

public enum CaseType {
    LIQUIDATION("清算"),
    REORGANIZATION("重整"),
    COMPROMISE("和解"),
    COMMON("通用");

    private final String description;

    CaseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
