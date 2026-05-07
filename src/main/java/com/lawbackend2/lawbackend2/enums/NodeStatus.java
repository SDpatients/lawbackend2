package com.lawbackend2.lawbackend2.enums;

public enum NodeStatus {
    PENDING("待启动"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    EXTENSION_REQUESTED("延期申请中"),
    EXTENDED("已延期"),
    SKIPPED("已跳过");

    private final String description;

    NodeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
