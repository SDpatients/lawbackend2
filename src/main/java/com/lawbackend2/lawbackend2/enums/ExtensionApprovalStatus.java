package com.lawbackend2.lawbackend2.enums;

public enum ExtensionApprovalStatus {
    PENDING("待审批"),
    APPROVED("已通过"),
    REJECTED("已驳回");

    private final String description;

    ExtensionApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
