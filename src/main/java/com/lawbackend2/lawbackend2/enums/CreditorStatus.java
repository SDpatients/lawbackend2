package com.lawbackend2.lawbackend2.enums;

public enum CreditorStatus {
    KNOWN("已知债权人"),
    CONFIRMED("确认债权人");

    private final String description;

    CreditorStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
