/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : law

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 06/06/2026 09:24:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_activity
-- ----------------------------
DROP TABLE IF EXISTS `tb_activity`;
CREATE TABLE `tb_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `related_id` bigint NULL DEFAULT NULL,
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_administrator
-- ----------------------------
DROP TABLE IF EXISTS `tb_administrator`;
CREATE TABLE `tb_administrator`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理人ID',
  `administrator_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理人名称（机构或团队名称）',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `administrator_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理人类型',
  `responsible_person_id` bigint NULL DEFAULT NULL COMMENT '负责人ID',
  `contact_phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `office_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '办公地址',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `responsible_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_responsible_person_id`(`responsible_person_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_administrator_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_administrator_responsible_person` FOREIGN KEY (`responsible_person_id`) REFERENCES `tb_administrator_staff` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理人信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_administrator_staff
-- ----------------------------
DROP TABLE IF EXISTS `tb_administrator_staff`;
CREATE TABLE `tb_administrator_staff`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `administrator_id` bigint NULL DEFAULT NULL COMMENT '所属管理人ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `staff_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人员类型',
  `id_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `lawyer_license_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '律师执业证号',
  `contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `responsibility` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职责',
  `appointment_date` date NULL DEFAULT NULL COMMENT '任命日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联的用户ID（系统登录账号）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_administrator_id`(`administrator_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_staff_type`(`staff_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `fk_staff_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_administrator_staff_administrator` FOREIGN KEY (`administrator_id`) REFERENCES `tb_administrator` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_staff_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_message`;
CREATE TABLE `tb_ai_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息内容',
  `sender_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '发送者类型: user-用户, ai-人工智能',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送者ID',
  `timestamp` datetime NOT NULL COMMENT '消息时间戳',
  `message_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'SENT' COMMENT '消息状态: SENT-已发送, DELIVERED-已送达, READ-已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_timestamp`(`timestamp` ASC) USING BTREE,
  INDEX `idx_sender_type`(`sender_type` ASC) USING BTREE,
  CONSTRAINT `fk_message_session` FOREIGN KEY (`session_id`) REFERENCES `tb_ai_chat_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'AI聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_session`;
CREATE TABLE `tb_ai_chat_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话名称',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'AI聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_approval
-- ----------------------------
DROP TABLE IF EXISTS `tb_approval`;
CREATE TABLE `tb_approval`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `lawyer_id` bigint NOT NULL COMMENT '律师ID',
  `approval_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '审核类型: CASE_SUBMIT-案件提交, CASE_CLOSE-案件结案, FEE_APPLY-费用申请, EVIDENCE_UPLOAD-证据上传',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消',
  `approval_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核标题',
  `approval_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审核内容',
  `approval_attachment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核结果: PASS-通过, FAIL-未通过',
  `approval_count` int NULL DEFAULT 0 COMMENT '审核次数',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `approval_date` datetime NULL DEFAULT NULL COMMENT '审核日期',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_lawyer_id`(`lawyer_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE,
  INDEX `idx_approval_date`(`approval_date` ASC) USING BTREE,
  INDEX `idx_case_type`(`case_id` ASC, `approval_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '审批管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_approval_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_approval_history`;
CREATE TABLE `tb_approval_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `approval_id` bigint NOT NULL COMMENT '审批管理表ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `approver_id` bigint NOT NULL COMMENT '审核人ID',
  `approval_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '审核类型: CASE_SUBMIT-案件提交, CASE_CLOSE-案件结案, FEE_APPLY-费用申请, EVIDENCE_UPLOAD-证据上传',
  `approval_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核标题',
  `approval_attachment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消',
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审核意见',
  `approval_date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_approval_id`(`approval_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE,
  INDEX `idx_approval_date`(`approval_date` ASC) USING BTREE,
  CONSTRAINT `fk_approval_history_approval` FOREIGN KEY (`approval_id`) REFERENCES `tb_approval` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '审批历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_archive_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_archive_category`;
CREATE TABLE `tb_archive_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类代码',
  `category_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级分类ID,NULL表示顶级分类',
  `level` tinyint NOT NULL DEFAULT 1 COMMENT '分类层级:1-一级分类,2-二级分类,3-三级分类',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号,数字越小越靠前',
  `is_required` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否必填:0-否,1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '分类描述',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_code`(`category_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '归档分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_archive_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_archive_record`;
CREATE TABLE `tb_archive_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '归档分类代码',
  `file_id` bigint NOT NULL COMMENT '文件ID(关联tb_file_record表)',
  `archive_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '归档编号,格式:AH-YYYYMMDD-XXXX',
  `file_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件标题',
  `file_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文件描述',
  `upload_user_id` bigint NOT NULL COMMENT '上传人ID',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '记录状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_confidential` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否机密:0-否,1-是',
  `access_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'INTERNAL' COMMENT '访问级别: PUBLIC-公开, INTERNAL-内部, CONFIDENTIAL-机密, TOP_SECRET-绝密',
  `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
  `parent_version_id` bigint NULL DEFAULT NULL COMMENT '父版本ID(用于版本回溯)',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_category_code`(`category_code` ASC) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_archive_no`(`archive_no` ASC) USING BTREE,
  INDEX `idx_upload_user_id`(`upload_user_id` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_access_level`(`access_level` ASC) USING BTREE,
  INDEX `idx_version`(`version` ASC) USING BTREE,
  CONSTRAINT `fk_archive_record_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_archive_record_file` FOREIGN KEY (`file_id`) REFERENCES `tb_file_record` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '归档记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_audit_log`;
CREATE TABLE `tb_audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `browser` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `business_id` bigint NULL DEFAULT NULL,
  `business_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `business_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `data_after` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `data_before` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `duration` bigint NULL DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `module_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operation_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `chain_sequence` bigint NULL DEFAULT NULL,
  `digital_signature` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `hash_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `integrity_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `previous_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `signed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `signed_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_audit_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_audit_module`(`module` ASC) USING BTREE,
  INDEX `idx_audit_operation`(`operation_type` ASC) USING BTREE,
  INDEX `idx_audit_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_audit_status`(`status` ASC) USING BTREE,
  INDEX `idx_audit_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 511 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '审计日志表 - 采用哈希链和数字签名技术确保不可篡改，通过数据库触发器防止删除和篡改' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_audit_log_archive
-- ----------------------------
DROP TABLE IF EXISTS `tb_audit_log_archive`;
CREATE TABLE `tb_audit_log_archive`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `browser` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `business_id` bigint NULL DEFAULT NULL,
  `business_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `business_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `data_after` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `data_before` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `duration` bigint NULL DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `module_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operation_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `chain_sequence` bigint NULL DEFAULT NULL,
  `digital_signature` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `hash_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `integrity_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `previous_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `signed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `signed_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_audit_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_audit_module`(`module` ASC) USING BTREE,
  INDEX `idx_audit_operation`(`operation_type` ASC) USING BTREE,
  INDEX `idx_audit_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_audit_status`(`status` ASC) USING BTREE,
  INDEX `idx_audit_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '审计日志表 - 采用哈希链和数字签名技术确保不可篡改，通过数据库触发器防止删除和修改' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_backup_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_backup_record`;
CREATE TABLE `tb_backup_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `backup_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `database_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `duration` bigint NULL DEFAULT NULL,
  `end_time` datetime(6) NULL DEFAULT NULL,
  `error_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `file_size` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `record_count` bigint NULL DEFAULT NULL,
  `start_time` datetime(6) NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `table_count` int NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_backup_status`(`status` ASC) USING BTREE,
  INDEX `idx_backup_type`(`backup_type` ASC) USING BTREE,
  INDEX `idx_backup_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_backup_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_bank_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_bank_account`;
CREATE TABLE `tb_bank_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账户名称',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `account_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账户类型',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '银行名称',
  `closing_date` date NULL DEFAULT NULL COMMENT '销户日期',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '币种',
  `current_balance` decimal(18, 2) NULL DEFAULT NULL COMMENT '当前余额',
  `opening_date` date NULL DEFAULT NULL COMMENT '开户日期',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_3w6o9b9i8bk4etdwohas3yjsu`(`account_number` ASC) USING BTREE,
  INDEX `idx_account_number`(`account_number` ASC) USING BTREE,
  INDEX `idx_account_name`(`account_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_bank_account_transaction
-- ----------------------------
DROP TABLE IF EXISTS `tb_bank_account_transaction`;
CREATE TABLE `tb_bank_account_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint NOT NULL COMMENT '账户ID',
  `transaction_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '交易类型(IN-流入/OUT-流出)',
  `amount` decimal(18, 2) NOT NULL COMMENT '交易金额',
  `transaction_date` date NOT NULL COMMENT '交易日期',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '交易摘要',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务类型(收款/付款/转账/利息收入/手续费等)',
  `counterparty_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '对方账户',
  `counterparty_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '对方名称',
  `balance_after` decimal(18, 2) NULL DEFAULT NULL COMMENT '交易后余额',
  `attachment_id` bigint NULL DEFAULT NULL COMMENT '附件ID',
  `related_business_id` bigint NULL DEFAULT NULL COMMENT '关联业务单据ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_transaction_type`(`transaction_type` ASC) USING BTREE,
  INDEX `idx_transaction_date`(`transaction_date` ASC) USING BTREE,
  INDEX `idx_business_type`(`business_type` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_bank_account_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `tb_bank_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '账户流入流出明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_bankrupt_case
-- ----------------------------
DROP TABLE IF EXISTS `tb_bankrupt_case`;
CREATE TABLE `tb_bankrupt_case`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '案件ID',
  `case_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案号',
  `case_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案件名称',
  `acceptance_date` date NULL DEFAULT NULL COMMENT '受理日期',
  `case_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案件来源',
  `acceptance_court` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '受理法院',
  `designated_institution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指定机构',
  `main_responsible_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主要负责人',
  `is_simplified_trial` tinyint(1) NULL DEFAULT 0 COMMENT '是否简化审: 0-否, 1-是',
  `case_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案由',
  `case_progress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案件进度: FIRST-第一阶段, SECOND-第二阶段, THIRD-第三阶段, FOURTH-第四阶段, FIFTH-第五阶段, SIXTH-第六阶段, SEVENTH-第七阶段',
  `debt_claim_deadline` datetime NULL DEFAULT NULL COMMENT '债权申报截止时间',
  `filing_date` date NULL DEFAULT NULL COMMENT '立案日期',
  `closing_date` date NULL DEFAULT NULL COMMENT '结案日期',
  `bankruptcy_date` date NULL DEFAULT NULL COMMENT '破产时间',
  `termination_date` date NULL DEFAULT NULL COMMENT '终结时间',
  `cancellation_date` date NULL DEFAULT NULL COMMENT '注销时间',
  `archiving_date` date NULL DEFAULT NULL COMMENT '归档时间',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `file_upload_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件上传路径',
  `undertaking_personnel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '承办人员',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `creator_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `review_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已驳回',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  `review_count` int NULL DEFAULT 0 COMMENT '审核次数',
  `case_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ONGOING' COMMENT 'ONGOING-在办，AWAITING-报结，COMPLETED已结',
  `designated_judge` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指定法官',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `case_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_case_number`(`case_number` ASC) USING BTREE,
  UNIQUE INDEX `UK_dh02kem1d6yiv78lwrlf0cdgi`(`case_number` ASC) USING BTREE,
  INDEX `idx_case_status`(`case_status` ASC) USING BTREE,
  INDEX `idx_acceptance_date`(`acceptance_date` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '案件信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_bankruptcy_expense
-- ----------------------------
DROP TABLE IF EXISTS `tb_bankruptcy_expense`;
CREATE TABLE `tb_bankruptcy_expense`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `applied_amount` decimal(18, 2) NULL DEFAULT NULL,
  `apply_date` datetime(6) NULL DEFAULT NULL,
  `approval_date` datetime(6) NULL DEFAULT NULL,
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approved_amount` decimal(18, 2) NULL DEFAULT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `basis_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `basis_document` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_basis` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_date` datetime(6) NULL DEFAULT NULL,
  `expense_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `expense_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_purpose` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `expense_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `paid_amount` decimal(18, 2) NULL DEFAULT NULL,
  `payee_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payee_bank` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payee_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_account_id` bigint NULL DEFAULT NULL,
  `payment_date` datetime(6) NULL DEFAULT NULL,
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_voucher` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `related_business_id` bigint NULL DEFAULT NULL,
  `related_business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `related_flow_id` bigint NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `unpaid_amount` decimal(18, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_ggkixoew6aq3glxcjyn6q1907`(`expense_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_expense_no`(`expense_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_expense_type`(`expense_type` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE,
  INDEX `idx_payment_date`(`payment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_announcement
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_announcement`;
CREATE TABLE `tb_case_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `announcement_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公告类型: NOTICE-通知, ANNOUNCEMENT-公告, WARNING-警告',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, ARCHIVED-已归档, DELETED-已删除',
  `publisher_id` bigint NULL DEFAULT NULL COMMENT '发布者ID',
  `publisher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布者姓名',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
  `top_expire_time` datetime NULL DEFAULT NULL COMMENT '置顶过期时间',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '附件',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `case_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `principal_officer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_announcement_type`(`announcement_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  INDEX `idx_is_top`(`is_top` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_case_announcement_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '案件公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_announcement_view
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_announcement_view`;
CREATE TABLE `tb_case_announcement_view`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `announcement_id` bigint NULL DEFAULT NULL,
  `announcement_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `browser_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `os_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `view_duration` int NULL DEFAULT 0,
  `view_time` datetime(6) NULL DEFAULT NULL,
  `viewer_id` bigint NULL DEFAULT NULL,
  `viewer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `viewer_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 342 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_node_alert_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_node_alert_record`;
CREATE TABLE `tb_case_node_alert_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_instance_id` bigint NOT NULL COMMENT '节点实例ID, 关联tb_case_node_instance',
  `case_id` bigint NOT NULL COMMENT '案件ID, 关联tb_bankrupt_case',
  `alert_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '预警级别: SOON_DUE-即将到期, DUE_TODAY-今日到期, OVERDUE-已逾期',
  `remaining_days` int NOT NULL COMMENT '剩余天数(计算得出, 逾期时为负数)',
  `deadline_date` date NOT NULL COMMENT '截止日期',
  `alert_date` date NOT NULL COMMENT '预警触发日期',
  `is_notified` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已发送通知: 0-否, 1-是',
  `notification_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '通知类型: SYSTEM_MSG-站内消息, SMS-短信, EMAIL-邮件, MULTI-多渠道',
  `notification_time` datetime NULL DEFAULT NULL COMMENT '通知发送时间',
  `notification_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知内容',
  `recipient_id` bigint NULL DEFAULT NULL COMMENT '通知接收人ID, 关联tb_user',
  `recipient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知接收人姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_node_instance_id`(`node_instance_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_alert_level`(`alert_level` ASC) USING BTREE,
  INDEX `idx_alert_date`(`alert_date` ASC) USING BTREE,
  INDEX `idx_is_notified`(`is_notified` ASC) USING BTREE,
  INDEX `idx_recipient_id`(`recipient_id` ASC) USING BTREE,
  INDEX `idx_case_id_alert_date`(`case_id` ASC, `alert_date` ASC) USING BTREE,
  CONSTRAINT `fk_case_node_alert_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_alert_node` FOREIGN KEY (`node_instance_id`) REFERENCES `tb_case_node_instance` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '节点预警记录表: 记录节点预警的历史触发记录, 用于审计和通知追踪' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_node_extension
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_node_extension`;
CREATE TABLE `tb_case_node_extension`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_instance_id` bigint NOT NULL COMMENT '节点实例ID, 关联tb_case_node_instance',
  `case_id` bigint NOT NULL COMMENT '案件ID, 关联tb_bankrupt_case',
  `extension_days` int NOT NULL COMMENT '申请延期天数',
  `original_deadline` date NOT NULL COMMENT '原截止日期',
  `new_deadline` date NOT NULL COMMENT '延期后新截止日期',
  `apply_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请理由',
  `attachment_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '附件路径',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已通过, REJECTED-已驳回',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID, 关联tb_user',
  `approver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审批意见',
  `apply_user_id` bigint NOT NULL COMMENT '申请人ID, 关联tb_user',
  `apply_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人姓名',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_node_instance_id`(`node_instance_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE,
  INDEX `idx_apply_user_id`(`apply_user_id` ASC) USING BTREE,
  INDEX `idx_apply_time`(`apply_time` ASC) USING BTREE,
  INDEX `idx_case_id_approval_status`(`case_id` ASC, `approval_status` ASC) USING BTREE,
  CONSTRAINT `fk_case_node_extension_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_extension_node` FOREIGN KEY (`node_instance_id`) REFERENCES `tb_case_node_instance` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '节点延期申请表: 记录案件节点的延期申请及审批结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_node_instance
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_node_instance`;
CREATE TABLE `tb_case_node_instance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID, 关联tb_bankrupt_case',
  `template_id` bigint NOT NULL COMMENT '节点模板ID, 关联tb_case_node_template',
  `node_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '节点编码, 冗余存储便于查询',
  `node_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点名称, 冗余存储便于查询',
  `node_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'PENDING' COMMENT '节点状态: PENDING-待启动, IN_PROGRESS-进行中, COMPLETED-已完成, EXTENSION_REQUESTED-延期申请中, EXTENDED-已延期, SKIPPED-已跳过',
  `start_date` date NULL DEFAULT NULL COMMENT '实际开始日期',
  `deadline_date` date NOT NULL COMMENT '截止日期(法定期限计算得出)',
  `completed_date` date NULL DEFAULT NULL COMMENT '实际完成日期',
  `extension_count` int NOT NULL DEFAULT 0 COMMENT '延期次数',
  `extension_days` int NOT NULL DEFAULT 0 COMMENT '累计延期天数',
  `responsible_person_id` bigint NULL DEFAULT NULL COMMENT '责任人ID, 关联tb_user',
  `responsible_person_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '责任人姓名',
  `completion_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '完成备注',
  `alert_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '预警级别: NORMAL-正常(>3天), SOON_DUE-即将到期(1-3天), DUE_TODAY-今日到期(0天), OVERDUE-已逾期(<0天)',
  `alert_triggered_at` datetime NULL DEFAULT NULL COMMENT '预警触发时间',
  `prev_node_instance_id` bigint NULL DEFAULT NULL COMMENT '前置节点实例ID, 用于节点依赖关系',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_node_code`(`node_code` ASC) USING BTREE,
  INDEX `idx_node_status`(`node_status` ASC) USING BTREE,
  INDEX `idx_deadline_date`(`deadline_date` ASC) USING BTREE,
  INDEX `idx_alert_level`(`alert_level` ASC) USING BTREE,
  INDEX `idx_responsible_person_id`(`responsible_person_id` ASC) USING BTREE,
  INDEX `idx_prev_node_instance_id`(`prev_node_instance_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_case_id_node_status`(`case_id` ASC, `node_status` ASC) USING BTREE,
  INDEX `idx_case_id_alert_level`(`case_id` ASC, `alert_level` ASC) USING BTREE,
  CONSTRAINT `fk_case_node_instance_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_instance_prev` FOREIGN KEY (`prev_node_instance_id`) REFERENCES `tb_case_node_instance` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_instance_template` FOREIGN KEY (`template_id`) REFERENCES `tb_case_node_template` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '案件节点实例表: 每个破产案件根据模板生成的实际节点实例' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_node_template
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_node_template`;
CREATE TABLE `tb_case_node_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '节点编码, 如: CLAIM_FILING_PERIOD',
  `node_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点名称, 如: 债权申报期限',
  `node_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '节点描述, 说明该节点的业务内容和要求',
  `case_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '适用案件类型: LIQUIDATION-清算, REORGANIZATION-重整, COMPROMISE-和解, COMMON-通用',
  `case_stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '案件阶段: ACCEPTANCE-受理阶段, LIQUIDATION-清算阶段, REORGANIZATION-重整阶段, COMPROMISE-和解阶段, TERMINATION-终结阶段',
  `legal_deadline_days` int NOT NULL COMMENT '法定期限天数, 如: 30, 90, 180等',
  `legal_deadline_days_max` int NULL DEFAULT NULL COMMENT '法定期限最大天数(区间时使用), 如债权申报30-90天',
  `calculation_base` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '起算规则: ACCEPTANCE_DATE-受理日, ANNOUNCEMENT_DATE-公告日, PREV_NODE_COMPLETE-前置节点完成日, MEETING_PASS_DATE-会议通过日, COMPLETION_DATE-完成日, REORGANIZATION_DATE-裁定重整日',
  `prev_node_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '前置节点编码, 依赖前置节点完成才能启动',
  `responsible_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '责任角色: ADMINISTRATOR-管理人, COURT-法院, CREDITOR_MEETING-债权人会议, APPLICANT-申请人',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号, 同一阶段内按此排序',
  `is_mandatory` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否必经节点: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_node_code_case_type`(`node_code` ASC, `case_type` ASC) USING BTREE,
  INDEX `idx_case_type`(`case_type` ASC) USING BTREE,
  INDEX `idx_case_stage`(`case_stage` ASC) USING BTREE,
  INDEX `idx_calculation_base`(`calculation_base` ASC) USING BTREE,
  INDEX `idx_prev_node_code`(`prev_node_code` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '案件节点模板表: 定义各类破产案件的法定节点标准模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_process_stage
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_process_stage`;
CREATE TABLE `tb_case_process_stage`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `attachments` json NULL,
  `case_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `field_data` json NOT NULL,
  `module_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `process_date` datetime(6) NULL DEFAULT NULL,
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `stage_num` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_progress
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_progress`;
CREATE TABLE `tb_case_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `completed_at` datetime(6) NULL DEFAULT NULL,
  `completed_by` bigint NULL DEFAULT NULL,
  `completed_by_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `completed_tasks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `completion_percentage` int NULL DEFAULT 0,
  `end_date` date NULL DEFAULT NULL,
  `expected_end_date` date NULL DEFAULT NULL,
  `is_completed` bit(1) NULL DEFAULT NULL,
  `issues` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `key_tasks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `pending_tasks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `progress_stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `progress_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `responsible_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `responsible_person_id` bigint NULL DEFAULT NULL,
  `solutions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `stage_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `stage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `start_date` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_task`;
CREATE TABLE `tb_case_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID，关联tb_bankrupt_case表',
  `task_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务编号: TASK_001-TASK_023',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `task_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '任务状态: IN_PROGRESS-进行中, COMPLETED-已完成, REVIEWING-核审中, SKIPPED-跳过, REJECTED-被驳回',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status_audit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_case_task_code`(`case_id` ASC, `task_code` ASC) USING BTREE COMMENT '同一案件下任务编号唯一',
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE COMMENT '案件ID索引',
  INDEX `idx_task_code`(`task_code` ASC) USING BTREE COMMENT '任务编号索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '任务状态索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE COMMENT '排序序号索引',
  CONSTRAINT `fk_case_task_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 832 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '案件任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_case_task_submission
-- ----------------------------
DROP TABLE IF EXISTS `tb_case_task_submission`;
CREATE TABLE `tb_case_task_submission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_task_id` bigint NOT NULL COMMENT '任务ID，关联tb_case_task表',
  `submission_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提交标题',
  `submission_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提交内容',
  `submission_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL' COMMENT '提交类型：NORMAL-普通提交，REVISION-修订提交',
  `submission_number` int NULL DEFAULT 1 COMMENT '提交序号，同一个任务的第几次提交',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '提交状态：PENDING-待审核，APPROVED-已通过，REJECTED-已驳回',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `review_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核意见',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `record_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE' COMMENT '记录状态：ACTIVE-有效，DELETED-已删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_task_id`(`case_task_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_record_status`(`record_status` ASC) USING BTREE,
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_case_task_id_submission_number`(`case_task_id` ASC, `submission_number` ASC) USING BTREE,
  CONSTRAINT `fk_case_task_submission_task` FOREIGN KEY (`case_task_id`) REFERENCES `tb_case_task` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 121 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '案件任务提交记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_chat_message`;
CREATE TABLE `tb_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `conversation_id` bigint NOT NULL,
  `deleted_by` bigint NULL DEFAULT NULL,
  `deleted_time` datetime(6) NULL DEFAULT NULL,
  `file_id` bigint NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `file_size` bigint NULL DEFAULT NULL,
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_recalled` bit(1) NULL DEFAULT NULL,
  `message_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `read_time` datetime(6) NULL DEFAULT NULL,
  `recall_time` datetime(6) NULL DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_message_status`(`message_status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  INDEX `idx_is_recalled`(`is_recalled` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_claim_confirmation
-- ----------------------------
DROP TABLE IF EXISTS `tb_claim_confirmation`;
CREATE TABLE `tb_claim_confirmation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `claim_registration_id` bigint NOT NULL COMMENT '债权申报ID',
  `confirmation_attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '确认附件',
  `confirmation_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '确认状态',
  `court_ruling_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '法院裁定金额',
  `court_ruling_date` datetime(6) NULL DEFAULT NULL COMMENT '法院裁定日期',
  `court_ruling_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '法院裁定文号',
  `court_ruling_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '法院裁定备注',
  `court_ruling_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '法院裁定结果',
  `creditor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '债权人名称',
  `final_confirmation_basis` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最终确认依据',
  `final_confirmation_date` datetime(6) NULL DEFAULT NULL COMMENT '最终确认日期',
  `final_confirmed_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '最终确认金额',
  `has_lawsuit` bit(1) NULL DEFAULT NULL COMMENT '是否有诉讼',
  `has_objection` bit(1) NULL DEFAULT NULL COMMENT '是否有异议',
  `lawsuit_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '诉讼金额',
  `lawsuit_case_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '诉讼案号',
  `lawsuit_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '诉讼备注',
  `lawsuit_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '诉讼结果',
  `lawsuit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '诉讼状态',
  `meeting_date` datetime(6) NULL DEFAULT NULL COMMENT '会议日期',
  `meeting_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议地点',
  `meeting_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议类型',
  `negotiation_date` datetime(6) NULL DEFAULT NULL COMMENT '协商日期',
  `negotiation_participants` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '协商参与人',
  `negotiation_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '协商结果',
  `objection_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '异议金额',
  `objection_date` datetime(6) NULL DEFAULT NULL COMMENT '异议日期',
  `objection_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '异议原因',
  `objector` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异议人',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '备注',
  `vote_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '投票备注',
  `vote_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '投票结果',
  `confirmed_interest` decimal(18, 2) NULL DEFAULT NULL,
  `confirmed_other_losses` decimal(18, 2) NULL DEFAULT NULL,
  `confirmed_penalty` decimal(18, 2) NULL DEFAULT NULL,
  `confirmed_principal` decimal(18, 2) NULL DEFAULT NULL,
  `confirmed_total_amount` decimal(18, 2) NULL DEFAULT NULL,
  `declared_interest` decimal(18, 2) NULL DEFAULT NULL,
  `declared_other_losses` decimal(18, 2) NULL DEFAULT NULL,
  `declared_penalty` decimal(18, 2) NULL DEFAULT NULL,
  `declared_principal` decimal(18, 2) NULL DEFAULT NULL,
  `declared_total_amount` decimal(18, 2) NULL DEFAULT NULL,
  `unconfirmed_interest` decimal(18, 2) NULL DEFAULT NULL,
  `unconfirmed_other_losses` decimal(18, 2) NULL DEFAULT NULL,
  `unconfirmed_penalty` decimal(18, 2) NULL DEFAULT NULL,
  `unconfirmed_principal` decimal(18, 2) NULL DEFAULT NULL,
  `unconfirmed_total_amount` decimal(18, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_claim_registration
-- ----------------------------
DROP TABLE IF EXISTS `tb_claim_registration`;
CREATE TABLE `tb_claim_registration`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '债权申报ID',
  `claim_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申报编号',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `case_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案件名称',
  `debtor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '债务人',
  `creditor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '债权人名称',
  `creditor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '债权人类型',
  `credit_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '信用代码',
  `legal_representative` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '法定代表人',
  `service_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '送达地址',
  `agent_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '代理人姓名',
  `agent_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `agent_id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `agent_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '代理人地址',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账户名称',
  `creditor_bank_account` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户银行',
  `principal` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报本金',
  `interest` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报利息',
  `penalty` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报罚金',
  `other_losses` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报其他损失',
  `total_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报总金额',
  `has_court_judgment` tinyint(1) NULL DEFAULT 0 COMMENT '是否有法院判决: 0-否, 1-是',
  `has_execution` tinyint(1) NULL DEFAULT 0 COMMENT '是否有执行: 0-否, 1-是',
  `has_collateral` tinyint(1) NULL DEFAULT 0 COMMENT '是否有担保: 0-否, 1-是',
  `claim_nature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '债权性质',
  `claim_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '债权类型',
  `claim_facts` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '债权事实',
  `claim_identifier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '债权标识',
  `evidence_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '证据清单',
  `evidence_materials` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '证据材料',
  `evidence_attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '证据附件',
  `registration_date` datetime NULL DEFAULT NULL COMMENT '申报日期',
  `registration_deadline` datetime NULL DEFAULT NULL COMMENT '申报截止日期',
  `material_receiver` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '材料接收人',
  `material_receive_date` datetime NULL DEFAULT NULL COMMENT '材料接收日期',
  `material_completeness` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '材料完整性: COMPLETE-完整, INCOMPLETE-不完整, PENDING-待补充',
  `registration_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '登记状态: PENDING-待登记, REGISTERED-已登记, REJECTED-已驳回',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_claim_no`(`claim_no` ASC) USING BTREE,
  UNIQUE INDEX `UK_g5pxf5sg7irkc2l8xtb4fy4wk`(`claim_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_creditor_name`(`creditor_name` ASC) USING BTREE,
  INDEX `idx_claim_type`(`claim_type` ASC) USING BTREE,
  INDEX `idx_registration_status`(`registration_status` ASC) USING BTREE,
  INDEX `idx_total_amount`(`total_amount` ASC) USING BTREE,
  INDEX `idx_registration_date`(`registration_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_claim_registration_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 93 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '债权申报登记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_claim_review
-- ----------------------------
DROP TABLE IF EXISTS `tb_claim_review`;
CREATE TABLE `tb_claim_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `adjustment_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '调整原因',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `claim_registration_id` bigint NOT NULL COMMENT '债权申报ID',
  `collateral_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '担保金额',
  `collateral_property` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '担保物',
  `collateral_term` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '担保期限',
  `collateral_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '担保类型',
  `collateral_validity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '担保有效性',
  `confirmed_claim_nature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '确认债权性质',
  `confirmed_interest` decimal(18, 2) NULL DEFAULT NULL COMMENT '确认利息',
  `confirmed_other_losses` decimal(18, 2) NULL DEFAULT NULL COMMENT '确认其他损失',
  `confirmed_penalty` decimal(18, 2) NULL DEFAULT NULL COMMENT '确认违约金',
  `confirmed_principal` decimal(18, 2) NULL DEFAULT NULL COMMENT '确认本金',
  `confirmed_total_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '确认总金额',
  `creditor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '债权人名称',
  `declared_interest` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报利息',
  `declared_other_losses` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报其他损失',
  `declared_penalty` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报违约金',
  `declared_principal` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报本金',
  `declared_total_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '申报总金额',
  `evidence_authenticity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证据真实性',
  `evidence_legality` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证据合法性',
  `evidence_relevance` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证据关联性',
  `evidence_review_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '证据审查备注',
  `expired_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '逾期原因',
  `insufficient_evidence_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '证据不足原因',
  `is_conditional` bit(1) NULL DEFAULT NULL COMMENT '是否附条件',
  `is_joint_liability` bit(1) NULL DEFAULT NULL COMMENT '是否连带责任',
  `is_term` bit(1) NULL DEFAULT NULL COMMENT '是否定期',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '备注',
  `review_attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审查附件',
  `review_basis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审查依据',
  `review_conclusion` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审查结论',
  `review_date` datetime(6) NULL DEFAULT NULL COMMENT '审查日期',
  `review_report` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审查报告',
  `review_round` int NULL DEFAULT NULL COMMENT '审查轮次',
  `review_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审查状态',
  `review_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审查摘要',
  `reviewer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审查人',
  `unconfirmed_interest` decimal(18, 2) NULL DEFAULT NULL COMMENT '未确认利息',
  `unconfirmed_other_losses` decimal(18, 2) NULL DEFAULT NULL COMMENT '未确认其他损失',
  `unconfirmed_penalty` decimal(18, 2) NULL DEFAULT NULL COMMENT '未确认违约金',
  `unconfirmed_principal` decimal(18, 2) NULL DEFAULT NULL COMMENT '未确认本金',
  `unconfirmed_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '未确认原因',
  `unconfirmed_total_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '未确认总金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_common_debt
-- ----------------------------
DROP TABLE IF EXISTS `tb_common_debt`;
CREATE TABLE `tb_common_debt`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `apply_date` datetime(6) NULL DEFAULT NULL,
  `approval_date` datetime(6) NULL DEFAULT NULL,
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `basis_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `basis_document` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `debt_amount` decimal(18, 2) NULL DEFAULT NULL,
  `debt_basis` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `debt_date` datetime(6) NULL DEFAULT NULL,
  `debt_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `debt_due_date` datetime(6) NULL DEFAULT NULL,
  `debt_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `debt_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `debt_start_date` datetime(6) NULL DEFAULT NULL,
  `debt_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_overdue` bit(1) NULL DEFAULT NULL,
  `related_business_id` bigint NULL DEFAULT NULL,
  `related_business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `related_flow_id` bigint NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `repaid_amount` decimal(18, 2) NULL DEFAULT NULL,
  `repayment_account_id` bigint NULL DEFAULT NULL,
  `repayment_date` datetime(6) NULL DEFAULT NULL,
  `repayment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `repayment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `repayment_voucher` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `unrepaid_amount` decimal(18, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_42qhgvcwhylsji2ystqk9dj75`(`debt_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_debt_no`(`debt_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_debt_type`(`debt_type` ASC) USING BTREE,
  INDEX `idx_creditor_name`(`creditor_name` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_repayment_status`(`repayment_status` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE,
  INDEX `idx_repayment_date`(`repayment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_conversation
-- ----------------------------
DROP TABLE IF EXISTS `tb_conversation`;
CREATE TABLE `tb_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `last_message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `last_message_id` bigint NULL DEFAULT NULL,
  `last_message_time` datetime(6) NULL DEFAULT NULL,
  `last_message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user1_deleted` bit(1) NULL DEFAULT NULL,
  `user1_pinned` bit(1) NULL DEFAULT NULL,
  `user1_unread_count` int NULL DEFAULT NULL,
  `user2_deleted` bit(1) NULL DEFAULT NULL,
  `user2_pinned` bit(1) NULL DEFAULT NULL,
  `user2_unread_count` int NULL DEFAULT NULL,
  `user_id1` bigint NOT NULL,
  `user_id2` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_conversation`(`user_id1` ASC, `user_id2` ASC) USING BTREE,
  INDEX `idx_user_id1`(`user_id1` ASC) USING BTREE,
  INDEX `idx_user_id2`(`user_id2` ASC) USING BTREE,
  INDEX `idx_last_message_time`(`last_message_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_court
-- ----------------------------
DROP TABLE IF EXISTS `tb_court`;
CREATE TABLE `tb_court`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `contact_phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `court_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `responsible_user_id` bigint NULL DEFAULT NULL,
  `short_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `undertaking_judge` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_70ncptwyhlklegcco3dx71c75`(`short_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_creditor_claim
-- ----------------------------
DROP TABLE IF EXISTS `tb_creditor_claim`;
CREATE TABLE `tb_creditor_claim`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `agent_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `agent_id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `agent_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `agent_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `bank_account` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `claim_facts` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `claim_identifier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `claim_nature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `claim_nature_manager` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `claim_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `credit_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_bank_account` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `debtor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `evidence_attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `evidence_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `evidence_materials` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `has_collateral` bit(1) NULL DEFAULT NULL,
  `has_court_judgment` bit(1) NULL DEFAULT NULL,
  `has_execution` bit(1) NULL DEFAULT NULL,
  `interest` decimal(18, 2) NULL DEFAULT NULL,
  `legal_representative` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `other_losses` decimal(18, 2) NULL DEFAULT NULL,
  `penalty` decimal(18, 2) NULL DEFAULT NULL,
  `principal` decimal(18, 2) NULL DEFAULT NULL,
  `registration_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `service_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `total_amount` decimal(18, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_creditor_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_creditor_info`;
CREATE TABLE `tb_creditor_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地址',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系邮箱',
  `contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '债权人名称',
  `creditor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '债权人类型',
  `id_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `legal_representative` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '法定代表人',
  `registered_capital` decimal(22, 4) NULL DEFAULT NULL COMMENT '注册资本',
  `creditor_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKk1oks2h74ebb8328wannsopeh`(`case_id` ASC) USING BTREE,
  CONSTRAINT `FKk1oks2h74ebb8328wannsopeh` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_debtor_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `tb_debtor_enterprise`;
CREATE TABLE `tb_debtor_enterprise`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `business_scope` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '经营范围',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `contact_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
  `enterprise_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '企业名称',
  `enterprise_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '企业类型',
  `establishment_date` date NULL DEFAULT NULL COMMENT '成立日期',
  `industry` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '行业',
  `legal_representative` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '法定代表人',
  `registered_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '注册地址',
  `registered_capital` decimal(18, 4) NULL DEFAULT NULL COMMENT '注册资本',
  `registration_authority` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登记机关',
  `unified_social_credit_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '统一社会信用代码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_lgh47qnvv506pc6bmeyd5fun4`(`unified_social_credit_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_dictionary_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_dictionary_category`;
CREATE TABLE `tb_dictionary_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_k3wvl8ckxlg8ov3rn9abji1cf`(`category_code` ASC) USING BTREE,
  UNIQUE INDEX `idx_category_code`(`category_code` ASC) USING BTREE,
  INDEX `idx_category_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_dictionary_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_dictionary_item`;
CREATE TABLE `tb_dictionary_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `item_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `item_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_item_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_distribution_detail
-- ----------------------------
DROP TABLE IF EXISTS `tb_distribution_detail`;
CREATE TABLE `tb_distribution_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `accumulated_distribution_amount` decimal(18, 2) NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `claim_amount` decimal(18, 2) NULL DEFAULT NULL,
  `confirmed_amount` decimal(18, 2) NULL DEFAULT NULL,
  `creditor_claim_id` bigint NULL DEFAULT NULL,
  `creditor_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creditor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `current_distribution_amount` decimal(18, 2) NULL DEFAULT NULL,
  `distribution_execution_id` bigint NULL DEFAULT NULL,
  `distribution_ratio` decimal(5, 2) NULL DEFAULT NULL,
  `escrow_id` bigint NULL DEFAULT NULL,
  `escrow_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `is_escrowed` bit(1) NULL DEFAULT NULL,
  `payee_account_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payee_account_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payee_bank_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_account_id` bigint NULL DEFAULT NULL,
  `payment_date` datetime(6) NULL DEFAULT NULL,
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_voucher` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_distribution_execution_id`(`distribution_execution_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_creditor_claim_id`(`creditor_claim_id` ASC) USING BTREE,
  INDEX `idx_creditor_name`(`creditor_name` ASC) USING BTREE,
  INDEX `idx_creditor_type`(`creditor_type` ASC) USING BTREE,
  INDEX `idx_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_payment_date`(`payment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_distribution_execution
-- ----------------------------
DROP TABLE IF EXISTS `tb_distribution_execution`;
CREATE TABLE `tb_distribution_execution`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `accumulated_distribution_amount` decimal(18, 2) NULL DEFAULT NULL,
  `approval_date` datetime(6) NULL DEFAULT NULL,
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `common_debt_amount` decimal(18, 2) NULL DEFAULT NULL,
  `common_debt_count` int NULL DEFAULT NULL,
  `common_debt_ratio` decimal(5, 2) NULL DEFAULT NULL,
  `distribution_batch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `distribution_date` datetime(6) NULL DEFAULT NULL,
  `distribution_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `distribution_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `distribution_plan_id` bigint NULL DEFAULT NULL,
  `distribution_plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `employee_claim_amount` decimal(18, 2) NULL DEFAULT NULL,
  `employee_claim_count` int NULL DEFAULT NULL,
  `employee_claim_ratio` decimal(5, 2) NULL DEFAULT NULL,
  `execution_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_amount` decimal(18, 2) NULL DEFAULT NULL,
  `expense_count` int NULL DEFAULT NULL,
  `expense_ratio` decimal(5, 2) NULL DEFAULT NULL,
  `related_flow_id` bigint NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `secured_claim_amount` decimal(18, 2) NULL DEFAULT NULL,
  `secured_claim_count` int NULL DEFAULT NULL,
  `secured_claim_ratio` decimal(5, 2) NULL DEFAULT NULL,
  `tax_claim_amount` decimal(18, 2) NULL DEFAULT NULL,
  `tax_claim_count` int NULL DEFAULT NULL,
  `tax_claim_ratio` decimal(5, 2) NULL DEFAULT NULL,
  `total_distributable_amount` decimal(18, 2) NULL DEFAULT NULL,
  `total_distribution_amount` decimal(18, 2) NULL DEFAULT NULL,
  `unsecured_claim_amount` decimal(18, 2) NULL DEFAULT NULL,
  `unsecured_claim_count` int NULL DEFAULT NULL,
  `unsecured_claim_ratio` decimal(5, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_mrcqma4cxcxs2mvnmr23281q0`(`distribution_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_distribution_no`(`distribution_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_distribution_batch`(`distribution_batch` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_execution_status`(`execution_status` ASC) USING BTREE,
  INDEX `idx_distribution_date`(`distribution_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_document_delivery
-- ----------------------------
DROP TABLE IF EXISTS `tb_document_delivery`;
CREATE TABLE `tb_document_delivery`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `case_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '案号',
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案件名称',
  `document_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文书名称',
  `document_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文书类型: COURT_NOTICE-法院通知, JUDGMENT-判决书, VERDICT-裁定书, NOTICE-通知书, OTHER-其他',
  `recipient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '受送达人',
  `recipient_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '受送达人类型: DEBTOR-债务人, CREDITOR-债权人, LAWYER-律师, COURT-法院, OTHER-其他',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
  `delivery_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '送达地址',
  `delivery_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '送达方式: POST-邮寄, EMAIL-邮件, EXPRESS-快递, IN_PERSON-当面送达, ELECTRONIC-电子送达, OTHER-其他',
  `send_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发送状态: PENDING-待发送, SENT-已发送,FAILED-发送失败, REJECTED-被拒收，SENDING-发送中',
  `delivery_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '送达内容（富文本）',
  `document_attachment` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文书附件路径',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `failure_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审批，APPROVED-已通过，REJECTED-已驳回\'',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '审批意见',
  `abbreviation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `document_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approval_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_case_number`(`case_number` ASC) USING BTREE,
  INDEX `idx_send_status`(`send_status` ASC) USING BTREE,
  INDEX `idx_document_type`(`document_type` ASC) USING BTREE,
  INDEX `idx_recipient_type`(`recipient_type` ASC) USING BTREE,
  INDEX `idx_delivery_method`(`delivery_method` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_document_delivery_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文书送达表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_document_edit_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_document_edit_history`;
CREATE TABLE `tb_document_edit_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` bigint NOT NULL COMMENT '文件 ID',
  `version` int NOT NULL COMMENT '版本号',
  `editor_id` bigint NOT NULL COMMENT '编辑人 ID',
  `editor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `edit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'MODIFY' COMMENT '编辑类型：CREATE, MODIFY, SAVE',
  `changes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '变更内容（JSON 格式）',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_version`(`version` ASC) USING BTREE,
  INDEX `idx_editor_id`(`editor_id` ASC) USING BTREE,
  INDEX `idx_edit_time`(`edit_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文档编辑历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_document_export_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_document_export_history`;
CREATE TABLE `tb_document_export_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '使用的模板ID',
  `export_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '导出类型: WORD-Word, EXCEL-Excel',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '导出文件名',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `export_params` json NULL COMMENT '导出参数(JSON格式)',
  `export_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'SUCCESS' COMMENT '导出状态: SUCCESS-成功, FAILED-失败',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `exported_by` bigint NOT NULL COMMENT '导出用户ID',
  `exported_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '导出时间',
  `processing_time` int NULL DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_exported_by`(`exported_by` ASC) USING BTREE,
  INDEX `idx_exported_time`(`exported_time` ASC) USING BTREE,
  INDEX `idx_export_status`(`export_status` ASC) USING BTREE,
  INDEX `idx_export_type`(`export_type` ASC) USING BTREE,
  CONSTRAINT `fk_export_history_template` FOREIGN KEY (`template_id`) REFERENCES `tb_document_export_template` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档导出历史记录表，用于记录每次文档导出操作的详细信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_document_export_template
-- ----------------------------
DROP TABLE IF EXISTS `tb_document_export_template`;
CREATE TABLE `tb_document_export_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `template_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板编码（唯一标识）',
  `template_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'WORD' COMMENT '模板类型：WORD, EXCEL',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板文件路径',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板描述',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否为默认模板',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板分类',
  `version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1.0' COMMENT '模板版本',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人 ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人 ID',
  `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_code`(`template_code` ASC) USING BTREE,
  INDEX `idx_template_type`(`template_type` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文档导出模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_document_template_field
-- ----------------------------
DROP TABLE IF EXISTS `tb_document_template_field`;
CREATE TABLE `tb_document_template_field`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `template_id` bigint NOT NULL COMMENT '模板 ID',
  `field_label` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段标签（显示名称）',
  `field_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名称（占位符名称）',
  `field_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'TEXT' COMMENT '字段类型：TEXT, NUMBER, DATE, TABLE, IMAGE',
  `is_required` tinyint(1) NULL DEFAULT 0 COMMENT '是否必填：0-否，1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `default_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '默认值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字段描述',
  `validation_rule` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '验证规则（JSON 格式）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人 ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人 ID',
  `format_pattern` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `source_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_field_name`(`field_name` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文档模板字段配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_escrow_management
-- ----------------------------
DROP TABLE IF EXISTS `tb_escrow_management`;
CREATE TABLE `tb_escrow_management`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `condition_met_date` datetime(6) NULL DEFAULT NULL,
  `creditor_claim_id` bigint NULL DEFAULT NULL,
  `creditor_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `escrow_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `escrow_amount` decimal(18, 2) NULL DEFAULT NULL,
  `escrow_date` datetime(6) NULL DEFAULT NULL,
  `escrow_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `escrow_institution` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `escrow_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `escrow_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `escrow_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `escrow_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_condition_met` bit(1) NULL DEFAULT NULL,
  `related_distribution_id` bigint NULL DEFAULT NULL,
  `related_flow_id` bigint NULL DEFAULT NULL,
  `release_account_id` bigint NULL DEFAULT NULL,
  `release_condition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `release_date` datetime(6) NULL DEFAULT NULL,
  `release_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `release_voucher` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `released_amount` decimal(18, 2) NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `unreleased_amount` decimal(18, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_1j42exonrk6j4h1biywiletth`(`escrow_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_escrow_no`(`escrow_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_escrow_type`(`escrow_type` ASC) USING BTREE,
  INDEX `idx_creditor_name`(`creditor_name` ASC) USING BTREE,
  INDEX `idx_release_status`(`release_status` ASC) USING BTREE,
  INDEX `idx_escrow_date`(`escrow_date` ASC) USING BTREE,
  INDEX `idx_release_date`(`release_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_excel_field_validation_rule
-- ----------------------------
DROP TABLE IF EXISTS `tb_excel_field_validation_rule`;
CREATE TABLE `tb_excel_field_validation_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名',
  `rule_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则类型: REQUIRED-必填, FORMAT-格式, LENGTH-长度, RANGE-范围, PATTERN-正则, CUSTOM-自定义',
  `rule_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规则值(JSON格式)',
  `error_message` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '错误提示信息',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `priority` int NULL DEFAULT 100 COMMENT '优先级(数字越小优先级越高)',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_field_name`(`field_name` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Excel导入字段验证规则表，用于定义和管理Excel字段的数据验证规则，支持必填、格式、长度、范围、正则等多种验证类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_excel_import_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_excel_import_history`;
CREATE TABLE `tb_excel_import_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '导入记录ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '使用的模板ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `sheet_index` int NULL DEFAULT 0 COMMENT 'Sheet索引',
  `total_rows` int NULL DEFAULT 0 COMMENT '总行数',
  `success_rows` int NULL DEFAULT 0 COMMENT '成功行数',
  `fail_rows` int NULL DEFAULT 0 COMMENT '失败行数',
  `import_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '导入状态: PENDING-待处理, PROCESSING-处理中, SUCCESS-成功, FAILED-失败',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `imported_by` bigint NOT NULL COMMENT '导入人ID',
  `imported_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
  `processing_time` int NULL DEFAULT NULL COMMENT '处理耗时(毫秒)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_imported_by`(`imported_by` ASC) USING BTREE,
  INDEX `idx_imported_time`(`imported_time` ASC) USING BTREE,
  INDEX `idx_import_status`(`import_status` ASC) USING BTREE,
  INDEX `idx_history_imported_by`(`imported_by` ASC) USING BTREE,
  CONSTRAINT `fk_excel_import_history_template` FOREIGN KEY (`template_id`) REFERENCES `tb_excel_import_template` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Excel导入历史记录表，用于记录每次Excel导入操作的详细信息，包括使用的模板、文件信息、处理结果等' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_excel_import_template
-- ----------------------------
DROP TABLE IF EXISTS `tb_excel_import_template`;
CREATE TABLE `tb_excel_import_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板描述',
  `field_mappings` json NOT NULL COMMENT '字段映射配置(JSON格式)，存储Excel表头与系统字段的对应关系',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认模板: 0-否, 1-是',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用: 0-禁用, 1-启用',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_code`(`template_code` ASC) USING BTREE,
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `idx_template_name`(`template_name` ASC) USING BTREE,
  INDEX `idx_template_code`(`template_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Excel导入模板管理表，用于存储和管理Excel导入的模板配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_expense_reimbursement
-- ----------------------------
DROP TABLE IF EXISTS `tb_expense_reimbursement`;
CREATE TABLE `tb_expense_reimbursement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reimbursement_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '报销单号',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `case_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '案件名称',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID',
  `applicant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请人姓名',
  `fund_account_id` bigint NOT NULL COMMENT '律师银行账户ID',
  `fund_account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账户名称',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行账号',
  `total_amount` decimal(18, 2) NOT NULL COMMENT '报销总金额（元）',
  `reimbursement_date` date NOT NULL COMMENT '报销日期',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '报销说明',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approval_opinion` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_reimbursement_number`(`reimbursement_number` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_applicant_id`(`applicant_id` ASC) USING BTREE,
  INDEX `idx_fund_account_id`(`fund_account_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_reimbursement_date`(`reimbursement_date` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_expense_reimbursement_applicant` FOREIGN KEY (`applicant_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_expense_reimbursement_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '费用报销主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_expense_reimbursement_attachment
-- ----------------------------
DROP TABLE IF EXISTS `tb_expense_reimbursement_attachment`;
CREATE TABLE `tb_expense_reimbursement_attachment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reimbursement_id` bigint NOT NULL COMMENT '报销单ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件类型',
  `upload_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reimbursement_id`(`reimbursement_id` ASC) USING BTREE,
  CONSTRAINT `fk_expense_reimbursement_attachment_reimbursement` FOREIGN KEY (`reimbursement_id`) REFERENCES `tb_expense_reimbursement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '费用报销附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_expense_reimbursement_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_expense_reimbursement_item`;
CREATE TABLE `tb_expense_reimbursement_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reimbursement_id` bigint NOT NULL COMMENT '报销单ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用名称',
  `item_amount` decimal(18, 2) NOT NULL COMMENT '费用金额（元）',
  `item_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '费用说明',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reimbursement_id`(`reimbursement_id` ASC) USING BTREE,
  CONSTRAINT `fk_expense_reimbursement_item_reimbursement` FOREIGN KEY (`reimbursement_id`) REFERENCES `tb_expense_reimbursement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '费用报销明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_file_lock
-- ----------------------------
DROP TABLE IF EXISTS `tb_file_lock`;
CREATE TABLE `tb_file_lock`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` bigint NOT NULL COMMENT '文件 ID',
  `user_id` bigint NOT NULL COMMENT '锁定用户 ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名称',
  `lock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `lock_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'EDIT' COMMENT '锁定类型：EDIT, PREVIEW',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'LOCKED' COMMENT '状态：LOCKED, UNLOCKED, EXPIRED',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_lock_time`(`lock_time` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件锁定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_file_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_file_record`;
CREATE TABLE `tb_file_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `biz_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `biz_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `delete_time` datetime(6) NULL DEFAULT NULL,
  `delete_user_id` bigint NULL DEFAULT NULL,
  `file_extension` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `file_size` bigint NULL DEFAULT NULL,
  `file_status` int NULL DEFAULT NULL,
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `original_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `stored_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `upload_time` datetime(6) NULL DEFAULT NULL,
  `upload_user_id` bigint NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `sort_order` int NULL DEFAULT NULL,
  `thumbnail_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `video_duration` bigint NULL DEFAULT NULL,
  `video_height` int NULL DEFAULT NULL,
  `video_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `video_width` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_biz_type`(`biz_type` ASC) USING BTREE,
  INDEX `idx_biz_id`(`biz_id` ASC) USING BTREE,
  INDEX `idx_file_status`(`file_status` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_upload_user_id`(`upload_user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_biz_type_biz_id`(`biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_biz_type_biz_ids`(`biz_type` ASC, `biz_id` ASC, `sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 571 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fund_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_fund_account`;
CREATE TABLE `tb_fund_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `account_balance_limit` decimal(18, 2) NULL DEFAULT NULL COMMENT '账户余额限额',
  `account_id` bigint NULL DEFAULT NULL COMMENT '账户ID',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户名称',
  `account_permission` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户权限',
  `account_purpose` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户用途',
  `account_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户类型',
  `bank_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行名称',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '案件名称',
  `closing_date` datetime(6) NULL DEFAULT NULL COMMENT '销户日期',
  `current_balance` decimal(18, 2) NULL DEFAULT NULL COMMENT '当前余额',
  `freeze_date` datetime(6) NULL DEFAULT NULL COMMENT '冻结日期',
  `freeze_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '冻结原因',
  `initial_balance` decimal(18, 2) NULL DEFAULT NULL COMMENT '初始余额',
  `is_frozen` bit(1) NULL DEFAULT NULL COMMENT '是否冻结',
  `opening_date` datetime(6) NULL DEFAULT NULL COMMENT '开户日期',
  `unfreeze_date` datetime(6) NULL DEFAULT NULL COMMENT '解冻日期',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fund_approval
-- ----------------------------
DROP TABLE IF EXISTS `tb_fund_approval`;
CREATE TABLE `tb_fund_approval`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `amount` decimal(18, 2) NULL DEFAULT NULL,
  `approval_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approval_level` int NULL DEFAULT NULL,
  `approval_opinion` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approval_stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approval_time` datetime(6) NULL DEFAULT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `flow_id` bigint NULL DEFAULT NULL,
  `is_timeout` bit(1) NULL DEFAULT NULL,
  `rejection_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `related_business_id` bigint NULL DEFAULT NULL,
  `related_business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `timeout_date` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_flow_id`(`flow_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fund_budget
-- ----------------------------
DROP TABLE IF EXISTS `tb_fund_budget`;
CREATE TABLE `tb_fund_budget`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `announcement_budget` decimal(18, 2) NULL DEFAULT NULL,
  `approval_date` datetime(6) NULL DEFAULT NULL,
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `arbitration_budget` decimal(18, 2) NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `auction_budget` decimal(18, 2) NULL DEFAULT NULL,
  `budget_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `budget_end_date` datetime(6) NULL DEFAULT NULL,
  `budget_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `budget_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `budget_start_date` datetime(6) NULL DEFAULT NULL,
  `budget_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `budget_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `evaluation_budget` decimal(18, 2) NULL DEFAULT NULL,
  `execution_budget` decimal(18, 2) NULL DEFAULT NULL,
  `insurance_budget` decimal(18, 2) NULL DEFAULT NULL,
  `litigation_budget` decimal(18, 2) NULL DEFAULT NULL,
  `manager_fee_budget` decimal(18, 2) NULL DEFAULT NULL,
  `meeting_budget` decimal(18, 2) NULL DEFAULT NULL,
  `other_budget` decimal(18, 2) NULL DEFAULT NULL,
  `related_budget_id` bigint NULL DEFAULT NULL,
  `remaining_amount` decimal(18, 2) NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `storage_budget` decimal(18, 2) NULL DEFAULT NULL,
  `total_budget_amount` decimal(18, 2) NULL DEFAULT NULL,
  `used_amount` decimal(18, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_q4wg4sqwnt77ak1aw3exdlotl`(`budget_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_budget_no`(`budget_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_budget_type`(`budget_type` ASC) USING BTREE,
  INDEX `idx_budget_status`(`budget_status` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_budget_start_date`(`budget_start_date` ASC) USING BTREE,
  INDEX `idx_budget_end_date`(`budget_end_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fund_flow
-- ----------------------------
DROP TABLE IF EXISTS `tb_fund_flow`;
CREATE TABLE `tb_fund_flow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `account_id` bigint NULL DEFAULT NULL COMMENT '账户ID',
  `amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '金额',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '附件',
  `balance_after` decimal(18, 2) NULL DEFAULT NULL COMMENT '交易后余额',
  `balance_before` decimal(18, 2) NULL DEFAULT NULL COMMENT '交易前余额',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '案件名称',
  `check_date` datetime(6) NULL DEFAULT NULL COMMENT '审核日期',
  `check_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核状态',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `expense_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '费用类型',
  `flow_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '流水类别',
  `flow_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '流水类型',
  `fund_account_id` bigint NULL DEFAULT NULL COMMENT '资金账户ID',
  `is_reversed` bit(1) NULL DEFAULT NULL COMMENT '是否冲正',
  `operation_time` datetime(6) NULL DEFAULT NULL COMMENT '操作时间',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `related_business_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `related_business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '关联业务类型',
  `related_document` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '关联单据',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `reversal_date` datetime(6) NULL DEFAULT NULL COMMENT '冲正日期',
  `reversal_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '冲正原因',
  `reversed_flow_id` bigint NULL DEFAULT NULL COMMENT '被冲正流水ID',
  `transaction_date` datetime(6) NULL DEFAULT NULL COMMENT '交易日期',
  `voucher_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '凭证号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_flow_type`(`flow_type` ASC) USING BTREE,
  INDEX `idx_transaction_date`(`transaction_date` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_fund_account_id`(`fund_account_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fund_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_fund_operation_log`;
CREATE TABLE `tb_fund_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `audit_date` datetime(6) NULL DEFAULT NULL,
  `audit_user_id` bigint NULL DEFAULT NULL,
  `browser_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `data_after` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `data_before` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_audited` bit(1) NULL DEFAULT NULL,
  `operation_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operation_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operation_time` datetime(6) NULL DEFAULT NULL,
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operator_id` bigint NULL DEFAULT NULL,
  `related_business_id` bigint NULL DEFAULT NULL,
  `related_business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fund_reimbursement
-- ----------------------------
DROP TABLE IF EXISTS `tb_fund_reimbursement`;
CREATE TABLE `tb_fund_reimbursement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `applicant_id` bigint NULL DEFAULT NULL,
  `applicant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `applied_amount` decimal(18, 2) NULL DEFAULT NULL,
  `apply_date` datetime(6) NULL DEFAULT NULL,
  `approval_date` datetime(6) NULL DEFAULT NULL,
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `approved_amount` decimal(18, 2) NULL DEFAULT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `budget_id` bigint NULL DEFAULT NULL,
  `budget_item` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `case_id` bigint NULL DEFAULT NULL,
  `case_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_date` datetime(6) NULL DEFAULT NULL,
  `expense_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expense_purpose` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `payee_account_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payee_account_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payee_bank_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_account_id` bigint NULL DEFAULT NULL,
  `payment_date` datetime(6) NULL DEFAULT NULL,
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `payment_voucher` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `reimbursed_amount` decimal(18, 2) NULL DEFAULT NULL,
  `reimbursement_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `reimbursement_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `reimbursement_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `reimbursement_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `related_expense_id` bigint NULL DEFAULT NULL,
  `related_flow_id` bigint NULL DEFAULT NULL,
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_h4nr54q6sshou0oddmaunglmu`(`reimbursement_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_reimbursement_no`(`reimbursement_no` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_reimbursement_type`(`reimbursement_type` ASC) USING BTREE,
  INDEX `idx_applicant_id`(`applicant_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_apply_date`(`apply_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document`;
CREATE TABLE `tb_lib_document`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文档名称',
  `document_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文档编码(唯一标识)',
  `folder_id` bigint NULL DEFAULT NULL COMMENT '所属文件夹ID, NULL表示根目录',
  `document_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文档类型: WORD-Word文档, EXCEL-Excel表格, PDF-PDF文档, OTHER-其他',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件存储路径',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小(字节)',
  `file_extension` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件扩展名(如: docx, xlsx, pdf)',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'MIME类型',
  `current_version` int NULL DEFAULT 1 COMMENT '当前版本号',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文档描述',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签(逗号分隔)',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开: 0-否, 1-是',
  `is_locked` tinyint(1) NULL DEFAULT 0 COMMENT '是否锁定: 0-否, 1-是(锁定时不可编辑)',
  `locked_by` bigint NULL DEFAULT NULL COMMENT '锁定者ID',
  `locked_time` datetime NULL DEFAULT NULL COMMENT '锁定时间',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除, DRAFT-草稿',
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_document_code`(`document_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_doc_code`(`document_code` ASC) USING BTREE,
  INDEX `idx_folder_id`(`folder_id` ASC) USING BTREE,
  INDEX `idx_document_type`(`document_type` ASC) USING BTREE,
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_folder_status`(`folder_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_lib_doc_folder_id`(`folder_id` ASC) USING BTREE,
  INDEX `idx_lib_doc_type`(`document_type` ASC) USING BTREE,
  INDEX `idx_lib_doc_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_doc_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_lib_doc_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_lib_document_folder` FOREIGN KEY (`folder_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_favorite
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_favorite`;
CREATE TABLE `tb_lib_document_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收藏夹名称',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  `create_user_id` bigint NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_document_user`(`document_id` ASC, `user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_fav_doc_user`(`document_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_document_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_fav_doc_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_lib_fav_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_lib_favorite_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_folder
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_folder`;
CREATE TABLE `tb_lib_document_folder`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件夹名称',
  `folder_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件夹完整路径(如: /合同/2024年)',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父文件夹ID, NULL表示根目录',
  `folder_level` int NULL DEFAULT 1 COMMENT '文件夹层级, 根目录为1',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件夹描述',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件夹图标',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文件夹颜色',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_folder_path`(`folder_path` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_folder_path`(`folder_path` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_parent_status`(`parent_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_lib_folder_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_lib_folder_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_folder_create_user_id`(`create_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_lib_folder_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档文件夹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_operation_log`;
CREATE TABLE `tb_lib_document_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NULL DEFAULT NULL COMMENT '文档ID',
  `document_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '文档名称(冗余存储)',
  `folder_id` bigint NULL DEFAULT NULL COMMENT '文件夹ID',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '操作类型: CREATE-创建, UPDATE-更新, DELETE-删除, DOWNLOAD-下载, VIEW-查看, MOVE-移动, COPY-复制, RENAME-重命名, LOCK-锁定, UNLOCK-解锁, RESTORE-恢复版本',
  `operation_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '操作详情(JSON格式)',
  `old_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '变更前值',
  `new_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '变更后值',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户代理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '操作者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_document_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_folder_id`(`folder_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_log_doc_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_lib_log_folder_id`(`folder_id` ASC) USING BTREE,
  INDEX `idx_lib_log_op_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_lib_log_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_lib_log_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_lib_log_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_lib_log_folder` FOREIGN KEY (`folder_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 172 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_permission`;
CREATE TABLE `tb_lib_document_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限名称',
  `permission_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限编码',
  `permission_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限类型: READ-读取, WRITE-编辑, DELETE-删除, ADMIN-管理, DOWNLOAD-下载',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '权限描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_perm_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_permission_type`(`permission_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_perm_type`(`permission_type` ASC) USING BTREE,
  INDEX `idx_lib_perm_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_permission_rel
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_permission_rel`;
CREATE TABLE `tb_lib_document_permission_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '目标类型: USER-用户, ROLE-角色, DEPARTMENT-部门',
  `target_id` bigint NOT NULL COMMENT '目标ID(用户ID/角色ID/部门ID)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_document_permission_target`(`document_id` ASC, `permission_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_dpr_target`(`document_id` ASC, `permission_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_dpr_unique`(`document_id` ASC, `permission_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_document_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_dpr_doc_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_lib_dpr_perm_id`(`permission_id` ASC) USING BTREE,
  INDEX `idx_lib_dpr_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  CONSTRAINT `fk_lib_doc_perm_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_lib_doc_perm_permission` FOREIGN KEY (`permission_id`) REFERENCES `tb_lib_document_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_share
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_share`;
CREATE TABLE `tb_lib_document_share`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `share_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '分享码(唯一)',
  `share_password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '分享密码(可选)',
  `permission_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'READ' COMMENT '分享权限: READ-仅查看, DOWNLOAD-可下载, EDIT-可编辑',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间(NULL表示永久有效)',
  `max_access_count` int NULL DEFAULT 0 COMMENT '最大访问次数(0表示不限制)',
  `access_count` int NULL DEFAULT 0 COMMENT '已访问次数',
  `is_enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_share_code`(`share_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_share_code`(`share_code` ASC) USING BTREE,
  INDEX `idx_document_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_share_doc_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_lib_share_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_lib_share_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_lib_share_code`(`share_code` ASC) USING BTREE,
  CONSTRAINT `fk_lib_share_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档分享表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_share_access
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_share_access`;
CREATE TABLE `tb_lib_document_share_access`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `share_id` bigint NOT NULL COMMENT '分享ID',
  `visitor_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '访问者IP',
  `visitor_user_id` bigint NULL DEFAULT NULL COMMENT '访问者用户ID(如已登录)',
  `access_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'VIEW' COMMENT '访问类型: VIEW-查看, DOWNLOAD-下载',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户代理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  `create_user_id` bigint NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `visitor_user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_share_id`(`share_id` ASC) USING BTREE,
  INDEX `idx_visitor_user_id`(`visitor_user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_access_share_id`(`share_id` ASC) USING BTREE,
  INDEX `idx_lib_access_visitor_user_id`(`visitor_user_id` ASC) USING BTREE,
  INDEX `idx_lib_access_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_lib_access_share` FOREIGN KEY (`share_id`) REFERENCES `tb_lib_document_share` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档分享访问记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_document_version
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_document_version`;
CREATE TABLE `tb_lib_document_version`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version_number` int NOT NULL COMMENT '版本号',
  `version_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '版本名称(如: v1.0, v1.1)',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '该版本的原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '该版本的文件存储路径',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小(字节)',
  `change_summary` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '变更说明',
  `change_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'UPDATE' COMMENT '变更类型: CREATE-创建, UPDATE-更新, RESTORE-恢复',
  `is_major` tinyint(1) NULL DEFAULT 0 COMMENT '是否主版本: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_document_version`(`document_id` ASC, `version_number` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_doc_version`(`document_id` ASC, `version_number` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_version_doc_num`(`document_id` ASC, `version_number` ASC) USING BTREE,
  INDEX `idx_document_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_version_doc_id`(`document_id` ASC) USING BTREE,
  INDEX `idx_lib_version_create_user_id`(`create_user_id` ASC) USING BTREE,
  INDEX `idx_lib_version_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_lib_version_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文档版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_lib_folder_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_lib_folder_permission`;
CREATE TABLE `tb_lib_folder_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `folder_id` bigint NOT NULL COMMENT '文件夹ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '目标类型: USER-用户, ROLE-角色, DEPARTMENT-部门',
  `target_id` bigint NOT NULL COMMENT '目标ID(用户ID/角色ID/部门ID)',
  `is_inherit` tinyint(1) NULL DEFAULT 1 COMMENT '是否继承到子文件夹: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_folder_permission_target`(`folder_id` ASC, `permission_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_fp_target`(`folder_id` ASC, `permission_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_lib_fp_unique`(`folder_id` ASC, `permission_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_folder_id`(`folder_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_lib_fp_folder_id`(`folder_id` ASC) USING BTREE,
  INDEX `idx_lib_fp_permission_id`(`permission_id` ASC) USING BTREE,
  INDEX `idx_lib_fp_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  CONSTRAINT `fk_lib_folder_perm_folder` FOREIGN KEY (`folder_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_lib_folder_perm_permission` FOREIGN KEY (`permission_id`) REFERENCES `tb_lib_document_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文件夹权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_login_fail
-- ----------------------------
DROP TABLE IF EXISTS `tb_login_fail`;
CREATE TABLE `tb_login_fail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `fail_count` int NULL DEFAULT NULL,
  `fail_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `fail_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `last_fail_time` datetime(6) NULL DEFAULT NULL,
  `login_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `unlock_time` datetime(6) NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_login_account`(`login_account` ASC) USING BTREE,
  INDEX `idx_fail_ip`(`fail_ip` ASC) USING BTREE,
  INDEX `idx_last_fail_time`(`last_fail_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_login_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_login_record`;
CREATE TABLE `tb_login_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `is_known_device` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_device` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `login_time` datetime(6) NULL DEFAULT NULL,
  `login_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `user_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_account`(`user_account` ASC) USING BTREE,
  INDEX `idx_login_time`(`login_time` ASC) USING BTREE,
  INDEX `idx_login_status`(`login_status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 595 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_meeting_video_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_meeting_video_tag`;
CREATE TABLE `tb_meeting_video_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meeting_id` bigint NOT NULL COMMENT '会议ID，关联会议表',
  `video_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频标题',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'pending' COMMENT '生成状态：generated=已生成，pending=待生成',
  `file_id` bigint NULL DEFAULT NULL COMMENT '关联的文件记录ID，对应tb_file_record表主键',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_meeting_id`(`meeting_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '债权人会议视频标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_meeting_vote_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_meeting_vote_item`;
CREATE TABLE `tb_meeting_vote_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meeting_id` bigint NOT NULL COMMENT '会议ID，关联会议表',
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '投票项名称',
  `agree_count` int NULL DEFAULT 0 COMMENT '同意票数',
  `oppose_count` int NULL DEFAULT 0 COMMENT '反对票数',
  `abstain_count` int NULL DEFAULT 0 COMMENT '弃权票数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_meeting_id`(`meeting_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '债权人会议投票项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_message_recall_config
-- ----------------------------
DROP TABLE IF EXISTS `tb_message_recall_config`;
CREATE TABLE `tb_message_recall_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `allow_recall` bit(1) NULL DEFAULT NULL,
  `config_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `max_recall_times` int NULL DEFAULT NULL,
  `recall_time_limit` int NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `target_id` bigint NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_type`(`config_type` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_notification
-- ----------------------------
DROP TABLE IF EXISTS `tb_notification`;
CREATE TABLE `tb_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expire_time` datetime(6) NULL DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `read_time` datetime(6) NULL DEFAULT NULL,
  `related_id` bigint NULL DEFAULT NULL,
  `related_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `user_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2099 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_onlyoffice_edit_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_onlyoffice_edit_record`;
CREATE TABLE `tb_onlyoffice_edit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` bigint NOT NULL COMMENT '文件 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名称',
  `edit_start_time` datetime NULL DEFAULT NULL COMMENT '编辑开始时间',
  `edit_end_time` datetime NULL DEFAULT NULL COMMENT '编辑结束时间',
  `version` int NULL DEFAULT 1 COMMENT '编辑版本',
  `changes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '变更内容（JSON 格式）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'EDITING' COMMENT '状态：EDITING, SAVED, CLOSED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_edit_start_time`(`edit_start_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OnlyOffice 文件编辑记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_permission`;
CREATE TABLE `tb_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `perm_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限代码',
  `perm_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限名称',
  `perm_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限类型',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组件',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_external` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '是否外部链接',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_perm_code`(`perm_code` ASC) USING BTREE,
  UNIQUE INDEX `UK_naguwsntxvhyp4i5h0wtaq3e6`(`perm_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_perm_type`(`perm_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_role`;
CREATE TABLE `tb_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色代码',
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `role_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色描述',
  `is_system` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '是否系统角色',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE,
  UNIQUE INDEX `UK_n0ggalialw26sy249dujnxbax`(`role_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_role_permission`;
CREATE TABLE `tb_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `perm_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_perm_id`(`perm_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sms_code
-- ----------------------------
DROP TABLE IF EXISTS `tb_sms_code`;
CREATE TABLE `tb_sms_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `expire_time` datetime(6) NOT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `sms_type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `used_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `used_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mobile`(`mobile` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_used_status`(`used_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_system_config
-- ----------------------------
DROP TABLE IF EXISTS `tb_system_config`;
CREATE TABLE `tb_system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `config_group` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_l645w2xbbgxq9c2as2ixv3223`(`config_key` ASC) USING BTREE,
  INDEX `idx_config_group`(`config_group` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_system_field
-- ----------------------------
DROP TABLE IF EXISTS `tb_system_field`;
CREATE TABLE `tb_system_field`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '分组名称',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字段标签',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字段值',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序顺序',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '字段描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_name`(`group_name` ASC) USING BTREE,
  INDEX `idx_value`(`value` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统字段表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_temp_upload_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_temp_upload_token`;
CREATE TABLE `tb_temp_upload_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expire_time` datetime(6) NOT NULL,
  `file_count` int NOT NULL,
  `token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `user_id` bigint NOT NULL,
  `creator_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_mv8kxm10dibcoj9t75o1xw4u9`(`token` ASC) USING BTREE,
  INDEX `idx_token`(`token` ASC) USING BTREE,
  INDEX `idx_biz_type`(`biz_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 141 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_todo
-- ----------------------------
DROP TABLE IF EXISTS `tb_todo`;
CREATE TABLE `tb_todo`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint NULL DEFAULT NULL,
  `assignee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `completed_time` datetime(6) NULL DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `create_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `deadline` datetime(6) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `related_id` bigint NULL DEFAULT NULL,
  `related_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `user_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deadline`(`deadline` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_token`;
CREATE TABLE `tb_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `device_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `device_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expire_time` datetime(6) NOT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `revoke_time` datetime(6) NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `token_type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `token_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'JWT Token值',
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1197 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户姓名',
  `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '是否有效',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '用户状态: ACTIVE-正常, INACTIVE-禁用, LOCKED-锁定, DELETED-删除',
  `login_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '登录类型',
  `bind_device` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '绑定设备',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最后登录IP',
  `login_count` int NULL DEFAULT 0 COMMENT '登录次数',
  `pwd_error_count` int NULL DEFAULT 0 COMMENT '密码错误次数',
  `pwd_error_time` datetime NULL DEFAULT NULL COMMENT '密码错误时间',
  `pwd_expire_time` datetime NULL DEFAULT NULL COMMENT '密码过期时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `UK_4wv83hfajry5tdoamn8wsqa6x`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_mobile`(`mobile` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `UK_4vih17mube9j7cqyjlfbcrk4m`(`email` ASC) USING BTREE,
  UNIQUE INDEX `UK_c4dxpoujf358m9ekdstivfqti`(`mobile` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user_agreement_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_agreement_record`;
CREATE TABLE `tb_user_agreement_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `agree_time` datetime(6) NULL DEFAULT NULL,
  `agreed` bit(1) NOT NULL,
  `agreement_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `agreement_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `agreement_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `user_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uagr_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_uagr_agreement_type`(`agreement_type` ASC) USING BTREE,
  INDEX `idx_uagr_agree_time`(`agree_time` ASC) USING BTREE,
  INDEX `idx_uagr_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user_recall_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_recall_record`;
CREATE TABLE `tb_user_recall_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `recall_count` int NULL DEFAULT NULL,
  `recall_date` date NOT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `recall_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_recall_date`(`recall_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_role`;
CREATE TABLE `tb_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `role_id` bigint NOT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_work_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_work_log`;
CREATE TABLE `tb_work_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `work_date` date NOT NULL COMMENT '工作日期',
  `work_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '工作类型: CASE_INVESTIGATION-案件调查, CREDITOR_CONTACT-债权人联系, ASSET_DISPOSAL-资产处置, COURT_COMMUNICATION-法院沟通, DOCUMENT_PREPARATION-文书准备, MEETING_ORGANIZATION-会议组织, OTHER-其他',
  `work_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '工作内容',
  `work_result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '工作结果',
  `attachment_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '附件ID列表(逗号分隔)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE COMMENT '案件ID索引',
  INDEX `idx_work_date`(`work_date` ASC) USING BTREE COMMENT '工作日期索引',
  INDEX `idx_work_type`(`work_type` ASC) USING BTREE COMMENT '工作类型索引',
  INDEX `idx_create_user_id`(`create_user_id` ASC) USING BTREE COMMENT '创建者ID索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_work_log_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '工作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_work_plan
-- ----------------------------
DROP TABLE IF EXISTS `tb_work_plan`;
CREATE TABLE `tb_work_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `is_deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `execution_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '执行状态',
  `plan_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '计划内容',
  `plan_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '计划编号',
  `plan_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '计划类型',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT '负责人ID',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_m3yxmpw4fvava0p89bqqrdbhv`(`plan_number` ASC) USING BTREE,
  INDEX `idx_plan_number`(`plan_number` ASC) USING BTREE,
  INDEX `idx_plan_type`(`plan_type` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_responsible_user_id`(`responsible_user_id` ASC) USING BTREE,
  INDEX `idx_execution_status`(`execution_status` ASC) USING BTREE,
  INDEX `idx_start_date`(`start_date` ASC) USING BTREE,
  INDEX `idx_end_date`(`end_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_work_team
-- ----------------------------
DROP TABLE IF EXISTS `tb_work_team`;
CREATE TABLE `tb_work_team`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '团队名称',
  `team_leader_id` bigint NULL DEFAULT NULL COMMENT '团队负责人ID',
  `case_id` bigint NULL DEFAULT NULL COMMENT '案件ID',
  `team_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '团队描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_team_leader_id`(`team_leader_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_work_team_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_work_team_leader` FOREIGN KEY (`team_leader_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '工作团队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_work_team_member
-- ----------------------------
DROP TABLE IF EXISTS `tb_work_team_member`;
CREATE TABLE `tb_work_team_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `team_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '团队角色（系统权限优先于团队权限，如果用户系统角色为LAWYER，被分配到团队后也可访问该案件）',
  `permission_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'VIEW' COMMENT '权限级别: VIEW-查看, EDIT-编辑, ADMIN-管理',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否激活',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_team_id`(`team_id` ASC) USING BTREE,
  INDEX `idx_case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_team_role`(`team_role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_work_team_member_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_team_member_team` FOREIGN KEY (`team_id`) REFERENCES `tb_work_team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_team_member_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 143 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '工作团队成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_work_team_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_work_team_permission`;
CREATE TABLE `tb_work_team_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `create_user_id` bigint NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `update_user_id` bigint NULL DEFAULT NULL,
  `is_allowed` int NULL DEFAULT NULL,
  `module_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `permission_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `team_member_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_team_member_id`(`team_member_id` ASC) USING BTREE,
  INDEX `idx_module_type`(`module_type` ASC) USING BTREE,
  INDEX `idx_permission_type`(`permission_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5681 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for v_creditor_claim
-- ----------------------------
DROP VIEW IF EXISTS `v_creditor_claim`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_creditor_claim` AS select `cr`.`id` AS `id`,`cr`.`claim_no` AS `claim_no`,`cr`.`case_id` AS `case_id`,`cr`.`case_name` AS `case_name`,`cr`.`debtor` AS `debtor`,`cr`.`creditor_name` AS `creditor_name`,`cr`.`creditor_type` AS `creditor_type`,`cr`.`credit_code` AS `credit_code`,`cr`.`legal_representative` AS `legal_representative`,`cr`.`service_address` AS `service_address`,`cr`.`agent_name` AS `agent_name`,`cr`.`agent_phone` AS `agent_phone`,`cr`.`agent_id_card` AS `agent_id_card`,`cr`.`agent_address` AS `agent_address`,`cr`.`account_name` AS `account_name`,`cr`.`creditor_bank_account` AS `creditor_bank_account`,`cr`.`bank_name` AS `bank_name`,`cr`.`principal` AS `principal`,`cr`.`interest` AS `interest`,`cr`.`penalty` AS `penalty`,`cr`.`other_losses` AS `other_losses`,`cr`.`total_amount` AS `total_amount`,`cr`.`has_court_judgment` AS `has_court_judgment`,`cr`.`has_execution` AS `has_execution`,`cr`.`has_collateral` AS `has_collateral`,`cr`.`claim_nature` AS `claim_nature`,`cr`.`claim_type` AS `claim_type`,`cr`.`claim_facts` AS `claim_facts`,`cr`.`claim_identifier` AS `claim_identifier`,`cr`.`evidence_list` AS `evidence_list`,`cr`.`evidence_materials` AS `evidence_materials`,`cr`.`evidence_attachments` AS `evidence_attachments`,`cr`.`registration_status` AS `registration_status`,`cr`.`remarks` AS `remarks`,`cr`.`status` AS `status`,`cr`.`is_deleted` AS `is_deleted`,`cr`.`create_time` AS `create_time`,`cr`.`update_time` AS `update_time`,`cr`.`create_user_id` AS `create_user_id`,`cr`.`update_user_id` AS `update_user_id`,`rv`.`review_conclusion` AS `review_conclusion`,`rv`.`confirmed_total_amount` AS `review_confirmed_amount`,`rv`.`review_date` AS `review_date`,`cf`.`confirmation_status` AS `confirmation_status`,`cf`.`final_confirmed_amount` AS `final_confirmed_amount`,`cf`.`final_confirmation_date` AS `final_confirmation_date` from ((`tb_claim_registration` `cr` left join `tb_claim_review` `rv` on(((`cr`.`id` = `rv`.`claim_registration_id`) and (`rv`.`status` = 'ACTIVE')))) left join `tb_claim_confirmation` `cf` on(((`cr`.`id` = `cf`.`claim_registration_id`) and (`cf`.`status` = 'ACTIVE')))) where (`cr`.`is_deleted` = 0);

-- ----------------------------
-- View structure for v_template_fields
-- ----------------------------
DROP VIEW IF EXISTS `v_template_fields`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_template_fields` AS select `t`.`id` AS `template_id`,`t`.`template_name` AS `template_name`,`t`.`template_code` AS `template_code`,`t`.`template_type` AS `template_type`,`t`.`category` AS `category`,`f`.`id` AS `field_id`,`f`.`field_label` AS `field_label`,`f`.`field_name` AS `field_name`,`f`.`field_type` AS `field_type`,`f`.`sort_order` AS `sort_order`,`f`.`is_required` AS `is_required`,`f`.`default_value` AS `default_value` from (`tb_document_export_template` `t` left join `tb_document_template_field` `f` on(((`t`.`id` = `f`.`template_id`) and (`f`.`is_deleted` = 0)))) where ((`t`.`is_deleted` = 0) and (`t`.`status` = 'ACTIVE'));

-- ----------------------------
-- Procedure structure for generate_audit_log_hash_chain
-- ----------------------------
DROP PROCEDURE IF EXISTS `generate_audit_log_hash_chain`;
delimiter ;;
CREATE PROCEDURE `generate_audit_log_hash_chain`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id BIGINT;
    DECLARE v_prev_id BIGINT;
    DECLARE v_user_id BIGINT;
    DECLARE v_user_account VARCHAR(100);
    DECLARE v_user_name VARCHAR(100);
    DECLARE v_module VARCHAR(100);
    DECLARE v_module_name VARCHAR(200);
    DECLARE v_operation_type VARCHAR(50);
    DECLARE v_operation_name VARCHAR(200);
    DECLARE v_business_type VARCHAR(100);
    DECLARE v_business_id BIGINT;
    DECLARE v_business_name VARCHAR(500);
    DECLARE v_request_method VARCHAR(10);
    DECLARE v_request_url VARCHAR(500);
    DECLARE v_request_params TEXT;
    DECLARE v_data_before LONGTEXT;
    DECLARE v_data_after LONGTEXT;
    DECLARE v_status VARCHAR(20);
    DECLARE v_error_message TEXT;
    DECLARE v_ip_address VARCHAR(50);
    DECLARE v_create_time DATETIME;
    DECLARE v_prev_hash VARCHAR(128);
    DECLARE v_current_hash VARCHAR(128);
    DECLARE v_digital_signature VARCHAR(256);
    DECLARE v_chain_sequence BIGINT;
    DECLARE v_data_to_hash TEXT;
    DECLARE v_processed_count INT DEFAULT 0;
    DECLARE v_skip_count INT DEFAULT 0;

    DECLARE cur CURSOR FOR
        SELECT id, user_id, user_account, user_name, module, module_name, operation_type, operation_name,
               business_type, business_id, business_name, request_method, request_url, request_params,
               data_before, data_after, status, error_message, ip_address, create_time
        FROM tb_audit_log
        WHERE hash_value IS NULL OR hash_value = ''
        ORDER BY id ASC;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 初始化创世哈希
    SET v_prev_hash = '0000000000000000000000000000000000000000000000000000000000000000';

    -- 统计需要处理的记录数
    SELECT COUNT(*) INTO v_processed_count FROM tb_audit_log WHERE hash_value IS NULL OR hash_value = '';

    SELECT CONCAT('需要处理的审计日志数量: ', v_processed_count) AS status;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_id, v_user_id, v_user_account, v_user_name, v_module, v_module_name,
                       v_operation_type, v_operation_name, v_business_type, v_business_id,
                       v_business_name, v_request_method, v_request_url, v_request_params,
                       v_data_before, v_data_after, v_status, v_error_message, v_ip_address, v_create_time;

        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 构建哈希数据（与 Java HashChainUtil.buildHashData 完全一致）
        -- 字段顺序: id|userId|userAccount|userName|module|moduleName|operationType|operationName|
        --          businessType|businessId|businessName|requestMethod|requestUrl|requestParams|
        --          dataBefore|dataAfter|status|errorMessage|ipAddress|createTime|previousHash
        SET v_data_to_hash = CONCAT(
            COALESCE(CAST(v_id AS CHAR), ''), '|',
            COALESCE(CAST(v_user_id AS CHAR), ''), '|',
            COALESCE(v_user_account, ''), '|',
            COALESCE(v_user_name, ''), '|',
            COALESCE(v_module, ''), '|',
            COALESCE(v_module_name, ''), '|',
            COALESCE(v_operation_type, ''), '|',
            COALESCE(v_operation_name, ''), '|',
            COALESCE(v_business_type, ''), '|',
            COALESCE(CAST(v_business_id AS CHAR), ''), '|',
            COALESCE(v_business_name, ''), '|',
            COALESCE(v_request_method, ''), '|',
            COALESCE(v_request_url, ''), '|',
            COALESCE(v_request_params, ''), '|',
            COALESCE(v_data_before, ''), '|',
            COALESCE(v_data_after, ''), '|',
            COALESCE(v_status, ''), '|',
            COALESCE(v_error_message, ''), '|',
            COALESCE(v_ip_address, ''), '|',
            COALESCE(DATE_FORMAT(v_create_time, '%Y-%m-%d %H:%i:%s'), ''), '|',
            COALESCE(v_prev_hash, '')
        );

        -- 生成 SHA-256 哈希值
        SET v_current_hash = SHA2(v_data_to_hash, 256);

        -- 生成数字签名（与 Java applyRSAWithPrivateKey 逻辑一致）
        SET v_digital_signature = SHA2(CONCAT(
            COALESCE(CAST(v_id AS CHAR), ''), '|',
            COALESCE(v_current_hash, ''), '|',
            COALESCE(CAST(v_user_id AS CHAR), ''), '|',
            COALESCE(v_operation_type, ''), '|',
            COALESCE(v_business_type, ''), '|',
            COALESCE(CAST(v_business_id AS CHAR), ''), '|',
            COALESCE(DATE_FORMAT(v_create_time, '%Y-%m-%d %H:%i:%s'), ''), '|',
            'lawbackend2-private-key'
        ), 256);

        -- 获取链序列号
        SELECT IFNULL(MAX(chain_sequence), 0) + 1 INTO v_chain_sequence
        FROM tb_audit_log
        WHERE chain_sequence IS NOT NULL;

        -- 更新记录
        UPDATE tb_audit_log
        SET hash_value = v_current_hash,
            previous_hash = v_prev_hash,
            digital_signature = v_digital_signature,
            chain_sequence = v_chain_sequence,
            integrity_status = 'VERIFIED'
        WHERE id = v_id;

        -- 更新前一条哈希值
        SET v_prev_hash = v_current_hash;

        -- 每100条输出一次进度
        SET v_skip_count = v_skip_count + 1;
        IF v_skip_count MOD 100 = 0 THEN
            SELECT CONCAT('已处理: ', v_skip_count, ' / ', v_processed_count) AS progress;
        END IF;

    END LOOP;

    CLOSE cur;

    SELECT CONCAT('迁移完成！共处理 ', v_skip_count, ' 条审计日志') AS result;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for insert_tasks_for_all_cases
-- ----------------------------
DROP PROCEDURE IF EXISTS `insert_tasks_for_all_cases`;
delimiter ;;
CREATE PROCEDURE `insert_tasks_for_all_cases`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_case_id BIGINT;
    DECLARE cur CURSOR FOR SELECT id FROM tb_bankrupt_case WHERE is_deleted = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO v_case_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        
        INSERT INTO tb_case_task (case_id, task_code, task_name, task_description, status, sort_order, create_time, update_time, is_deleted, status_audit) VALUES
        (v_case_id, 'TASK_001', '鎻愪氦鐮翠骇鐢宠?鏉愭枡', '鐢宠?浜?, 'IN_PROGRESS', 1, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_002', '瑁佸畾鍙楃悊骞跺叕鍛?, '娉曢櫌', 'IN_PROGRESS', 2, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_003', '鍏ㄩ潰鎺ョ?鍊哄姟浜?, '绠＄悊浜?, 'IN_PROGRESS', 3, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_004', '绠＄悊浜哄嵃绔?, '绠＄悊浜?, 'IN_PROGRESS', 4, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_005', '璋冩煡璐?骇鍙婄粡钀ョ姸鍐?, '绠＄悊浜?, 'IN_PROGRESS', 5, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_006', '杩芥敹鍊哄姟浜鸿储浜?, '绠＄悊浜?, 'IN_PROGRESS', 6, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_007', '鍐冲畾鍚堝悓缁х画灞ヨ?鎴栬В闄?, '绠＄悊浜?, 'IN_PROGRESS', 7, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_008', '閫氱煡宸茬煡鍊烘潈浜哄苟鍏?憡', '绠＄悊浜?, 'IN_PROGRESS', 8, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_009', '鎺ユ敹銆佺櫥璁板?鏉冪敵鎶?, '绠＄悊浜?, 'IN_PROGRESS', 9, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_010', '瀹℃煡鐢虫姤鍊烘潈骞剁紪鍒跺?鏉冭〃', '绠＄悊浜?, 'IN_PROGRESS', 10, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_011', '鍊烘潈瀹℃煡缁撴灉閫氱煡', '绠＄悊浜?, 'IN_PROGRESS', 11, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_012', '浼氳?璧勬枡', '绠＄悊浜?, 'IN_PROGRESS', 12, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_013', '琛ㄥ喅浜嬮」鍜岃〃鍐崇粨鏋?, '鍊烘潈浜轰細璁?, 'IN_PROGRESS', 13, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_014', '瀹ｅ憡閲嶆暣涓庡拰瑙?, '娉曢櫌', 'IN_PROGRESS', 14, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_015', '瀹℃煡瀹ｅ憡鐮翠骇鏉′欢', '娉曢櫌', 'IN_PROGRESS', 15, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_016', '瑁佸畾瀹ｅ憡鍊哄姟浜虹牬浜у強鍏?憡', '娉曢櫌', 'IN_PROGRESS', 16, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_017', '鐮翠骇璐?骇鍙樹环鏂规?', '绠＄悊浜?, 'IN_PROGRESS', 17, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_018', '鐮翠骇璐?骇鍒嗛厤鏂规?', '绠＄悊浜?, 'IN_PROGRESS', 18, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_019', '鐮翠骇璐圭敤涓庡叡鐩婂?鍔?, '绠＄悊浜?, 'IN_PROGRESS', 19, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_020', '鎻愯?缁堢粨鐮翠骇绋嬪簭', '绠＄悊浜?, 'IN_PROGRESS', 20, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_021', '娉曢櫌瑁佸畾骞跺叕鍛?, '娉曢櫌', 'IN_PROGRESS', 21, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_022', '鍔炵悊浼佷笟娉ㄩ攢鐧昏?', '绠＄悊浜?, 'IN_PROGRESS', 22, NOW(), NOW(), 0, 'ACTIVE'),
        (v_case_id, 'TASK_023', '绠＄悊浜虹粓姝㈡墽琛岃亴鍔″苟褰掓。', '绠＄悊浜?, 'IN_PROGRESS', 23, NOW(), NOW(), 0, 'ACTIVE');
        
    END LOOP;
    
    CLOSE cur;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for sp_cleanup_expired_locks
-- ----------------------------
DROP PROCEDURE IF EXISTS `sp_cleanup_expired_locks`;
delimiter ;;
CREATE PROCEDURE `sp_cleanup_expired_locks`()
BEGIN
  UPDATE tb_file_lock
  SET status = 'EXPIRED'
  WHERE status = 'LOCKED'
  AND (expire_time IS NULL OR expire_time < NOW());
  
  SELECT ROW_COUNT() AS affected_rows;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for verify_audit_log_integrity
-- ----------------------------
DROP PROCEDURE IF EXISTS `verify_audit_log_integrity`;
delimiter ;;
CREATE PROCEDURE `verify_audit_log_integrity`(OUT p_total_count BIGINT,
    OUT p_tampered_count BIGINT,
    OUT p_integrity_rate DECIMAL(5,2))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id BIGINT;
    DECLARE v_hash_value VARCHAR(128);
    DECLARE v_previous_hash VARCHAR(128);
    DECLARE v_integrity_status VARCHAR(20);
    DECLARE v_count INT DEFAULT 0;
    DECLARE cur CURSOR FOR
        SELECT id, hash_value, previous_hash, integrity_status
        FROM tb_audit_log
        ORDER BY id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SET p_tampered_count = 0;
    SET v_count = 0;

    SELECT COUNT(*) INTO p_total_count FROM tb_audit_log;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_id, v_hash_value, v_previous_hash, v_integrity_status;
        IF done THEN
            LEAVE read_loop;
        END IF;

        SET v_count = v_count + 1;

        -- 验证完整性
        IF v_integrity_status = 'TAMPERED' THEN
            SET p_tampered_count = p_tampered_count + 1;
        END IF;
    END LOOP;

    CLOSE cur;

    IF p_total_count > 0 THEN
        SET p_integrity_rate = ((p_total_count - p_tampered_count) / p_total_count) * 100;
    ELSE
        SET p_integrity_rate = 100.00;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table tb_audit_log
-- ----------------------------
DROP TRIGGER IF EXISTS `prevent_audit_log_update`;
delimiter ;;
CREATE TRIGGER `prevent_audit_log_update` BEFORE UPDATE ON `tb_audit_log` FOR EACH ROW BEGIN
    IF OLD.id <=> NEW.id
       AND OLD.user_id <=> NEW.user_id AND OLD.user_account <=> NEW.user_account AND OLD.user_name <=> NEW.user_name
       AND OLD.module <=> NEW.module AND OLD.module_name <=> NEW.module_name
       AND OLD.operation_type <=> NEW.operation_type AND OLD.operation_name <=> NEW.operation_name
       AND OLD.business_type <=> NEW.business_type AND OLD.business_id <=> NEW.business_id AND OLD.business_name <=> NEW.business_name
       AND OLD.request_method <=> NEW.request_method AND OLD.request_url <=> NEW.request_url AND OLD.request_params <=> NEW.request_params
       AND OLD.data_before <=> NEW.data_before AND OLD.data_after <=> NEW.data_after
       AND OLD.status <=> NEW.status AND OLD.error_message <=> NEW.error_message
       AND OLD.ip_address <=> NEW.ip_address AND OLD.location <=> NEW.location
       AND OLD.browser <=> NEW.browser AND OLD.os <=> NEW.os AND OLD.duration <=> NEW.duration
       AND OLD.create_time <=> NEW.create_time
    THEN
        IF (OLD.hash_value IS NULL OR OLD.hash_value = '') AND (NEW.hash_value IS NOT NULL AND NEW.hash_value != '') THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSEIF OLD.hash_value <=> NEW.hash_value THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改哈希值';
        END IF;

        IF (OLD.previous_hash IS NULL OR OLD.previous_hash = '') AND (NEW.previous_hash IS NOT NULL AND NEW.previous_hash != '') THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSEIF OLD.previous_hash <=> NEW.previous_hash THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改前驱哈希';
        END IF;

        IF (OLD.digital_signature IS NULL OR OLD.digital_signature = '') AND (NEW.digital_signature IS NOT NULL AND NEW.digital_signature != '') THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSEIF OLD.digital_signature <=> NEW.digital_signature THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改数字签名';
        END IF;

        IF (OLD.chain_sequence IS NULL) AND (NEW.chain_sequence IS NOT NULL) THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSEIF OLD.chain_sequence <=> NEW.chain_sequence THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改链序列号';
        END IF;

        SET NEW.integrity_status = NEW.integrity_status;
        SET NEW.signed_by = NEW.signed_by;
        SET NEW.signed_time = NEW.signed_time;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改审计日志';
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table tb_audit_log
-- ----------------------------
DROP TRIGGER IF EXISTS `prevent_audit_log_delete`;
delimiter ;;
CREATE TRIGGER `prevent_audit_log_delete` BEFORE DELETE ON `tb_audit_log` FOR EACH ROW BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止删除审计日志';
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table tb_document_export_template
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_template_delete`;
delimiter ;;
CREATE TRIGGER `trg_template_delete` BEFORE DELETE ON `tb_document_export_template` FOR EACH ROW BEGIN
  UPDATE tb_document_template_field
  SET is_deleted = 1
  WHERE template_id = OLD.id;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
