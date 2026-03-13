-- =====================================================
-- 文档库数据库表结构
-- 包含: 文件夹管理、文档管理、版本管理、权限控制
-- 创建日期: 2026-03-09
-- =====================================================

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

-- ---------------------------------------------------
-- 1. 文档文件夹表 (tb_lib_document_folder)
-- 用于实现文件夹/目录结构，支持多级嵌套
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_folder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `folder_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '文件夹名称',
  `folder_path` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '文件夹完整路径(如: /合同/2024年)',
  `parent_id` bigint DEFAULT NULL COMMENT '父文件夹ID, NULL表示根目录',
  `folder_level` int DEFAULT 1 COMMENT '文件夹层级, 根目录为1',
  `sort_order` int DEFAULT 0 COMMENT '排序序号',
  `description` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件夹描述',
  `icon` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件夹图标',
  `color` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件夹颜色',
  `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_folder_path` (`folder_path`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_user_id` (`create_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_parent_status` (`parent_id`, `status`),
  CONSTRAINT `fk_lib_folder_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档文件夹表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 2. 文档表 (tb_lib_document)
-- 存储文档基本信息，支持Word/Excel等格式
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '文档名称',
  `document_code` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文档编码(唯一标识)',
  `folder_id` bigint DEFAULT NULL COMMENT '所属文件夹ID, NULL表示根目录',
  `document_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '文档类型: WORD-Word文档, EXCEL-Excel表格, PDF-PDF文档, OTHER-其他',
  `file_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '文件存储路径',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
  `file_extension` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件扩展名(如: docx, xlsx, pdf)',
  `mime_type` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'MIME类型',
  `current_version` int DEFAULT 1 COMMENT '当前版本号',
  `description` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文档描述',
  `tags` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标签(逗号分隔)',
  `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开: 0-否, 1-是',
  `is_locked` tinyint(1) DEFAULT '0' COMMENT '是否锁定: 0-否, 1-是(锁定时不可编辑)',
  `locked_by` bigint DEFAULT NULL COMMENT '锁定者ID',
  `locked_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `download_count` int DEFAULT 0 COMMENT '下载次数',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除, DRAFT-草稿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_code` (`document_code`),
  KEY `idx_folder_id` (`folder_id`),
  KEY `idx_document_type` (`document_type`),
  KEY `idx_create_user_id` (`create_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_folder_status` (`folder_id`, `status`),
  KEY `idx_is_public` (`is_public`),
  CONSTRAINT `fk_lib_document_folder` FOREIGN KEY (`folder_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 3. 文档版本表 (tb_lib_document_version)
-- 记录文档的所有历史版本，支持版本回溯
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_version` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version_number` int NOT NULL COMMENT '版本号',
  `version_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '版本名称(如: v1.0, v1.1)',
  `file_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '该版本的原始文件名',
  `file_path` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '该版本的文件存储路径',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
  `change_summary` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '变更说明',
  `change_type` varchar(20) COLLATE utf8mb4_bin DEFAULT 'UPDATE' COMMENT '变更类型: CREATE-创建, UPDATE-更新, RESTORE-恢复',
  `is_major` tinyint(1) DEFAULT '0' COMMENT '是否主版本: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_version` (`document_id`, `version_number`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_create_user_id` (`create_user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_version_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档版本表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 4. 文档权限表 (tb_lib_document_permission)
-- 定义权限类型和权限模板
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '权限名称',
  `permission_code` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '权限编码',
  `permission_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '权限类型: READ-读取, WRITE-编辑, DELETE-删除, ADMIN-管理, DOWNLOAD-下载',
  `description` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限描述',
  `sort_order` int DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 5. 文档文件夹权限关联表 (tb_lib_folder_permission)
-- 配置文件夹级别的权限控制
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_folder_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_folder_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `folder_id` bigint NOT NULL COMMENT '文件夹ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `target_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '目标类型: USER-用户, ROLE-角色, DEPARTMENT-部门',
  `target_id` bigint NOT NULL COMMENT '目标ID(用户ID/角色ID/部门ID)',
  `is_inherit` tinyint(1) DEFAULT '1' COMMENT '是否继承到子文件夹: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_folder_permission_target` (`folder_id`, `permission_id`, `target_type`, `target_id`),
  KEY `idx_folder_id` (`folder_id`),
  KEY `idx_permission_id` (`permission_id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_folder_perm_folder` FOREIGN KEY (`folder_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_lib_folder_perm_permission` FOREIGN KEY (`permission_id`) REFERENCES `tb_lib_document_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文件夹权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 6. 文档权限关联表 (tb_lib_document_permission_rel)
-- 配置文档级别的权限控制
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_permission_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_permission_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `target_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '目标类型: USER-用户, ROLE-角色, DEPARTMENT-部门',
  `target_id` bigint NOT NULL COMMENT '目标ID(用户ID/角色ID/部门ID)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_permission_target` (`document_id`, `permission_id`, `target_type`, `target_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_permission_id` (`permission_id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_doc_perm_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_lib_doc_perm_permission` FOREIGN KEY (`permission_id`) REFERENCES `tb_lib_document_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 7. 文档操作日志表 (tb_lib_document_operation_log)
-- 记录文档的所有操作历史
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint DEFAULT NULL COMMENT '文档ID',
  `document_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文档名称(冗余存储)',
  `folder_id` bigint DEFAULT NULL COMMENT '文件夹ID',
  `operation_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '操作类型: CREATE-创建, UPDATE-更新, DELETE-删除, DOWNLOAD-下载, VIEW-查看, MOVE-移动, COPY-复制, RENAME-重命名, LOCK-锁定, UNLOCK-解锁, RESTORE-恢复版本',
  `operation_detail` text COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作详情(JSON格式)',
  `old_value` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '变更前值',
  `new_value` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '变更后值',
  `ip_address` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户代理',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '操作者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_folder_id` (`folder_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_user_id` (`create_user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_log_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_lib_log_folder` FOREIGN KEY (`folder_id`) REFERENCES `tb_lib_document_folder` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 8. 文档收藏表 (tb_lib_document_favorite)
-- 用户收藏的文档
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `folder_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '收藏夹名称',
  `sort_order` int DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_user` (`document_id`, `user_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_favorite_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 9. 文档分享表 (tb_lib_document_share)
-- 文档分享链接管理
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_share` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `share_code` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '分享码(唯一)',
  `share_password` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分享密码(可选)',
  `permission_type` varchar(20) COLLATE utf8mb4_bin DEFAULT 'READ' COMMENT '分享权限: READ-仅查看, DOWNLOAD-可下载, EDIT-可编辑',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间(NULL表示永久有效)',
  `max_access_count` int DEFAULT 0 COMMENT '最大访问次数(0表示不限制)',
  `access_count` int DEFAULT 0 COMMENT '已访问次数',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_share_code` (`share_code`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_create_user_id` (`create_user_id`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_share_document` FOREIGN KEY (`document_id`) REFERENCES `tb_lib_document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 10. 文档分享访问记录表 (tb_lib_document_share_access)
-- 记录分享链接的访问历史
-- ---------------------------------------------------
DROP TABLE IF EXISTS `tb_lib_document_share_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_lib_document_share_access` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `share_id` bigint NOT NULL COMMENT '分享ID',
  `visitor_ip` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问者IP',
  `visitor_user_id` bigint DEFAULT NULL COMMENT '访问者用户ID(如已登录)',
  `access_type` varchar(20) COLLATE utf8mb4_bin DEFAULT 'VIEW' COMMENT '访问类型: VIEW-查看, DOWNLOAD-下载',
  `user_agent` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户代理',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_share_id` (`share_id`),
  KEY `idx_visitor_user_id` (`visitor_user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_lib_access_share` FOREIGN KEY (`share_id`) REFERENCES `tb_lib_document_share` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='文档分享访问记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------
-- 初始化权限数据
-- ---------------------------------------------------
INSERT INTO `tb_lib_document_permission` (`permission_name`, `permission_code`, `permission_type`, `description`, `sort_order`, `status`) VALUES
('查看权限', 'DOC_READ', 'READ', '查看文档内容和信息', 1, 'ACTIVE'),
('编辑权限', 'DOC_WRITE', 'WRITE', '编辑文档内容', 2, 'ACTIVE'),
('删除权限', 'DOC_DELETE', 'DELETE', '删除文档', 3, 'ACTIVE'),
('下载权限', 'DOC_DOWNLOAD', 'DOWNLOAD', '下载文档到本地', 4, 'ACTIVE'),
('管理权限', 'DOC_ADMIN', 'ADMIN', '管理文档权限和设置', 5, 'ACTIVE'),
('分享权限', 'DOC_SHARE', 'SHARE', '创建文档分享链接', 6, 'ACTIVE');

-- ---------------------------------------------------
-- 创建根目录文件夹(可选)
-- ---------------------------------------------------
INSERT INTO `tb_lib_document_folder` (`folder_name`, `folder_path`, `parent_id`, `folder_level`, `sort_order`, `description`, `is_public`, `status`) VALUES
('全部文档', '/', NULL, 1, 0, '文档库根目录', 1, 'ACTIVE');

-- ---------------------------------------------------
-- 恢复原始设置
-- ---------------------------------------------------
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
