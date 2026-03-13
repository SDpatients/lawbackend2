-- =====================================================
-- OnlyOffice 集成与 Word 模板管理数据库脚本
-- 数据库：MySQL 8.0+
-- 创建日期：2026-03-07
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 文档导出模板表
-- =====================================================
DROP TABLE IF EXISTS `tb_document_export_template`;
CREATE TABLE `tb_document_export_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `template_name` VARCHAR(200) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(100) DEFAULT NULL COMMENT '模板编码（唯一标识）',
  `template_type` VARCHAR(20) NOT NULL DEFAULT 'WORD' COMMENT '模板类型：WORD, EXCEL',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '模板文件路径',
  `description` VARCHAR(1000) DEFAULT NULL COMMENT '模板描述',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否为默认模板',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '模板分类',
  `version` VARCHAR(20) DEFAULT '1.0' COMMENT '模板版本',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID',
  `update_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档导出模板表';

-- =====================================================
-- 2. 文档模板字段配置表
-- =====================================================
DROP TABLE IF EXISTS `tb_document_template_field`;
CREATE TABLE `tb_document_template_field` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `template_id` BIGINT NOT NULL COMMENT '模板 ID',
  `field_label` VARCHAR(200) NOT NULL COMMENT '字段标签（显示名称）',
  `field_name` VARCHAR(100) NOT NULL COMMENT '字段名称（占位符名称）',
  `field_type` VARCHAR(20) DEFAULT 'TEXT' COMMENT '字段类型：TEXT, NUMBER, DATE, TABLE, IMAGE',
  `is_required` TINYINT(1) DEFAULT 0 COMMENT '是否必填：0-否，1-是',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `default_value` VARCHAR(500) DEFAULT NULL COMMENT '默认值',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '字段描述',
  `validation_rule` TEXT DEFAULT NULL COMMENT '验证规则（JSON 格式）',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` BIGINT DEFAULT NULL COMMENT '创建人 ID',
  `update_user_id` BIGINT DEFAULT NULL COMMENT '更新人 ID',
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_field_name` (`field_name`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档模板字段配置表';

-- =====================================================
-- 3. OnlyOffice 文件编辑记录表
-- =====================================================
DROP TABLE IF EXISTS `tb_onlyoffice_edit_record`;
CREATE TABLE `tb_onlyoffice_edit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` BIGINT NOT NULL COMMENT '文件 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名称',
  `edit_start_time` DATETIME DEFAULT NULL COMMENT '编辑开始时间',
  `edit_end_time` DATETIME DEFAULT NULL COMMENT '编辑结束时间',
  `version` INT DEFAULT 1 COMMENT '编辑版本',
  `changes` TEXT DEFAULT NULL COMMENT '变更内容（JSON 格式）',
  `status` VARCHAR(20) DEFAULT 'EDITING' COMMENT '状态：EDITING, SAVED, CLOSED',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_edit_start_time` (`edit_start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OnlyOffice 文件编辑记录表';

-- =====================================================
-- 4. 文件锁定表
-- =====================================================
DROP TABLE IF EXISTS `tb_file_lock`;
CREATE TABLE `tb_file_lock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` BIGINT NOT NULL COMMENT '文件 ID',
  `user_id` BIGINT NOT NULL COMMENT '锁定用户 ID',
  `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名称',
  `lock_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `lock_type` VARCHAR(20) DEFAULT 'EDIT' COMMENT '锁定类型：EDIT, PREVIEW',
  `status` VARCHAR(20) DEFAULT 'LOCKED' COMMENT '状态：LOCKED, UNLOCKED, EXPIRED',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_id` (`file_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_lock_time` (`lock_time`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件锁定表';

-- =====================================================
-- 5. 文档编辑历史表
-- =====================================================
DROP TABLE IF EXISTS `tb_document_edit_history`;
CREATE TABLE `tb_document_edit_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `file_id` BIGINT NOT NULL COMMENT '文件 ID',
  `version` INT NOT NULL COMMENT '版本号',
  `editor_id` BIGINT NOT NULL COMMENT '编辑人 ID',
  `editor_name` VARCHAR(100) DEFAULT NULL COMMENT '编辑人名称',
  `edit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `edit_type` VARCHAR(20) DEFAULT 'MODIFY' COMMENT '编辑类型：CREATE, MODIFY, SAVE',
  `changes` TEXT DEFAULT NULL COMMENT '变更内容（JSON 格式）',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_version` (`version`),
  KEY `idx_editor_id` (`editor_id`),
  KEY `idx_edit_time` (`edit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档编辑历史表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 初始化模板数据
INSERT INTO `tb_document_export_template` (`template_name`, `template_code`, `template_type`, `description`, `category`, `is_default`, `status`) VALUES
('数据报告模板', 'DATA_REPORT', 'WORD', '标准的数据分析报告模板', 'REPORT', 1, 'ACTIVE'),
('财务报告模板', 'FINANCIAL_REPORT', 'WORD', '财务报表分析模板', 'REPORT', 0, 'ACTIVE'),
('法律案件报告模板', 'LEGAL_REPORT', 'WORD', '法律案件分析报告模板', 'LEGAL', 0, 'ACTIVE'),
('项目进度报告模板', 'PROJECT_REPORT', 'WORD', '项目进度管理报告模板', 'PROJECT', 0, 'ACTIVE'),
('会议纪要模板', 'MEETING_MINUTES', 'WORD', '会议记录模板', 'MEETING', 0, 'ACTIVE');

-- 初始化模板字段数据 - 数据报告模板
INSERT INTO `tb_document_template_field` (`template_id`, `field_label`, `field_name`, `field_type`, `sort_order`, `is_required`) VALUES
(1, '报告标题', 'reportTitle', 'TEXT', 1, 1),
(1, '报告编号', 'reportNo', 'TEXT', 2, 1),
(1, '报告日期', 'reportDate', 'DATE', 3, 1),
(1, '编制单位', 'company', 'TEXT', 4, 1),
(1, '编制人', 'creator', 'TEXT', 5, 1),
(1, '审核人', 'reviewer', 'TEXT', 6, 1),
(1, '报告类型', 'reportType', 'TEXT', 7, 1),
(1, '报告摘要', 'summary', 'TEXT', 8, 0),
(1, '结论', 'conclusion', 'TEXT', 9, 0);

-- 初始化模板字段数据 - 财务报告模板
INSERT INTO `tb_document_template_field` (`template_id`, `field_label`, `field_name`, `field_type`, `sort_order`, `is_required`) VALUES
(2, '报表编号', 'reportNo', 'TEXT', 1, 1),
(2, '报表期间', 'reportPeriod', 'TEXT', 2, 1),
(2, '编制单位', 'company', 'TEXT', 3, 1),
(2, '编制人', 'creator', 'TEXT', 4, 1),
(2, '审核人', 'reviewer', 'TEXT', 5, 1),
(2, '货币单位', 'currency', 'TEXT', 6, 1);

-- 初始化模板字段数据 - 法律案件报告模板
INSERT INTO `tb_document_template_field` (`template_id`, `field_label`, `field_name`, `field_type`, `sort_order`, `is_required`) VALUES
(3, '案件编号', 'caseNo', 'TEXT', 1, 1),
(3, '案件类型', 'caseType', 'TEXT', 2, 1),
(3, '立案日期', 'filingDate', 'DATE', 3, 1),
(3, '承办律师', 'lawyer', 'TEXT', 4, 1),
(3, '案件状态', 'status', 'TEXT', 5, 1),
(3, '受理法院', 'court', 'TEXT', 6, 1);

-- =====================================================
-- 视图：模板字段视图
-- =====================================================
DROP VIEW IF EXISTS `v_template_fields`;
CREATE VIEW `v_template_fields` AS
SELECT 
  t.id AS template_id,
  t.template_name,
  t.template_code,
  t.template_type,
  t.category,
  f.id AS field_id,
  f.field_label,
  f.field_name,
  f.field_type,
  f.sort_order,
  f.is_required,
  f.default_value
FROM tb_document_export_template t
LEFT JOIN tb_document_template_field f ON t.id = f.template_id AND f.is_deleted = 0
WHERE t.is_deleted = 0 AND t.status = 'ACTIVE';

-- =====================================================
-- 存储过程：清理过期的文件锁定
-- =====================================================
DROP PROCEDURE IF EXISTS `sp_cleanup_expired_locks`;
DELIMITER $$
CREATE PROCEDURE `sp_cleanup_expired_locks`()
BEGIN
  UPDATE tb_file_lock
  SET status = 'EXPIRED'
  WHERE status = 'LOCKED'
  AND (expire_time IS NULL OR expire_time < NOW());
  
  SELECT ROW_COUNT() AS affected_rows;
END$$
DELIMITER ;

-- =====================================================
-- 触发器：模板删除时级联删除字段
-- =====================================================
DROP TRIGGER IF EXISTS `trg_template_delete`;
DELIMITER $$
CREATE TRIGGER `trg_template_delete`
BEFORE DELETE ON tb_document_export_template
FOR EACH ROW
BEGIN
  UPDATE tb_document_template_field
  SET is_deleted = 1
  WHERE template_id = OLD.id;
END$$
DELIMITER ;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 数据查询示例
-- =====================================================

-- 查询所有模板及其字段
-- SELECT * FROM v_template_fields;

-- 查询某个模板的详细信息
-- SELECT t.*, f.field_label, f.field_name, f.field_type
-- FROM tb_document_export_template t
-- LEFT JOIN tb_document_template_field f ON t.id = f.template_id
-- WHERE t.template_code = 'DATA_REPORT' AND t.is_deleted = 0;

-- 查询当前被锁定的文件
-- SELECT fl.*, fr.original_file_name
-- FROM tb_file_lock fl
-- JOIN tb_file_record fr ON fl.file_id = fr.id
-- WHERE fl.status = 'LOCKED';

-- 查询文件的编辑历史
-- SELECT deh.*, u.real_name AS editor_real_name
-- FROM tb_document_edit_history deh
-- LEFT JOIN tb_user u ON deh.editor_id = u.id
-- WHERE deh.file_id = 123
-- ORDER BY deh.version DESC;

-- =====================================================
-- 结束
-- =====================================================
