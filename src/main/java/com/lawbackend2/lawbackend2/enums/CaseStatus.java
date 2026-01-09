package com.lawbackend2.lawbackend2.enums;

public enum CaseStatus {
    PENDING("待处理"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    CLOSED("已结案"),
    TERMINATED("已终结"),
    ARCHIVED("已归档");

    private final String description;

    CaseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
