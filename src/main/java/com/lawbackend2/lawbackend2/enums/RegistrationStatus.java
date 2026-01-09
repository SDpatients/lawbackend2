package com.lawbackend2.lawbackend2.enums;

public enum RegistrationStatus {
    PENDING("待登记"),
    REGISTERED("已登记"),
    REJECTED("已驳回");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
