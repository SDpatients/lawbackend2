-- ========================================
-- SM4加密字段数据库迁移脚本
-- 将需要加密的VARCHAR字段长度扩展为255
-- 日期：2024-05-09
-- ========================================

ALTER TABLE tb_creditor_info MODIFY COLUMN contact_phone VARCHAR(255);
ALTER TABLE tb_creditor_info MODIFY COLUMN id_number VARCHAR(255);

ALTER TABLE tb_creditor_claim MODIFY COLUMN bank_account VARCHAR(500);
ALTER TABLE tb_creditor_claim MODIFY COLUMN agent_phone VARCHAR(255);
ALTER TABLE tb_creditor_claim MODIFY COLUMN agent_id_card VARCHAR(255);
ALTER TABLE tb_creditor_claim MODIFY COLUMN creditor_bank_account VARCHAR(500);

ALTER TABLE tb_claim_registration MODIFY COLUMN agent_phone VARCHAR(255);
ALTER TABLE tb_claim_registration MODIFY COLUMN agent_id_card VARCHAR(255);
ALTER TABLE tb_claim_registration MODIFY COLUMN creditor_bank_account VARCHAR(500);

ALTER TABLE tb_bank_account MODIFY COLUMN account_number VARCHAR(255);

ALTER TABLE tb_user MODIFY COLUMN mobile VARCHAR(255);
ALTER TABLE tb_user MODIFY COLUMN phone VARCHAR(255);

ALTER TABLE tb_fund_account MODIFY COLUMN bank_account VARCHAR(255);

ALTER TABLE tb_administrator_staff MODIFY COLUMN id_number VARCHAR(255);
ALTER TABLE tb_administrator_staff MODIFY COLUMN contact_phone VARCHAR(255);