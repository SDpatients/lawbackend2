package com.lawbackend2.lawbackend2.enums;

public enum NotificationType {
    SYSTEM_MSG("站内消息"),
    SMS("短信"),
    EMAIL("邮件"),
    MULTI("多渠道");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
