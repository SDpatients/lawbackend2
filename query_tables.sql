-- 查询law数据库中M6和M7模块相关表的数据
-- 按create_time降序，返回前10条，并统计总记录数

USE law;

-- ============================================
-- M6工作管理模块
-- ============================================

-- 1. tb_work_team - 工作团队表
SELECT '=== tb_work_team (工作团队表) ===' AS '表信息';
SELECT id, team_name, create_user_id, create_time 
FROM tb_work_team 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_work_team WHERE is_deleted = 0;

-- 2. tb_work_team_member - 工作团队成员表
SELECT '=== tb_work_team_member (工作团队成员表) ===' AS '表信息';
SELECT id, team_id, user_id, create_time 
FROM tb_work_team_member 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_work_team_member WHERE is_deleted = 0;

-- 3. tb_work_team_permission - 工作团队权限表 (使用team_member_id而非team_id)
SELECT '=== tb_work_team_permission (工作团队权限表) ===' AS '表信息';
SELECT id, team_member_id, permission_type, create_time 
FROM tb_work_team_permission 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_work_team_permission WHERE is_deleted = 0;

-- 4. tb_work_plan - 工作计划表 (使用plan_number作为标题)
SELECT '=== tb_work_plan (工作计划表) ===' AS '表信息';
SELECT id, plan_number AS plan_title, create_user_id, create_time 
FROM tb_work_plan 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_work_plan WHERE is_deleted = 0;

-- 5. tb_work_log - 工作日志表 (使用work_content作为log_title)
SELECT '=== tb_work_log (工作日志表) ===' AS '表信息';
SELECT id, work_content AS log_title, create_user_id, create_time 
FROM tb_work_log 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_work_log WHERE is_deleted = 0;

-- ============================================
-- M7审批流程模块
-- ============================================

-- 6. tb_approval - 审批表
SELECT '=== tb_approval (审批表) ===' AS '表信息';
SELECT id, approval_title, create_user_id, create_time 
FROM tb_approval 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_approval WHERE is_deleted = 0;

-- 7. tb_approval_history - 审批历史表 (使用approval_status作为operation)
SELECT '=== tb_approval_history (审批历史表) ===' AS '表信息';
SELECT id, approval_id, approval_status AS operation, create_time 
FROM tb_approval_history 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_approval_history WHERE is_deleted = 0;

-- 8. tb_administrator - 管理员表 (使用administrator_name而非admin_name)
SELECT '=== tb_administrator (管理员表) ===' AS '表信息';
SELECT id, administrator_name AS admin_name, create_time 
FROM tb_administrator 
WHERE is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

SELECT COUNT(*) AS total_count FROM tb_administrator WHERE is_deleted = 0;
