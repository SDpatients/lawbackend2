-- 修改审批管理表，新增审核标题和审核附件字段
ALTER TABLE `tb_approval` 
ADD COLUMN `approval_title` varchar(255) NULL COMMENT '审核标题' AFTER `approval_status`,
ADD COLUMN `approval_attachment` varchar(500) NULL COMMENT '审核附件' AFTER `approval_content`;

-- 修改审批历史表，新增审核标题和审核附件字段
ALTER TABLE `tb_approval_history` 
ADD COLUMN `approval_title` varchar(255) NULL COMMENT '审核标题' AFTER `approval_type`,
ADD COLUMN `approval_attachment` varchar(500) NULL COMMENT '审核附件' AFTER `approval_title`;