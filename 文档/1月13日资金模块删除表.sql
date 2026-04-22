-- 资金模块和资产模块数据库表删除语句
-- 创建日期: 2026-01-16
-- 版本: v1.0

-- 注意：删除表时需考虑外键依赖关系，按照依赖关系逆序删除

-- ========================================
-- 开始删除表
-- ========================================

-- 1. 删除分配明细表 (依赖分配执行表和提存管理表)
DROP TABLE IF EXISTS tb_distribution_detail;

-- 2. 删除提存管理表
DROP TABLE IF EXISTS tb_escrow_management;

-- 3. 删除分配执行表
DROP TABLE IF EXISTS tb_distribution_execution;

-- 4. 删除费用报销表
DROP TABLE IF EXISTS tb_fund_reimbursement;

-- 5. 删除资金预算表
DROP TABLE IF EXISTS tb_fund_budget;

-- 6. 删除共益债务管理表
DROP TABLE IF EXISTS tb_common_debt;

-- 7. 删除破产费用管理表
DROP TABLE IF EXISTS tb_bankruptcy_expense;

-- 8. 删除房产表 (依赖财产表)
DROP TABLE IF EXISTS tb_real_estate;

-- 9. 删除车辆表 (依赖财产表)
DROP TABLE IF EXISTS tb_vehicle;

-- 10. 删除设备表 (依赖财产表)
DROP TABLE IF EXISTS tb_equipment;

-- 11. 删除存货表 (依赖财产表)
DROP TABLE IF EXISTS tb_inventory;

-- 12. 删除知识产权表 (依赖财产表)
DROP TABLE IF EXISTS tb_intellectual_property;
    
-- 13. 删除财产表
DROP TABLE IF EXISTS tb_property;

-- ========================================
-- 删除完成
-- ========================================
-- 本SQL文件包含资金模块和资产模块的所有数据表删除语句
-- 资金模块包含：
--   1. tb_bankruptcy_expense - 破产费用管理表
--   2. tb_common_debt - 共益债务管理表
--   3. tb_distribution_execution - 分配执行表
--   4. tb_distribution_detail - 分配明细表
--   5. tb_escrow_management - 提存管理表
--   6. tb_fund_budget - 资金预算表
--   7. tb_fund_reimbursement - 费用报销表
-- 资产模块包含：
--   8. tb_property - 财产表
--   9. tb_real_estate - 房产表
--   10. tb_vehicle - 车辆表
--   11. tb_equipment - 设备表
--   12. tb_inventory - 存货表
--   13. tb_intellectual_property - 知识产权表
