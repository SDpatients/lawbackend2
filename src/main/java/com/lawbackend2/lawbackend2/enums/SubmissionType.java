package com.lawbackend2.lawbackend2.enums;

public enum SubmissionType {
    NORMAL("普通提交"),
    REVISION("修订提交");

    private final String description;

    SubmissionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
