-- ============================================
-- 案件状态迁移SQL
-- 将旧的9个状态精简为新的5个状态
-- 
-- 旧状态 → 新状态映射：
--   PENDING      → PENDING      (待处理，保持不变)
--   ONGOING      → ONGOING      (进行中，保持不变)
--   IN_PROGRESS  → ONGOING      (进行中，合并)
--   APPROVED     → ONGOING      (进行中，合并)
--   AWAITING     → AWAITING     (报结中，保持不变)
--   COMPLETED    → COMPLETED    (已结案，保持不变)
--   CLOSED       → COMPLETED    (已结案，合并)
--   TERMINATED   → COMPLETED    (已结案，合并)
--   ARCHIVED     → ARCHIVED     (已归档，保持不变)
-- 
-- 执行前请备份数据库！
-- ============================================

-- 1. 查看当前状态分布（执行前检查）
SELECT 
    case_status AS '当前状态',
    COUNT(*) AS '数量'
FROM tb_bankrupt_case
GROUP BY case_status
ORDER BY case_status;

-- 2. 将 IN_PROGRESS 和 APPROVED 更新为 ONGOING
UPDATE tb_bankrupt_case 
SET case_status = 'ONGOING' 
WHERE case_status IN ('IN_PROGRESS', 'APPROVED');

-- 3. 将 CLOSED 和 TERMINATED 更新为 COMPLETED
UPDATE tb_bankrupt_case 
SET case_status = 'COMPLETED' 
WHERE case_status IN ('CLOSED', 'TERMINATED');

-- 4. 验证迁移结果（执行后检查）
SELECT 
    case_status AS '新状态',
    COUNT(*) AS '数量'
FROM tb_bankrupt_case
GROUP BY case_status
ORDER BY case_status;

-- 5. 检查是否有未知状态（应该为0）
SELECT 
    case_status AS '未知状态',
    COUNT(*) AS '数量'
FROM tb_bankrupt_case
WHERE case_status NOT IN ('PENDING', 'ONGOING', 'AWAITING', 'COMPLETED', 'ARCHIVED')
GROUP BY case_status;
