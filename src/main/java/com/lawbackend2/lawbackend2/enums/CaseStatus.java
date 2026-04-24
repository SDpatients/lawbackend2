package com.lawbackend2.lawbackend2.enums;

/**
 * 案件状态枚举
 * 优化后的状态管理：精简为5个核心状态
 *
 * 状态说明：
 * - PENDING: 待处理（案件刚创建，尚未开始处理）
 * - ONGOING: 进行中（案件正在正常办理中，包含原IN_PROGRESS和APPROVED状态）
 * - AWAITING: 报结中（已提交报结申请，等待管理员审核）
 * - COMPLETED: 已结案（案件流程已结束，包含原CLOSED和TERMINATED状态）
 * - ARCHIVED: 已归档（案件已归档，仅可查看）
 */
public enum CaseStatus {
    PENDING("待处理"),
    ONGOING("进行中"),
    AWAITING("报结中"),
    COMPLETED("已结案"),
    ARCHIVED("已归档");

    private final String description;

    CaseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否为进行中状态（可以编辑和操作）
     */
    public boolean isInProgress() {
        return this == PENDING || this == ONGOING;
    }

    /**
     * 是否为已结束状态（不可编辑）
     */
    public boolean isFinished() {
        return this == COMPLETED || this == ARCHIVED;
    }

    /**
     * 是否可以编辑案件信息
     */
    public boolean isEditable() {
        return this == PENDING || this == ONGOING;
    }

    /**
     * 是否可以提交报结申请
     */
    public boolean canSubmitForReview() {
        return this == ONGOING;
    }

    /**
     * 是否为报结中状态
     */
    public boolean isAwaiting() {
        return this == AWAITING;
    }

    /**
     * 是否可以进行业务操作（任务、报销、公告等）
     */
    public boolean canDoBusiness() {
        return this == PENDING || this == ONGOING;
    }

    /**
     * 根据状态编码获取枚举
     */
    public static CaseStatus fromString(String status) {
        if (status == null) {
            return null;
        }
        try {
            return CaseStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            // 兼容旧状态映射
            return mapLegacyStatus(status);
        }
    }

    /**
     * 兼容旧状态映射
     * 将旧状态映射到新状态
     */
    private static CaseStatus mapLegacyStatus(String legacyStatus) {
        if (legacyStatus == null) {
            return null;
        }
        switch (legacyStatus) {
            case "IN_PROGRESS":
            case "APPROVED":
                return ONGOING;
            case "CLOSED":
            case "TERMINATED":
                return COMPLETED;
            default:
                return null;
        }
    }
}
