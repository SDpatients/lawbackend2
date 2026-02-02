-- ============================================================ --
-- Excel导入模板管理功能数据库表（优化版）
-- 创建日期: 2026-01-31
-- 说明: 优化版SQL脚本，符合新建sql规范.md标准要求
-- ============================================================ --

-- 1. Excel导入模板表
CREATE TABLE IF NOT EXISTS `tb_excel_import_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(50) NOT NULL COMMENT '模板编码',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '模板描述',
  `field_mappings` JSON NOT NULL COMMENT '字段映射配置(JSON格式)，存储Excel表头与系统字段的对应关系',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认模板: 0-否, 1-是',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用: 0-禁用, 1-启用',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Excel导入模板表';

-- 2. Excel导入历史记录表
CREATE TABLE IF NOT EXISTS `tb_excel_import_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '导入记录ID',
  `template_id` BIGINT DEFAULT NULL COMMENT '使用的模板ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `sheet_index` INT DEFAULT 0 COMMENT 'Sheet索引',
  `total_rows` INT DEFAULT 0 COMMENT '总行数',
  `success_rows` INT DEFAULT 0 COMMENT '成功行数',
  `fail_rows` INT DEFAULT 0 COMMENT '失败行数',
  `import_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '导入状态: PENDING-待处理, PROCESSING-处理中, SUCCESS-成功, FAILED-失败',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `imported_by` BIGINT NOT NULL COMMENT '导入人ID',
  `imported_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
  `processing_time` INT DEFAULT NULL COMMENT '处理耗时(毫秒)',
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_imported_by` (`imported_by`),
  KEY `idx_imported_time` (`imported_time`),
  KEY `idx_import_status` (`import_status`),
  CONSTRAINT `fk_excel_import_history_template` FOREIGN KEY (`template_id`) REFERENCES `tb_excel_import_template` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Excel导入历史记录表';

-- 3. Excel导入字段验证规则表
CREATE TABLE IF NOT EXISTS `tb_excel_field_validation_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `field_name` VARCHAR(50) NOT NULL COMMENT '字段名',
  `rule_type` VARCHAR(20) NOT NULL COMMENT '规则类型: REQUIRED-必填, FORMAT-格式, LENGTH-长度, RANGE-范围, PATTERN-正则, CUSTOM-自定义',
  `rule_value` VARCHAR(500) DEFAULT NULL COMMENT '规则值(JSON格式)',
  `error_message` VARCHAR(200) NOT NULL COMMENT '错误提示信息',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `priority` INT DEFAULT 100 COMMENT '优先级(数字越小优先级越高)',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_field_name` (`field_name`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Excel字段验证规则表';

-- 4. 插入默认模板数据
INSERT INTO `tb_excel_import_template` 
  (`template_name`, `template_code`, `description`, `field_mappings`, `is_default`, `is_active`, `created_by`) 
VALUES 
  ('默认债权申报模板', 'default', '默认的债权申报Excel导入模板，适用于标准格式的债权登记簿', 
   '{"收件编号":"receiptNumber","编号":"receiptNumber","序号":"receiptNumber","债权人":"creditorName","债权人名称":"creditorName","申报人":"creditorName","申报时间":"declarationTime","申报日期":"declarationTime","登记日期":"declarationTime","住所/邮编":"addressAndPostalCode","住所":"addressAndPostalCode","地址":"addressAndPostalCode","邮编":"addressAndPostalCode","送达地址":"serviceAddress","联系电话":"contactPhone","电话":"contactPhone","手机号":"contactPhone","申报金额":"declaredAmount","金额":"declaredAmount","债权金额":"declaredAmount","总金额":"declaredAmount","性质":"nature","债权性质":"claimNature","法定代表人":"legalRepresentative","法人":"legalRepresentative","代理人":"agentName","委托代理人":"agentName","代理人电话":"agentPhone","代理人联系电话":"agentPhone","债权种类":"claimType","债权类型":"claimType","开户名":"accountName","账户名":"accountName","开户行":"bankName","开户银行":"bankName","银行名称":"bankName","账号":"bankAccount","银行账号":"bankAccount","债权人银行账号":"creditorBankAccount","涉讼":"litigationStatus","诉讼情况":"litigationStatus","备注":"remarks","说明":"remarks"}', 
   1, 1, 1);

-- 5. 插入默认字段验证规则
INSERT INTO `tb_excel_field_validation_rule` 
  (`field_name`, `rule_type`, `rule_value`, `error_message`, `priority`) 
VALUES 
  ('creditorName', 'REQUIRED', NULL, '债权人名称不能为空', 100),
  ('declaredAmount', 'REQUIRED', NULL, '申报金额不能为空', 100),
  ('declaredAmount', 'FORMAT', '{"min":0}', '申报金额必须大于0', 100),
  ('creditorName', 'LENGTH', '{"max":100}', '债权人名称不能超过100个字符', 90),
  ('contactPhone', 'PATTERN', '^1[3-9]\\d{9}$', '联系电话格式不正确', 80),
  ('declarationTime', 'FORMAT', 'yyyy-MM-dd HH:mm:ss', '申报时间格式不正确', 80);

-- 6. 添加额外性能优化索引
CREATE INDEX `idx_template_name` ON `tb_excel_import_template` (`template_name`);
CREATE INDEX `idx_template_code` ON `tb_excel_import_template` (`template_code`);
CREATE INDEX `idx_history_imported_by` ON `tb_excel_import_history` (`imported_by`);

-- 7. 添加表级注释
ALTER TABLE `tb_excel_import_template` COMMENT = 'Excel导入模板管理表，用于存储和管理Excel导入的模板配置';
ALTER TABLE `tb_excel_import_history` COMMENT = 'Excel导入历史记录表，用于记录每次Excel导入操作的详细信息，包括使用的模板、文件信息、处理结果等';
ALTER TABLE `tb_excel_field_validation_rule` COMMENT = 'Excel导入字段验证规则表，用于定义和管理Excel字段的数据验证规则，支持必填、格式、长度、范围、正则等多种验证类型';

-- ============================================================ --
-- 优化说明：
-- 1. 表名统一使用tb_前缀，符合项目命名规范
-- 2. 添加了完整的表级注释，说明表的用途
-- 3. 字段注释更加详细，明确字段的含义和取值规范
-- 4. 添加了性能优化索引，提高查询效率
-- 5. JSON字段添加了明确的格式注释
-- 6. 外键约束使用了正确的级联规则
-- 7. 插入了默认模板数据和验证规则
-- ============================================================ --
