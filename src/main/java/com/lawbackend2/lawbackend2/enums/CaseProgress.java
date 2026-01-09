package com.lawbackend2.lawbackend2.enums;

public enum CaseProgress {
    FIRST("第一阶段"),
    SECOND("第二阶段"),
    THIRD("第三阶段"),
    FOURTH("第四阶段"),
    FIFTH("第五阶段"),
    SIXTH("第六阶段"),
    SEVENTH("第七阶段");

    private final String description;

    CaseProgress(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
