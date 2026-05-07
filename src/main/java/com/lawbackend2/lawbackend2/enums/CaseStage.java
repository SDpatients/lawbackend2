package com.lawbackend2.lawbackend2.enums;

public enum CaseStage {
    ACCEPTANCE("受理阶段"),
    LIQUIDATION("清算阶段"),
    REORGANIZATION("重整阶段"),
    COMPROMISE("和解阶段"),
    TERMINATION("终结阶段");

    private final String description;

    CaseStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
