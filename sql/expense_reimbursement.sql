-- 费用报销相关表
-- 创建日期: 2026-01-24
-- 说明: 用于律师费用报销功能，包括报销单、报销明细、报销附件

-- 费用报销主表
DROP TABLE IF EXISTS `tb_expense_reimbursement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_expense_reimbursement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reimbursement_number` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '报销单号',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `case_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案件名称',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID',
  `applicant_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请人姓名',
  `fund_account_id` bigint NOT NULL COMMENT '律师银行账户ID',
  `fund_account_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户名称',
  `bank_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '银行账号',
  `total_amount` decimal(18,2) NOT NULL COMMENT '报销总金额（元）',
  `reimbursement_date` date NOT NULL COMMENT '报销日期',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报销说明',
  `approval_status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_opinion` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reimbursement_number` (`reimbursement_number`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_applicant_id` (`applicant_id`),
  KEY `idx_fund_account_id` (`fund_account_id`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_reimbursement_date` (`reimbursement_date`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_expense_reimbursement_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_expense_reimbursement_applicant` FOREIGN KEY (`applicant_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_expense_reimbursement_fund_account` FOREIGN KEY (`fund_account_id`) REFERENCES `tb_fund_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='费用报销主表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 费用报销明细表
DROP TABLE IF EXISTS `tb_expense_reimbursement_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_expense_reimbursement_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reimbursement_id` bigint NOT NULL COMMENT '报销单ID',
  `item_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用名称',
  `item_amount` decimal(18,2) NOT NULL COMMENT '费用金额（元）',
  `item_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '费用说明',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_reimbursement_id` (`reimbursement_id`),
  CONSTRAINT `fk_expense_reimbursement_item_reimbursement` FOREIGN KEY (`reimbursement_id`) REFERENCES `tb_expense_reimbursement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='费用报销明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 费用报销附件表
DROP TABLE IF EXISTS `tb_expense_reimbursement_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_expense_reimbursement_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reimbursement_id` bigint NOT NULL COMMENT '报销单ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件类型',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_reimbursement_id` (`reimbursement_id`),
  CONSTRAINT `fk_expense_reimbursement_attachment_reimbursement` FOREIGN KEY (`reimbursement_id`) REFERENCES `tb_expense_reimbursement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='费用报销附件表';
/*!40101 SET character_set_client = @saved_cs_client */;
