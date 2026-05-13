-- ========================================
-- SM4加密字段长度更新迁移脚本
-- 日期: 2026-05-11
-- 说明: 将所有加密字段长度扩展为255/500，以容纳SM4加密后的Base64字符串
-- ========================================

-- ========================================
-- 用户表
-- ========================================
ALTER TABLE tb_user MODIFY COLUMN mobile VARCHAR(255);
ALTER TABLE tb_user MODIFY COLUMN phone VARCHAR(255);

-- ========================================
-- 债权人信息表
-- ========================================
ALTER TABLE tb_creditor_info MODIFY COLUMN contact_phone VARCHAR(255);
ALTER TABLE tb_creditor_info MODIFY COLUMN id_number VARCHAR(255);

-- ========================================
-- 债权人债权表
-- ========================================
ALTER TABLE tb_creditor_claim MODIFY COLUMN bank_account VARCHAR(500);
ALTER TABLE tb_creditor_claim MODIFY COLUMN agent_phone VARCHAR(255);
ALTER TABLE tb_creditor_claim MODIFY COLUMN agent_id_card VARCHAR(255);
ALTER TABLE tb_creditor_claim MODIFY COLUMN creditor_bank_account VARCHAR(500);

-- ========================================
-- 债权登记表
-- ========================================
ALTER TABLE tb_claim_registration MODIFY COLUMN agent_phone VARCHAR(255);
ALTER TABLE tb_claim_registration MODIFY COLUMN agent_id_card VARCHAR(255);
ALTER TABLE tb_claim_registration MODIFY COLUMN creditor_bank_account VARCHAR(500);

-- ========================================
-- 银行账户表
-- ========================================
ALTER TABLE tb_bank_account MODIFY COLUMN account_number VARCHAR(255);

-- ========================================
-- 资金账户表
-- ========================================
ALTER TABLE tb_fund_account MODIFY COLUMN bank_account VARCHAR(255);

-- ========================================
-- 管理员员工表
-- ========================================
ALTER TABLE tb_administrator_staff MODIFY COLUMN id_number VARCHAR(255);
ALTER TABLE tb_administrator_staff MODIFY COLUMN contact_phone VARCHAR(255);

-- ========================================
-- 完成！
-- ========================================