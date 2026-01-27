-- 审批管理表
DROP TABLE IF EXISTS `tb_approval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_approval` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `lawyer_id` bigint NOT NULL COMMENT '律师ID',
  `approval_type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '审核类型: CASE_SUBMIT-案件提交, CASE_CLOSE-案件结案, FEE_APPLY-费用申请, EVIDENCE_UPLOAD-证据上传',
  `approval_status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'PENDING' COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消',
  `approval_content` text COLLATE utf8mb4_bin COMMENT '审核内容',
  `approval_result` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '审核结果: PASS-通过, FAIL-未通过',
  `approval_count` int DEFAULT '0' COMMENT '审核次数',
  `approver_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `approval_date` datetime DEFAULT NULL COMMENT '审核日期',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_case_type` (`case_id`, `approval_type`),
  KEY `idx_lawyer_id` (`lawyer_id`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_approver_id` (`approver_id`),
  KEY `idx_approval_date` (`approval_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='审批管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 审批历史表
DROP TABLE IF EXISTS `tb_approval_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_approval_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `approval_id` bigint NOT NULL COMMENT '审批管理表ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `approver_id` bigint NOT NULL COMMENT '审核人ID',
  `approval_type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '审核类型: CASE_SUBMIT-案件提交, CASE_CLOSE-案件结案, FEE_APPLY-费用申请, EVIDENCE_UPLOAD-证据上传',
  `approval_status` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消',
  `approval_opinion` text COLLATE utf8mb4_bin COMMENT '审核意见',
  `approval_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '审核日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
  PRIMARY KEY (`id`),
  KEY `idx_approval_id` (`approval_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_approver_id` (`approver_id`),
  KEY `idx_approval_date` (`approval_date`),
  CONSTRAINT `fk_approval_history_approval` FOREIGN KEY (`approval_id`) REFERENCES `tb_approval` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='审批历史表';
/*!40101 SET character_set_client = @saved_cs_client */;