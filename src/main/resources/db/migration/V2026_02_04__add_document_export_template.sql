-- 文档导出历史记录表（先删除，因为有外键引用其他表）
-- 用于记录每次文档导出操作的详细信息
DROP TABLE IF EXISTS `tb_document_export_history`;

-- 文档模板字段映射表（先删除，因为有外键引用模板表）
-- 用于定义模板中的字段与系统数据字段的映射关系
DROP TABLE IF EXISTS `tb_document_template_field`;

-- 文档导出模板管理表
-- 用于存储和管理Word/Excel文档导出模板配置
DROP TABLE IF EXISTS `tb_document_export_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_document_export_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '模板编码',
  `template_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '模板类型: WORD-Word文档, EXCEL-Excel表格',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板描述',
  `file_path` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模板文件存储路径',
  `config_json` json DEFAULT NULL COMMENT '模板配置(JSON格式)，存储前端拖拉拽的配置信息',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认模板: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档导出模板管理表，用于存储和管理Word/Excel文档导出模板配置';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 文档模板字段映射表
-- 用于定义模板中的字段与系统数据字段的映射关系
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_document_template_field` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `field_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '字段名(占位符名称)',
  `field_label` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段标签(显示名称)',
  `field_type ` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '字段类型: TEXT-文本, NUMBER-数字, DATE-日期, LIST-列表, IMAGE-图片, TABLE-表格',
  `source_field` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '数据源字段(对应系统数据字段路径)',
  `default_value` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '默认值',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `is_required` tinyint(1) DEFAULT '0' COMMENT '是否必填: 0-否, 1-是',
  `format_pattern` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '格式模式(如日期格式yyyy-MM-dd)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_field` (`template_id`, `field_name`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_field_type` (`field_type`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_template_field_template` FOREIGN KEY (`template_id`) REFERENCES `tb_document_export_template` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档模板字段映射表，用于定义模板中的字段与系统数据字段的映射关系';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 文档导出历史记录表
-- 用于记录每次文档导出操作的详细信息
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_document_export_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '使用的模板ID',
  `export_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '导出类型: WORD-Word, EXCEL-Excel',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '导出文件名',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `export_params` json DEFAULT NULL COMMENT '导出参数(JSON格式)',
  `export_status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'SUCCESS' COMMENT '导出状态: SUCCESS-成功, FAILED-失败',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `exported_by` bigint NOT NULL COMMENT '导出用户ID',
  `exported_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '导出时间',
  `processing_time` int DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_exported_by` (`exported_by`),
  KEY `idx_exported_time` (`exported_time`),
  KEY `idx_export_status` (`export_status`),
  KEY `idx_export_type` (`export_type`),
  CONSTRAINT `fk_export_history_template` FOREIGN KEY (`template_id`) REFERENCES `tb_document_export_template` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档导出历史记录表，用于记录每次文档导出操作的详细信息';
/*!40101 SET character_set_client = @saved_cs_client */;
