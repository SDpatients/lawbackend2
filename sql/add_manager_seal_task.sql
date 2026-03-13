-- ========================================
-- 为现有案件添加"管理人印章"任务
-- 创建日期: 2026-03-06
-- 说明: 在第二阶段添加 TASK_004 "管理人印章" 任务
-- ========================================

-- 1. 为现有案件插入"管理人印章"任务
-- 注意: 在"全面接管债务人"(TASK_003)之后、"调查财产及经营状况"(原TASK_004)之前插入
INSERT INTO tb_case_task (case_id, task_code, task_name, task_description, status, sort_order, create_time, is_deleted, status_audit)
SELECT 
    ct.case_id,
    'TASK_004' as task_code,
    '管理人印章' as task_name,
    '管理人' as task_description,
    'IN_PROGRESS' as status,
    4 as sort_order,
    NOW() as create_time,
    0 as is_deleted,
    'ACTIVE' as status_audit
FROM (SELECT DISTINCT case_id FROM tb_case_task WHERE is_deleted = 0) ct
WHERE NOT EXISTS (
    SELECT 1 FROM tb_case_task 
    WHERE case_id = ct.case_id 
    AND task_code = 'TASK_004' 
    AND is_deleted = 0
);

-- 2. 更新后续任务的 task_code 和 sort_order
-- TASK_004 -> TASK_005 (调查财产及经营状况)
UPDATE tb_case_task 
SET task_code = 'TASK_005', sort_order = 5 
WHERE task_code = 'TASK_004' AND task_name = '调查财产及经营状况' AND is_deleted = 0;

-- TASK_005 -> TASK_006 (决定合同继续履行或解除)
UPDATE tb_case_task 
SET task_code = 'TASK_006', sort_order = 6 
WHERE task_code = 'TASK_005' AND task_name = '决定合同继续履行或解除' AND is_deleted = 0;

-- TASK_006 -> TASK_007 (追收债务人财产)
UPDATE tb_case_task 
SET task_code = 'TASK_007', sort_order = 7 
WHERE task_code = 'TASK_006' AND task_name = '追收债务人财产' AND is_deleted = 0;

-- TASK_007 -> TASK_008 (通知已知债权人并公告)
UPDATE tb_case_task 
SET task_code = 'TASK_008', sort_order = 8 
WHERE task_code = 'TASK_007' AND task_name = '通知已知债权人并公告' AND is_deleted = 0;

-- TASK_008 -> TASK_009 (接收、登记债权申报)
UPDATE tb_case_task 
SET task_code = 'TASK_009', sort_order = 9 
WHERE task_code = 'TASK_008' AND task_name LIKE '接收%登记债权申报' AND is_deleted = 0;

-- TASK_009 -> TASK_010 (审查申报债权并编制债权表)
UPDATE tb_case_task 
SET task_code = 'TASK_010', sort_order = 10 
WHERE task_code = 'TASK_009' AND task_name LIKE '审查申报债权%' AND is_deleted = 0;

-- TASK_010 -> TASK_011 (筹备第一次债权人会议)
UPDATE tb_case_task 
SET task_code = 'TASK_011', sort_order = 11 
WHERE task_code = 'TASK_010' AND task_name LIKE '筹备第一次债权人会议%' AND is_deleted = 0;

-- TASK_011 -> TASK_012 (召开会议核查债权与议决事项)
UPDATE tb_case_task 
SET task_code = 'TASK_012', sort_order = 12 
WHERE task_code = 'TASK_011' AND task_name LIKE '召开会议核查%' AND is_deleted = 0;

-- TASK_012 -> TASK_013 (表决通过财产变价/分配方案)
UPDATE tb_case_task 
SET task_code = 'TASK_013', sort_order = 13 
WHERE task_code = 'TASK_012' AND task_name LIKE '表决通过财产变价%' AND is_deleted = 0;

-- TASK_013 -> TASK_014 (宣告重整与和解)
UPDATE tb_case_task 
SET task_code = 'TASK_014', sort_order = 14 
WHERE task_code = 'TASK_013' AND task_name = '宣告重整与和解' AND is_deleted = 0;

-- TASK_014 -> TASK_015 (审查宣告破产条件)
UPDATE tb_case_task 
SET task_code = 'TASK_015', sort_order = 15 
WHERE task_code = 'TASK_014' AND task_name = '审查宣告破产条件' AND is_deleted = 0;

-- TASK_015 -> TASK_016 (裁定宣告债务人破产)
UPDATE tb_case_task 
SET task_code = 'TASK_016', sort_order = 16 
WHERE task_code = 'TASK_015' AND task_name = '裁定宣告债务人破产' AND is_deleted = 0;

-- TASK_016 -> TASK_017 (拟定并执行财产变价方案)
UPDATE tb_case_task 
SET task_code = 'TASK_017', sort_order = 17 
WHERE task_code = 'TASK_016' AND task_name LIKE '拟定并执行财产变价%' AND is_deleted = 0;

-- TASK_017 -> TASK_018 (执行破产财产分配)
UPDATE tb_case_task 
SET task_code = 'TASK_018', sort_order = 18 
WHERE task_code = 'TASK_017' AND task_name = '执行破产财产分配' AND is_deleted = 0;

-- TASK_018 -> TASK_019 (破产费用与共益债务)
UPDATE tb_case_task 
SET task_code = 'TASK_019', sort_order = 19 
WHERE task_code = 'TASK_018' AND task_name = '破产费用与共益债务' AND is_deleted = 0;

-- TASK_019 -> TASK_020 (提请终结破产程序)
UPDATE tb_case_task 
SET task_code = 'TASK_020', sort_order = 20 
WHERE task_code = 'TASK_019' AND task_name = '提请终结破产程序' AND is_deleted = 0;

-- TASK_020 -> TASK_021 (法院裁定并公告)
UPDATE tb_case_task 
SET task_code = 'TASK_021', sort_order = 21 
WHERE task_code = 'TASK_020' AND task_name = '法院裁定并公告' AND is_deleted = 0;

-- TASK_021 -> TASK_022 (办理企业注销登记)
UPDATE tb_case_task 
SET task_code = 'TASK_022', sort_order = 22 
WHERE task_code = 'TASK_021' AND task_name = '办理企业注销登记' AND is_deleted = 0;

-- TASK_022 -> TASK_023 (管理人终止执行职务并归档)
UPDATE tb_case_task 
SET task_code = 'TASK_023', sort_order = 23 
WHERE task_code = 'TASK_022' AND task_name LIKE '管理人终止执行职务%' AND is_deleted = 0;

-- ========================================
-- 验证脚本
-- ========================================
-- 查看某个案件的任务列表（按sort_order排序）
-- SELECT case_id, task_code, task_name, sort_order FROM tb_case_task WHERE case_id = 1 AND is_deleted = 0 ORDER BY sort_order;

-- 查看每个案件的任务数量
-- SELECT case_id, COUNT(*) as task_count FROM tb_case_task WHERE is_deleted = 0 GROUP BY case_id;
