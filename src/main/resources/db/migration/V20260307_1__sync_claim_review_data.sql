-- 批量同步债权审查数据到债权确认表
-- 执行时间：2026-03-07
-- 用途：将已存在的 tb_claim_review 数据同步到 tb_claim_confirmation 表的新字段中

-- 同步最新一轮的审查数据到确认表
UPDATE tb_claim_confirmation cc 
INNER JOIN (
    SELECT 
        cr.claim_registration_id,
        cr.declared_principal,
        cr.declared_interest,
        cr.declared_penalty,
        cr.declared_other_losses,
        cr.declared_total_amount,
        cr.confirmed_principal,
        cr.confirmed_interest,
        cr.confirmed_penalty,
        cr.confirmed_other_losses,
        cr.confirmed_total_amount,
        cr.unconfirmed_principal,
        cr.unconfirmed_interest,
        cr.unconfirmed_penalty,
        cr.unconfirmed_other_losses,
        cr.unconfirmed_total_amount
    FROM tb_claim_review cr
    INNER JOIN (
        SELECT claim_registration_id, MAX(review_round) as max_round
        FROM tb_claim_review
        GROUP BY claim_registration_id
    ) latest ON cr.claim_registration_id = latest.claim_registration_id 
             AND cr.review_round = latest.max_round
    WHERE cr.is_deleted = false
) cr ON cc.claim_registration_id = cr.claim_registration_id
SET 
    cc.declared_principal = IFNULL(cc.declared_principal, cr.declared_principal),
    cc.declared_interest = IFNULL(cc.declared_interest, cr.declared_interest),
    cc.declared_penalty = IFNULL(cc.declared_penalty, cr.declared_penalty),
    cc.declared_other_losses = IFNULL(cc.declared_other_losses, cr.declared_other_losses),
    cc.declared_total_amount = IFNULL(cc.declared_total_amount, cr.declared_total_amount),
    cc.confirmed_principal = IFNULL(cc.confirmed_principal, cr.confirmed_principal),
    cc.confirmed_interest = IFNULL(cc.confirmed_interest, cr.confirmed_interest),
    cc.confirmed_penalty = IFNULL(cc.confirmed_penalty, cr.confirmed_penalty),
    cc.confirmed_other_losses = IFNULL(cc.confirmed_other_losses, cr.confirmed_other_losses),
    cc.confirmed_total_amount = IFNULL(cc.confirmed_total_amount, cr.confirmed_total_amount),
    cc.unconfirmed_principal = IFNULL(cc.unconfirmed_principal, cr.unconfirmed_principal),
    cc.unconfirmed_interest = IFNULL(cc.unconfirmed_interest, cr.unconfirmed_interest),
    cc.unconfirmed_penalty = IFNULL(cc.unconfirmed_penalty, cr.unconfirmed_penalty),
    cc.unconfirmed_other_losses = IFNULL(cc.unconfirmed_other_losses, cr.unconfirmed_other_losses),
    cc.unconfirmed_total_amount = IFNULL(cc.unconfirmed_total_amount, cr.unconfirmed_total_amount),
    cc.final_confirmed_amount = IFNULL(cc.final_confirmed_amount, cr.confirmed_total_amount)
WHERE 
    cc.is_deleted = false
    AND cc.declared_principal IS NULL;

-- 验证同步结果
SELECT 
    cc.id,
    cc.claim_registration_id,
    cc.creditor_name,
    cc.declared_principal,
    cc.confirmed_principal,
    cc.unconfirmed_principal,
    cc.final_confirmed_amount
FROM tb_claim_confirmation cc
WHERE cc.is_deleted = false
LIMIT 10;
