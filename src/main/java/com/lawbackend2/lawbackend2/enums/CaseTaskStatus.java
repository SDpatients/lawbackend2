package com.lawbackend2.lawbackend2.enums;

public enum CaseTaskStatus {
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    REVIEWING("核审中"),
    SKIPPED("跳过"),
    REJECTED("被驳回");

    private final String description;

    CaseTaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
