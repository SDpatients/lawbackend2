-- ========================================
-- 用户协议同意记录表
-- 记录用户对隐私政策和用户协议的同意操作
-- ========================================

CREATE TABLE IF NOT EXISTS `tb_user_agreement_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `user_account` VARCHAR(100) DEFAULT NULL COMMENT '用户账号',
    `agreement_type` VARCHAR(50) DEFAULT NULL COMMENT '协议类型: PRIVACY_POLICY-隐私政策, USER_AGREEMENT-用户协议',
    `agreement_version` VARCHAR(50) DEFAULT NULL COMMENT '协议版本号，如 v1.0, v2.0',
    `agreed` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否同意: 1-同意, 0-不同意',
    `agreement_content` TEXT DEFAULT NULL COMMENT '同意时的协议内容快照',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '同意时的IP地址',
    `agree_time` DATETIME DEFAULT NULL COMMENT '同意时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '软删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_user_id` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    INDEX `idx_uagr_user_id` (`user_id`),
    INDEX `idx_uagr_agreement_type` (`agreement_type`),
    INDEX `idx_uagr_agree_time` (`agree_time`),
    INDEX `idx_uagr_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户协议同意记录表';