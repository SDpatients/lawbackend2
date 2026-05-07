-- ========================================================
-- 破产案件法定节点管理模块数据库脚本
-- 版本: 1.0
-- 日期: 2026-04-27
-- 描述: 包含节点模板表、案件节点实例表、节点延期申请表
-- ========================================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- --------------------------------------------------------
-- 表结构: tb_case_node_template (案件节点模板表)
-- 描述: 定义各类破产案件的法定节点标准模板
-- --------------------------------------------------------
DROP TABLE IF EXISTS `tb_case_node_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_node_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_code` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '节点编码, 如: CLAIM_FILING_PERIOD',
  `node_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点名称, 如: 债权申报期限',
  `node_description` text COLLATE utf8mb4_unicode_ci COMMENT '节点描述, 说明该节点的业务内容和要求',
  `case_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '适用案件类型: LIQUIDATION-清算, REORGANIZATION-重整, COMPROMISE-和解, COMMON-通用',
  `case_stage` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '案件阶段: ACCEPTANCE-受理阶段, LIQUIDATION-清算阶段, REORGANIZATION-重整阶段, COMPROMISE-和解阶段, TERMINATION-终结阶段',
  `legal_deadline_days` int NOT NULL COMMENT '法定期限天数, 如: 30, 90, 180等',
  `legal_deadline_days_max` int DEFAULT NULL COMMENT '法定期限最大天数(区间时使用), 如债权申报30-90天',
  `calculation_base` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '起算规则: ACCEPTANCE_DATE-受理日, ANNOUNCEMENT_DATE-公告日, PREV_NODE_COMPLETE-前置节点完成日, MEETING_PASS_DATE-会议通过日, COMPLETION_DATE-完成日, REORGANIZATION_DATE-裁定重整日',
  `prev_node_code` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '前置节点编码, 依赖前置节点完成才能启动',
  `responsible_role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '责任角色: ADMINISTRATOR-管理人, COURT-法院, CREDITOR_MEETING-债权人会议, APPLICANT-申请人',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序序号, 同一阶段内按此排序',
  `is_mandatory` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否必经节点: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_node_code_case_type` (`node_code`, `case_type`),
  KEY `idx_case_type` (`case_type`),
  KEY `idx_case_stage` (`case_stage`),
  KEY `idx_calculation_base` (`calculation_base`),
  KEY `idx_prev_node_code` (`prev_node_code`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='案件节点模板表: 定义各类破产案件的法定节点标准模板';
/*!40101 SET character_set_client = @saved_cs_client */;

-- --------------------------------------------------------
-- 表结构: tb_case_node_instance (案件节点实例表)
-- 描述: 每个破产案件根据模板生成的实际节点实例
-- --------------------------------------------------------
DROP TABLE IF EXISTS `tb_case_node_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_node_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID, 关联tb_bankrupt_case',
  `template_id` bigint NOT NULL COMMENT '节点模板ID, 关联tb_case_node_template',
  `node_code` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '节点编码, 冗余存储便于查询',
  `node_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点名称, 冗余存储便于查询',
  `node_status` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'PENDING' COMMENT '节点状态: PENDING-待启动, IN_PROGRESS-进行中, COMPLETED-已完成, EXTENSION_REQUESTED-延期申请中, EXTENDED-已延期, SKIPPED-已跳过',
  `start_date` date DEFAULT NULL COMMENT '实际开始日期',
  `deadline_date` date NOT NULL COMMENT '截止日期(法定期限计算得出)',
  `completed_date` date DEFAULT NULL COMMENT '实际完成日期',
  `extension_count` int NOT NULL DEFAULT '0' COMMENT '延期次数',
  `extension_days` int NOT NULL DEFAULT '0' COMMENT '累计延期天数',
  `responsible_person_id` bigint DEFAULT NULL COMMENT '责任人ID, 关联tb_user',
  `responsible_person_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '责任人姓名',
  `completion_remark` text COLLATE utf8mb4_unicode_ci COMMENT '完成备注',
  `alert_level` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '预警级别: NORMAL-正常(>3天), SOON_DUE-即将到期(1-3天), DUE_TODAY-今日到期(0天), OVERDUE-已逾期(<0天)',
  `alert_triggered_at` datetime DEFAULT NULL COMMENT '预警触发时间',
  `prev_node_instance_id` bigint DEFAULT NULL COMMENT '前置节点实例ID, 用于节点依赖关系',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_node_code` (`node_code`),
  KEY `idx_node_status` (`node_status`),
  KEY `idx_deadline_date` (`deadline_date`),
  KEY `idx_alert_level` (`alert_level`),
  KEY `idx_responsible_person_id` (`responsible_person_id`),
  KEY `idx_prev_node_instance_id` (`prev_node_instance_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_case_id_node_status` (`case_id`, `node_status`),
  KEY `idx_case_id_alert_level` (`case_id`, `alert_level`),
  CONSTRAINT `fk_case_node_instance_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_instance_template` FOREIGN KEY (`template_id`) REFERENCES `tb_case_node_template` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_instance_prev` FOREIGN KEY (`prev_node_instance_id`) REFERENCES `tb_case_node_instance` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='案件节点实例表: 每个破产案件根据模板生成的实际节点实例';
/*!40101 SET character_set_client = @saved_cs_client */;

-- --------------------------------------------------------
-- 表结构: tb_case_node_extension (节点延期申请表)
-- 描述: 记录案件节点的延期申请及审批结果
-- --------------------------------------------------------
DROP TABLE IF EXISTS `tb_case_node_extension`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_node_extension` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_instance_id` bigint NOT NULL COMMENT '节点实例ID, 关联tb_case_node_instance',
  `case_id` bigint NOT NULL COMMENT '案件ID, 关联tb_bankrupt_case',
  `extension_days` int NOT NULL COMMENT '申请延期天数',
  `original_deadline` date NOT NULL COMMENT '原截止日期',
  `new_deadline` date NOT NULL COMMENT '延期后新截止日期',
  `apply_reason` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请理由',
  `attachment_path` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '附件路径',
  `approval_status` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已通过, REJECTED-已驳回',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID, 关联tb_user',
  `approver_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_opinion` text COLLATE utf8mb4_unicode_ci COMMENT '审批意见',
  `apply_user_id` bigint NOT NULL COMMENT '申请人ID, 关联tb_user',
  `apply_user_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人姓名',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`),
  KEY `idx_node_instance_id` (`node_instance_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_approver_id` (`approver_id`),
  KEY `idx_apply_user_id` (`apply_user_id`),
  KEY `idx_apply_time` (`apply_time`),
  KEY `idx_case_id_approval_status` (`case_id`, `approval_status`),
  CONSTRAINT `fk_case_node_extension_node` FOREIGN KEY (`node_instance_id`) REFERENCES `tb_case_node_instance` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_extension_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='节点延期申请表: 记录案件节点的延期申请及审批结果';
/*!40101 SET character_set_client = @saved_cs_client */;

-- --------------------------------------------------------
-- 表结构: tb_case_node_alert_record (节点预警记录表)
-- 描述: 记录节点预警的历史触发记录, 用于审计和通知追踪
-- --------------------------------------------------------
DROP TABLE IF EXISTS `tb_case_node_alert_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_node_alert_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_instance_id` bigint NOT NULL COMMENT '节点实例ID, 关联tb_case_node_instance',
  `case_id` bigint NOT NULL COMMENT '案件ID, 关联tb_bankrupt_case',
  `alert_level` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '预警级别: SOON_DUE-即将到期, DUE_TODAY-今日到期, OVERDUE-已逾期',
  `remaining_days` int NOT NULL COMMENT '剩余天数(计算得出, 逾期时为负数)',
  `deadline_date` date NOT NULL COMMENT '截止日期',
  `alert_date` date NOT NULL COMMENT '预警触发日期',
  `is_notified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已发送通知: 0-否, 1-是',
  `notification_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '通知类型: SYSTEM_MSG-站内消息, SMS-短信, EMAIL-邮件, MULTI-多渠道',
  `notification_time` datetime DEFAULT NULL COMMENT '通知发送时间',
  `notification_content` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `recipient_id` bigint DEFAULT NULL COMMENT '通知接收人ID, 关联tb_user',
  `recipient_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知接收人姓名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`),
  KEY `idx_node_instance_id` (`node_instance_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_alert_level` (`alert_level`),
  KEY `idx_alert_date` (`alert_date`),
  KEY `idx_is_notified` (`is_notified`),
  KEY `idx_recipient_id` (`recipient_id`),
  KEY `idx_case_id_alert_date` (`case_id`, `alert_date`),
  CONSTRAINT `fk_case_node_alert_node` FOREIGN KEY (`node_instance_id`) REFERENCES `tb_case_node_instance` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_node_alert_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='节点预警记录表: 记录节点预警的历史触发记录, 用于审计和通知追踪';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
