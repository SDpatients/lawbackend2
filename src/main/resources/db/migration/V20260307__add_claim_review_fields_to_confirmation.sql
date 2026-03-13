-- 为 tb_claim_confirmation 表添加债权审查相关金额字段
-- 执行时间：2026-03-07

-- 添加申报金额字段
ALTER TABLE tb_claim_confirmation 
ADD COLUMN declared_principal DECIMAL(18,2) COMMENT '申报本金',
ADD COLUMN declared_interest DECIMAL(18,2) COMMENT '申报利息',
ADD COLUMN declared_penalty DECIMAL(18,2) COMMENT '申报罚息',
ADD COLUMN declared_other_losses DECIMAL(18,2) COMMENT '申报其他损失',
ADD COLUMN declared_total_amount DECIMAL(18,2) COMMENT '申报总额';

-- 添加确认金额字段
ALTER TABLE tb_claim_confirmation 
ADD COLUMN confirmed_principal DECIMAL(18,2) COMMENT '确认本金',
ADD COLUMN confirmed_interest DECIMAL(18,2) COMMENT '确认利息',
ADD COLUMN confirmed_penalty DECIMAL(18,2) COMMENT '确认罚息',
ADD COLUMN confirmed_other_losses DECIMAL(18,2) COMMENT '确认其他损失',
ADD COLUMN confirmed_total_amount DECIMAL(18,2) COMMENT '确认总额';

-- 添加未确认金额字段
ALTER TABLE tb_claim_confirmation 
ADD COLUMN unconfirmed_principal DECIMAL(18,2) COMMENT '未确认本金',
ADD COLUMN unconfirmed_interest DECIMAL(18,2) COMMENT '未确认利息',
ADD COLUMN unconfirmed_penalty DECIMAL(18,2) COMMENT '未确认罚息',
ADD COLUMN unconfirmed_other_losses DECIMAL(18,2) COMMENT '未确认其他损失',
ADD COLUMN unconfirmed_total_amount DECIMAL(18,2) COMMENT '未确认总额';
