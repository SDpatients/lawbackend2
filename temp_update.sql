ALTER TABLE `tb_expense_reimbursement_attachment` MODIFY COLUMN `file_type` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件类型';
DESCRIBE `tb_expense_reimbursement_attachment`;