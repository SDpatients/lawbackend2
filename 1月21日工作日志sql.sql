-- 工作日志表：记录案件处理过程中的重要工作内容
DROP TABLE IF EXISTS `tb_work_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_work_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `work_date` date NOT NULL COMMENT '工作日期',
  `work_type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '工作类型: CASE_INVESTIGATION-案件调查, CREDITOR_CONTACT-债权人联系, ASSET_DISPOSAL-资产处置, COURT_COMMUNICATION-法院沟通, DOCUMENT_PREPARATION-文书准备, MEETING_ORGANIZATION-会议组织, OTHER-其他',
  `work_content` text COLLATE utf8mb4_bin NOT NULL COMMENT '工作内容',
  `work_result` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '工作结果',
  `attachment_ids` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '附件ID列表(逗号分隔)',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`) COMMENT '案件ID索引',
  KEY `idx_work_date` (`work_date`) COMMENT '工作日期索引',
  KEY `idx_work_type` (`work_type`) COMMENT '工作类型索引',
  KEY `idx_create_user_id` (`create_user_id`) COMMENT '创建者ID索引',
  KEY `idx_status` (`status`) COMMENT '状态索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  CONSTRAINT `fk_work_log_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;
