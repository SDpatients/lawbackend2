-- 更新费用报销附件表的file_type字段长度
-- 创建日期: 2026-02-04
-- 说明: 将file_type字段长度从50增加到255，以支持更长的MIME类型

ALTER TABLE `tb_expense_reimbursement_attachment` 
MODIFY COLUMN `file_type` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件类型';

-- 验证更新结果
DESCRIBE `tb_expense_reimbursement_attachment`;