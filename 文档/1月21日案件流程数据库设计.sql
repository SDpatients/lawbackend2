-- ========================================
-- 案件流程管理模块数据库设计
-- 创建日期: 2026-01-21
-- 版本: 1.0
-- 说明: 本脚本用于创建案件任务管理表，支持案件全流程的文件集中管理
-- ========================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ========================================
-- 1. 案件任务表 (tb_case_task)
-- ========================================
-- 表说明: 存储案件全流程的23个核心任务信息，每个案件创建时自动生成23个任务
-- ========================================

DROP TABLE IF EXISTS `tb_case_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_case_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_id` bigint NOT NULL COMMENT '案件ID，关联tb_bankrupt_case表',
  `task_code` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '任务编号: TASK_001-TASK_023',
  `task_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `task_description` text COLLATE utf8mb4_unicode_ci COMMENT '任务描述',
  `status` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '任务状态: IN_PROGRESS-进行中, COMPLETED-已完成, REVIEWING-核审中, SKIPPED-跳过, REJECTED-被驳回',
  `sort_order` int DEFAULT NULL COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status_audit` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_case_task_code` (`case_id`, `task_code`) COMMENT '同一案件下任务编号唯一',
  KEY `idx_case_id` (`case_id`) COMMENT '案件ID索引',
  KEY `idx_task_code` (`task_code`) COMMENT '任务编号索引',
  KEY `idx_status` (`status`) COMMENT '任务状态索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  KEY `idx_sort_order` (`sort_order`) COMMENT '排序序号索引',
  CONSTRAINT `fk_case_task_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='案件任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ========================================
-- 2. 案件任务初始化数据（示例）
-- ========================================
-- 说明: 以下为案件任务初始化示例，实际数据由系统在案件创建时自动生成
-- ========================================

-- 示例: 为案件ID=1创建23个核心任务
-- 注意: 实际应用中，这些数据由CaseTaskService.createTasksForCase()方法自动创建

-- INSERT INTO `tb_case_task` (`case_id`, `task_code`, `task_name`, `task_description`, `status`, `sort_order`, `create_user_id`) VALUES
-- (1, 'TASK_001', '提交破产申请材料', '申请人', 'IN_PROGRESS', 1, 1),
-- (1, 'TASK_002', '裁定受理并公告', '法院', 'IN_PROGRESS', 2, 1),
-- (1, 'TASK_003', '全面接管债务人', '管理人', 'IN_PROGRESS', 3, 1),
-- (1, 'TASK_004', '管理人印章', '管理人', 'IN_PROGRESS', 4, 1),
-- (1, 'TASK_005', '调查财产及经营状况', '管理人', 'IN_PROGRESS', 5, 1),
-- (1, 'TASK_006', '决定合同继续履行或解除', '管理人', 'IN_PROGRESS', 6, 1),
-- (1, 'TASK_007', '追收债务人财产', '管理人', 'IN_PROGRESS', 7, 1),
-- (1, 'TASK_008', '通知已知债权人并公告', '管理人', 'IN_PROGRESS', 8, 1),
-- (1, 'TASK_009', '接收、登记债权申报', '管理人', 'IN_PROGRESS', 9, 1),
-- (1, 'TASK_010', '审查申报债权并编制债权表', '管理人', 'IN_PROGRESS', 10, 1),
-- (1, 'TASK_011', '筹备第一次债权人会议', '管理人', 'IN_PROGRESS', 11, 1),
-- (1, 'TASK_012', '召开会议核查债权与议决事项', '债权人会议', 'IN_PROGRESS', 12, 1),
-- (1, 'TASK_013', '表决通过财产变价/分配方案', '债权人会议、法院', 'IN_PROGRESS', 13, 1),
-- (1, 'TASK_014', '宣告重整与和解', '法院', 'IN_PROGRESS', 14, 1),
-- (1, 'TASK_015', '审查宣告破产条件', '法院', 'IN_PROGRESS', 15, 1),
-- (1, 'TASK_016', '裁定宣告债务人破产', '法院', 'IN_PROGRESS', 16, 1),
-- (1, 'TASK_017', '拟定并执行财产变价方案', '管理人', 'IN_PROGRESS', 17, 1),
-- (1, 'TASK_018', '执行破产财产分配', '管理人', 'IN_PROGRESS', 18, 1),
-- (1, 'TASK_019', '破产费用与共益债务', '管理人', 'IN_PROGRESS', 19, 1),
-- (1, 'TASK_020', '提请终结破产程序', '管理人', 'IN_PROGRESS', 20, 1),
-- (1, 'TASK_021', '法院裁定并公告', '法院', 'IN_PROGRESS', 21, 1),
-- (1, 'TASK_022', '办理企业注销登记', '管理人', 'IN_PROGRESS', 22, 1),
-- (1, 'TASK_023', '管理人终止执行职务并归档', '管理人', 'IN_PROGRESS', 23, 1);

-- ========================================
-- 3. 文件记录表扩展说明
-- ========================================
-- 说明: 复用现有的tb_file_record表，通过biz_type和biz_id关联到案件任务
-- ========================================

-- tb_file_record表已存在，无需创建
-- 关联方式:
--   biz_type = 'CASE_TASK' (案件任务)
--   biz_id = tb_case_task表的id值

-- 示例: 为案件任务上传文件
-- INSERT INTO `tb_file_record` (`original_file_name`, `stored_file_name`, `file_path`, `file_size`, `file_extension`, `mime_type`, `biz_type`, `biz_id`, `upload_time`, `upload_user_id`, `file_status`, `create_user_id`) VALUES
-- ('破产申请书.pdf', '20260121_001.pdf', '/uploads/2026/01/21/20260121_001.pdf', 1024000, 'pdf', 'application/pdf', 'CASE_TASK', '1', NOW(), 1, 1, 1);

-- ========================================
-- 4. 常用查询示例
-- ========================================

-- 4.1 查询某个案件的所有任务
-- SELECT * FROM tb_case_task WHERE case_id = 1 AND is_deleted = 0 ORDER BY sort_order;

-- 4.2 查询某个案件的任务统计
-- SELECT
--   COUNT(*) AS total_tasks,
--   SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_tasks,
--   SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress_tasks,
--   SUM(CASE WHEN status = 'REVIEWING' THEN 1 ELSE 0 END) AS reviewing_tasks,
--   SUM(CASE WHEN status = 'SKIPPED' THEN 1 ELSE 0 END) AS skipped_tasks,
--   SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected_tasks
-- FROM tb_case_task
-- WHERE case_id = 1 AND is_deleted = 0;

-- 4.3 查询某个任务的所有文件
-- SELECT * FROM tb_file_record WHERE biz_type = 'CASE_TASK' AND biz_id = '1' AND is_deleted = 0 ORDER BY upload_time DESC;

-- 4.4 查询某个案件的任务及文件数量
-- SELECT
--   ct.*,
--   (SELECT COUNT(*) FROM tb_file_record WHERE biz_type = 'CASE_TASK' AND biz_id = ct.id AND is_deleted = 0) AS file_count
-- FROM tb_case_task ct
-- WHERE ct.case_id = 1 AND ct.is_deleted = 0
-- ORDER BY ct.sort_order;

-- 4.5 查询某个案件的完成率
-- SELECT
--   COUNT(*) AS total_tasks,
--   SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_tasks,
--   ROUND(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS completion_rate
-- FROM tb_case_task
-- WHERE case_id = 1 AND is_deleted = 0;

-- ========================================
-- 5. 索引说明
-- ========================================
-- 5.1 主键索引: PRIMARY KEY (id)
-- 5.2 唯一索引: uk_case_task_code (case_id, task_code) - 确保同一案件下任务编号唯一
-- 5.3 普通索引:
--   - idx_case_id: 用于按案件ID查询任务
--   - idx_task_code: 用于按任务编号查询
--   - idx_status: 用于按任务状态筛选
--   - idx_create_time: 用于按创建时间排序
--   - idx_sort_order: 用于按排序序号排序
-- 5.4 外键约束: fk_case_task_case - 关联tb_bankrupt_case表

-- ========================================
-- 6. 数据字典
-- ========================================

-- 6.1 任务状态字典 (status字段)
-- IN_PROGRESS - 进行中
-- COMPLETED - 已完成
-- REVIEWING - 核审中
-- SKIPPED - 跳过
-- REJECTED - 被驳回

-- 6.2 任务编号字典 (task_code字段)
-- TASK_001 - 提交破产申请材料
-- TASK_002 - 裁定受理并公告
-- TASK_003 - 全面接管债务人
-- TASK_004 - 管理人印章
-- TASK_005 - 调查财产及经营状况
-- TASK_006 - 决定合同继续履行或解除
-- TASK_007 - 追收债务人财产
-- TASK_008 - 通知已知债权人并公告
-- TASK_009 - 接收、登记债权申报
-- TASK_010 - 审查申报债权并编制债权表
-- TASK_011 - 筹备第一次债权人会议
-- TASK_012 - 召开会议核查债权与议决事项
-- TASK_013 - 表决通过财产变价/分配方案
-- TASK_014 - 宣告重整与和解
-- TASK_015 - 审查宣告破产条件
-- TASK_016 - 裁定宣告债务人破产
-- TASK_017 - 拟定并执行财产变价方案
-- TASK_018 - 执行破产财产分配
-- TASK_019 - 破产费用与共益债务
-- TASK_020 - 提请终结破产程序
-- TASK_021 - 法院裁定并公告
-- TASK_022 - 办理企业注销登记
-- TASK_023 - 管理人终止执行职务并归档

-- 6.3 文件业务类型字典 (tb_file_record表的biz_type字段)
-- CASE_TASK - 案件任务

-- ========================================
-- 7. 性能优化建议
-- ========================================

-- 7.1 对于大案件量的场景，建议定期归档已完成案件的旧数据
-- 7.2 对于文件查询频繁的场景，可以考虑对tb_file_record表的biz_type和biz_id建立联合索引
-- 7.3 对于统计查询频繁的场景，可以考虑建立物化视图或定时任务预计算统计数据

-- ========================================
-- 8. 数据迁移说明
-- ========================================

-- 8.1 如果需要从旧的任务表迁移数据，可以编写迁移脚本
-- 8.2 迁移时需要注意保持任务编号的一致性
-- 8.3 迁移后需要验证数据的完整性和一致性

-- ========================================
-- 9. 备份与恢复
-- ========================================

-- 9.1 定期备份tb_case_task表数据
-- 9.2 备份时需要同时备份相关的tb_file_record表数据
-- 9.3 恢复时需要先恢复tb_bankrupt_case表，再恢复tb_case_task表，最后恢复tb_file_record表

-- ========================================
-- 10. 监控与维护
-- ========================================

-- 10.1 监控tb_case_task表的数据量增长情况
-- 10.2 监控慢查询日志，优化查询性能
-- 10.3 定期检查索引的使用情况，删除不必要的索引
-- 10.4 定期清理软删除的数据（is_deleted = 1）

-- ========================================
-- 恢复原始设置
-- ========================================

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ========================================
-- 脚本执行完成
-- ========================================
