-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: law
-- ------------------------------------------------------
-- Server version	8.0.44

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

--
-- Table structure for table `tb_user`
--

DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户账号',
  `password` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户密码',
  `real_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户姓名',
  `mobile` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `is_valid` char(1) COLLATE utf8mb4_bin DEFAULT '1' COMMENT '是否有效',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '用户状态: ACTIVE-正常, INACTIVE-禁用, LOCKED-锁定, DELETED-删除',
  `login_type` char(1) COLLATE utf8mb4_bin DEFAULT '1' COMMENT '登录类型',
  `bind_device` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '绑定设备',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '最后登录IP',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  `pwd_error_count` int DEFAULT '0' COMMENT '密码错误次数',
  `pwd_error_time` datetime DEFAULT NULL COMMENT '密码错误时间',
  `pwd_expire_time` datetime DEFAULT NULL COMMENT '密码过期时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_mobile` (`mobile`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_role`
--

DROP TABLE IF EXISTS `tb_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '角色代码',
  `role_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `role_desc` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色描述',
  `is_system` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '是否系统角色',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_permission`
--

DROP TABLE IF EXISTS `tb_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `perm_code` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '权限代码',
  `perm_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '权限名称',
  `perm_type` char(1) COLLATE utf8mb4_bin NOT NULL COMMENT '权限类型',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID',
  `path` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路径',
  `component` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '组件',
  `icon` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_external` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '是否外部链接',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_perm_type` (`perm_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_user_role`
--

DROP TABLE IF EXISTS `tb_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_role_permission`
--

DROP TABLE IF EXISTS `tb_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `perm_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_perm_id` (`perm_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_permission_perm` FOREIGN KEY (`perm_id`) REFERENCES `tb_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_token`
--

DROP TABLE IF EXISTS `tb_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_token` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `token_type` char(1) COLLATE utf8mb4_bin NOT NULL COMMENT 'Token类型',
  `token_value` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT 'Token值',
  `device_id` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '设备ID',
  `device_info` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '设备信息',
  `ip_address` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'IP地址',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `revoke_time` datetime DEFAULT NULL COMMENT '撤销时间',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_token_value` (`token_value`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_token_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='Token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_sms_code`
--

DROP TABLE IF EXISTS `tb_sms_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sms_code` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mobile` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
  `code` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '验证码',
  `sms_type` char(1) COLLATE utf8mb4_bin NOT NULL COMMENT '短信类型',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `used_status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '使用状态',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_used_status` (`used_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='短信验证码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_login_fail`
--

DROP TABLE IF EXISTS `tb_login_fail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_login_fail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `login_account` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '登录账号',
  `fail_ip` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '失败IP',
  `fail_count` int DEFAULT '1' COMMENT '失败次数',
  `fail_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '失败类型: PASSWORD-密码错误, ACCOUNT-账号不存在, LOCKED-账号锁定, CAPTCHA-验证码错误',
  `last_fail_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后失败时间',
  `unlock_time` datetime DEFAULT NULL COMMENT '解锁时间',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_login_account` (`login_account`),
  KEY `idx_fail_ip` (`fail_ip`),
  KEY `idx_last_fail_time` (`last_fail_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='登录失败记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_login_record`
--

DROP TABLE IF EXISTS `tb_login_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_login_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `user_account` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户账号',
  `user_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户姓名',
  `login_type` varchar(10) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录类型',
  `login_ip` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录IP',
  `login_location` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录位置',
  `login_device` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录设备',
  `login_browser` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '浏览器',
  `login_os` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作系统',
  `login_status` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录状态: SUCCESS-成功, FAILED-失败',
  `error_msg` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '错误信息',
  `risk_level` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '风险等级: LOW-低, MEDIUM-中, HIGH-高',
  `is_known_device` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否已知设备: YES-是, NO-否',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_account` (`user_account`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_login_status` (`login_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='登录记录表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `tb_system_config`
--

DROP TABLE IF EXISTS `tb_system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '配置键',
  `config_value` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '配置值',
  `config_desc` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '配置描述',
  `config_group` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '配置分组',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_group` (`config_group`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_court`
--

DROP TABLE IF EXISTS `tb_court`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_court` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '法院ID',
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法院全称',
  `short_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法院简称',
  `court_level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法院级别',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `responsible_user_id` bigint DEFAULT NULL COMMENT '负责人用户ID',
  `undertaking_judge` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '承办法官',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_short_name` (`short_name`),
  KEY `idx_full_name` (`full_name`),
  KEY `idx_court_level` (`court_level`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_responsible_user_id` (`responsible_user_id`),
  CONSTRAINT `fk_court_responsible_user` FOREIGN KEY (`responsible_user_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='法院信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_debtor_enterprise`
--

DROP TABLE IF EXISTS `tb_debtor_enterprise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_debtor_enterprise` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '债务人ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `enterprise_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '企业名称',
  `unified_social_credit_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '统一社会信用代码',
  `legal_representative` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法定代表人',
  `registration_authority` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登记机关',
  `establishment_date` date DEFAULT NULL COMMENT '成立日期',
  `registered_capital` decimal(18,4) DEFAULT NULL COMMENT '注册资本',
  `business_scope` text COLLATE utf8mb4_unicode_ci COMMENT '经营范围',
  `enterprise_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '企业类型',
  `industry` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属行业',
  `registered_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册地址',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `contact_person` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_unified_social_credit_code` (`unified_social_credit_code`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_enterprise_name` (`enterprise_name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_debtor_enterprise_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='债务人信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_administrator`
--

DROP TABLE IF EXISTS `tb_administrator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_administrator` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理人ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `administrator_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理人类型',
  `responsible_person_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系邮箱',
  `office_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '办公地址',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_responsible_person_id` (`responsible_person_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_administrator_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_administrator_responsible_person` FOREIGN KEY (`responsible_person_id`) REFERENCES `tb_administrator_staff` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理人信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_administrator_staff`
--

DROP TABLE IF EXISTS `tb_administrator_staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_administrator_staff` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `administrator_id` bigint DEFAULT NULL COMMENT '所属管理人ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `staff_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '人员类型',
  `id_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `lawyer_license_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '律师执业证号',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `responsibility` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职责',
  `appointment_date` date DEFAULT NULL COMMENT '任命日期',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_administrator_id` (`administrator_id`),
  KEY `idx_name` (`name`),
  KEY `idx_staff_type` (`staff_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_administrator_staff_administrator` FOREIGN KEY (`administrator_id`) REFERENCES `tb_administrator` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工信息表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `tb_bank_account`
--

DROP TABLE IF EXISTS `tb_bank_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_bank_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `account_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户名称',
  `bank_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行',
  `account_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
  `account_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户类型',
  `currency` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'CNY' COMMENT '币种',
  `current_balance` decimal(18,2) DEFAULT NULL COMMENT '当前余额',
  `opening_date` date DEFAULT NULL COMMENT '开户日期',
  `closing_date` date DEFAULT NULL COMMENT '销户日期',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码（使用AES-256加密存储）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_number` (`account_number`),
  KEY `idx_account_name` (`account_name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='银行账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_bankrupt_case`
--

DROP TABLE IF EXISTS `tb_bankrupt_case`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_bankrupt_case` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '案件ID',
  `case_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案号',
  `case_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案件名称',
  `acceptance_date` date DEFAULT NULL COMMENT '受理日期',
  `case_source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案件来源',
  `acceptance_court` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '受理法院',
  `designated_institution` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '指定机构',
  `main_responsible_person` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主要负责人',
  `is_simplified_trial` tinyint(1) DEFAULT '0' COMMENT '是否简化审: 0-否, 1-是',
  `case_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案由',
  `case_progress` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案件进度: FIRST-第一阶段, SECOND-第二阶段, THIRD-第三阶段, FOURTH-第四阶段, FIFTH-第五阶段, SIXTH-第六阶段, SEVENTH-第七阶段',
  `debt_claim_deadline` datetime DEFAULT NULL COMMENT '债权申报截止时间',
  `filing_date` date DEFAULT NULL COMMENT '立案日期',
  `closing_date` date DEFAULT NULL COMMENT '结案日期',
  `bankruptcy_date` date DEFAULT NULL COMMENT '破产时间',
  `termination_date` date DEFAULT NULL COMMENT '终结时间',
  `cancellation_date` date DEFAULT NULL COMMENT '注销时间',
  `archiving_date` date DEFAULT NULL COMMENT '归档时间',
  `remarks` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `file_upload_path` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件上传路径',
  `undertaking_personnel` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '承办人员',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `review_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已驳回',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_opinion` text COLLATE utf8mb4_unicode_ci COMMENT '审核意见',
  `review_count` int DEFAULT '0' COMMENT '审核次数',
  `case_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '案件状态: PENDING-待处理, IN_PROGRESS-进行中, COMPLETED-已完成, CLOSED-已结案, TERMINATED-已终结, ARCHIVED-已归档',
  `designated_judge` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '指定法官',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_case_number` (`case_number`),
  KEY `idx_case_status` (`case_status`),
  KEY `idx_acceptance_date` (`acceptance_date`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_creditor_info`
--

DROP TABLE IF EXISTS `tb_creditor_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_creditor_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '债权人ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `creditor_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权人名称',
  `creditor_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权人类型',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系邮箱',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `id_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号/统一社会信用代码',
  `legal_representative` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法定代表人',
  `registered_capital` decimal(18,4) DEFAULT NULL COMMENT '注册资本',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_creditor_name` (`creditor_name`),
  KEY `idx_creditor_type` (`creditor_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_creditor_info_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='债权人信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_creditor_claim`
--

DROP TABLE IF EXISTS `tb_creditor_claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_creditor_claim` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '债权申报ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `case_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '案件名称',
  `debtor` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债务人',
  `bank_account` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行账户',
  `creditor_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权人名称',
  `creditor_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权人类型',
  `credit_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '信用代码',
  `legal_representative` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法定代表人',
  `service_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '送达地址',
  `agent_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代理人姓名',
  `agent_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代理人电话',
  `agent_id_card` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代理人身份证号',
  `agent_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代理人地址',
  `account_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户名称',
  `creditor_bank_account` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行账号',
  `bank_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户银行',
  `principal` decimal(18,2) DEFAULT NULL COMMENT '本金',
  `interest` decimal(18,2) DEFAULT NULL COMMENT '利息',
  `penalty` decimal(18,2) DEFAULT NULL COMMENT '罚金',
  `other_losses` decimal(18,2) DEFAULT NULL COMMENT '其他损失',
  `total_amount` decimal(18,2) DEFAULT NULL COMMENT '总金额',
  `has_court_judgment` tinyint(1) DEFAULT '0' COMMENT '是否有法院判决: 0-否, 1-是',
  `has_execution` tinyint(1) DEFAULT '0' COMMENT '是否有执行: 0-否, 1-是',
  `has_collateral` tinyint(1) DEFAULT '0' COMMENT '是否有担保: 0-否, 1-是',
  `claim_nature` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权性质',
  `claim_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权类型',
  `claim_facts` text COLLATE utf8mb4_unicode_ci COMMENT '债权事实',
  `claim_nature_manager` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权性质（管理人）',
  `claim_identifier` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '债权标识',
  `evidence_list` text COLLATE utf8mb4_unicode_ci COMMENT '证据清单',
  `evidence_materials` text COLLATE utf8mb4_unicode_ci COMMENT '证据材料',
  `evidence_attachments` text COLLATE utf8mb4_unicode_ci COMMENT '证据附件',
  `remarks` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `registration_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '登记状态: PENDING-待登记, REGISTERED-已登记, REJECTED-已驳回',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_creditor_name` (`creditor_name`),
  KEY `idx_claim_type` (`claim_type`),
  KEY `idx_registration_status` (`registration_status`),
  KEY `idx_total_amount` (`total_amount`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_creditor_claim_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='债权申报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_fund_account`
--

DROP TABLE IF EXISTS `tb_fund_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_fund_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `case_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '案件名称',
  `account_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称',
  `account_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户类型',
  `initial_balance` decimal(18,2) NOT NULL COMMENT '初始余额',
  `current_balance` decimal(18,2) NOT NULL COMMENT '当前余额',
  `bank_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行账号',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_fund_account_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_fund_flow`
--

DROP TABLE IF EXISTS `tb_fund_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_fund_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `case_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '案件名称',
  `fund_account_id` bigint NOT NULL COMMENT '资金账户ID',
  `flow_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水类型',
  `amount` decimal(18,2) NOT NULL COMMENT '金额',
  `balance_before` decimal(18,2) NOT NULL COMMENT '变动前余额',
  `balance_after` decimal(18,2) NOT NULL COMMENT '变动后余额',
  `transaction_date` datetime NOT NULL COMMENT '交易日期',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `related_document` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '相关文档',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_fund_account_id` (`fund_account_id`),
  KEY `idx_flow_type` (`flow_type`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_fund_flow_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_fund_flow_fund_account` FOREIGN KEY (`fund_account_id`) REFERENCES `tb_fund_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_fund_approval`
--

DROP TABLE IF EXISTS `tb_fund_approval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_fund_approval` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `flow_id` bigint NOT NULL COMMENT '流程ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `amount` decimal(18,2) NOT NULL COMMENT '金额',
  `approval_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已通过, REJECTED-已驳回',
  `approval_content` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批内容',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_flow_id` (`flow_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_approver_id` (`approver_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_fund_approval_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_fund_approval_flow` FOREIGN KEY (`flow_id`) REFERENCES `tb_fund_flow` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金审批表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_fund_operation_log`
--

DROP TABLE IF EXISTS `tb_fund_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_fund_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `operation_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `operation_content` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作内容',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `browser_info` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器信息',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint NOT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_fund_operation_log_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_file_record`
--

DROP TABLE IF EXISTS `tb_file_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_file_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件记录ID',
  `original_file_name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `stored_file_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_extension` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `file_hash` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件哈希值',
  `biz_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型',
  `biz_id` bigint NOT NULL COMMENT '业务ID',
  `upload_time` datetime NOT NULL COMMENT '上传时间',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `file_status` tinyint(1) DEFAULT '1' COMMENT '文件状态: 0-无效, 1-有效',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_user_id` bigint DEFAULT NULL COMMENT '删除用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_biz_type` (`biz_type`),
  KEY `idx_biz_id` (`biz_id`),
  KEY `idx_file_status` (`file_status`),
  KEY `idx_status` (`status`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_activity`
--

DROP TABLE IF EXISTS `tb_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名称',
  `type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `content` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作内容',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_activity_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_notification`
--

DROP TABLE IF EXISTS `tb_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读（0-未读，1-已读）',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_todo`
--

DROP TABLE IF EXISTS `tb_todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_todo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办标题',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '待办描述',
  `priority` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'MEDIUM' COMMENT '优先级: LOW-低, MEDIUM-中, HIGH-高',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '状态: PENDING-待处理, COMPLETED-已完成, CANCELLED-已取消',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `source_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型',
  `source_id` bigint DEFAULT NULL COMMENT '来源ID',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `completed_time` datetime DEFAULT NULL COMMENT '完成时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deadline` (`deadline`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_todo_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统待办事项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_work_plan`
--

DROP TABLE IF EXISTS `tb_work_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_work_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工作计划ID',
  `plan_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单据号',
  `plan_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划类型: WEEKLY-周计划, MONTHLY-月计划, QUARTERLY-季计划, YEARLY-年计划, SPECIAL-专项计划',
  `plan_content` text COLLATE utf8mb4_unicode_ci COMMENT '计划内容',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `responsible_user_id` bigint DEFAULT NULL COMMENT '负责人用户ID',
  `execution_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执行状态: NOT_STARTED-未开始, IN_PROGRESS-进行中, COMPLETED-已完成, DELAYED-延期, CANCELLED-已取消',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_number` (`plan_number`),
  KEY `idx_plan_type` (`plan_type`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_responsible_user_id` (`responsible_user_id`),
  KEY `idx_execution_status` (`execution_status`),
  KEY `idx_start_date` (`start_date`),
  KEY `idx_end_date` (`end_date`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_work_plan_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_work_plan_responsible_user` FOREIGN KEY (`responsible_user_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作计划表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `tb_work_team`
--

DROP TABLE IF EXISTS `tb_work_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_work_team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_name` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '团队名称',
  `team_leader_id` bigint DEFAULT NULL COMMENT '团队负责人ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `team_description` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '团队描述',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_team_leader_id` (`team_leader_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_work_team_leader` FOREIGN KEY (`team_leader_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_work_team_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作团队表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_work_team_member`
--

DROP TABLE IF EXISTS `tb_work_team_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_work_team_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `case_id` bigint NOT NULL COMMENT '案件ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `team_role` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '团队角色（系统权限优先于团队权限，如果用户系统角色为LAWYER，被分配到团队后也可访问该案件）',
  `permission_level` varchar(20) COLLATE utf8mb4_bin DEFAULT 'VIEW' COMMENT '权限级别: VIEW-查看, EDIT-编辑, ADMIN-管理',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_team_role` (`team_role`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_work_team_member_team` FOREIGN KEY (`team_id`) REFERENCES `tb_work_team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_team_member_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_team_member_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作团队成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_work_team_permission`
--

DROP TABLE IF EXISTS `tb_work_team_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_work_team_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_member_id` bigint NOT NULL COMMENT '团队成员ID',
  `module_type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '模块类型',
  `permission_type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '权限类型',
  `is_allowed` tinyint(1) DEFAULT '1' COMMENT '是否允许',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_team_member_id` (`team_member_id`),
  KEY `idx_module_type` (`module_type`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_work_team_permission_member` FOREIGN KEY (`team_member_id`) REFERENCES `tb_work_team_member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作团队权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_case_announcement`
--

DROP TABLE IF EXISTS `tb_case_announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `announcement_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公告类型: NOTICE-通知, ANNOUNCEMENT-公告, WARNING-警告',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, ARCHIVED-已归档, DELETED-已删除',
  `publisher_id` bigint DEFAULT NULL COMMENT '发布者ID',
  `publisher_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布者姓名',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶: 0-否, 1-是',
  `top_expire_time` datetime DEFAULT NULL COMMENT '置顶过期时间',
  `attachments` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_announcement_type` (`announcement_type`),
  KEY `idx_status` (`status`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_case_announcement_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_case_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `tb_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案件公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_case_announcement_view`
--

DROP TABLE IF EXISTS `tb_case_announcement_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_announcement_view` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `announcement_id` bigint NOT NULL COMMENT '公告ID',
  `case_id` bigint DEFAULT NULL COMMENT '案件ID',
  `viewer_id` bigint NOT NULL COMMENT '查看者ID',
  `viewer_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查看者姓名',
  `view_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '查看时间',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  KEY `idx_announcement_id` (`announcement_id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_viewer_id` (`viewer_id`),
  KEY `idx_view_time` (`view_time`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_announcement_view_announcement` FOREIGN KEY (`announcement_id`) REFERENCES `tb_case_announcement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_announcement_view_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_announcement_view_viewer` FOREIGN KEY (`viewer_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案件公告查看记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-08
