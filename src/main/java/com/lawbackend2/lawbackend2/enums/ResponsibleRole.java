package com.lawbackend2.lawbackend2.enums;

public enum ResponsibleRole {
    ADMINISTRATOR("管理人"),
    COURT("法院"),
    CREDITOR_MEETING("债权人会议"),
    APPLICANT("申请人");

    private final String description;

    ResponsibleRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
