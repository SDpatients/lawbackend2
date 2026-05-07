package com.lawbackend2.lawbackend2.enums;

public enum CalculationBase {
    ACCEPTANCE_DATE("受理日"),
    ANNOUNCEMENT_DATE("公告日"),
    PREV_NODE_COMPLETE("前置节点完成日"),
    MEETING_PASS_DATE("会议通过日"),
    COMPLETION_DATE("完成日"),
    REORGANIZATION_DATE("裁定重整日");

    private final String description;

    CalculationBase(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
