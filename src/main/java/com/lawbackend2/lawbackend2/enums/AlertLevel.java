package com.lawbackend2.lawbackend2.enums;

public enum AlertLevel {
    NORMAL("正常", "绿色"),
    SOON_DUE("即将到期", "黄色"),
    DUE_TODAY("今日到期", "橙色"),
    OVERDUE("已逾期", "红色");

    private final String description;
    private final String color;

    AlertLevel(String description, String color) {
        this.description = description;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }
}
