-- 聊天消息表
DROP TABLE IF EXISTS `tb_ai_chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_ai_chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `content` text COLLATE utf8mb4_bin NOT NULL COMMENT '消息内容',
  `sender_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '发送者类型: user-用户, ai-人工智能',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
  `timestamp` datetime NOT NULL COMMENT '消息时间戳',
  `message_status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'SENT' COMMENT '消息状态: SENT-已发送, DELIVERED-已送达, READ-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_sender_type` (`sender_type`),
  CONSTRAINT `fk_message_session` FOREIGN KEY (`session_id`) REFERENCES `tb_ai_chat_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='AI聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;