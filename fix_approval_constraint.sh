mysql -u root -p123456 law << 'EOF'
-- 删除审批表的唯一约束，改为普通索引
ALTER TABLE `tb_approval` DROP INDEX `uk_case_type`;

-- 添加普通索引
ALTER TABLE `tb_approval` ADD INDEX `idx_case_type` (`case_id`, `approval_type`);
EOF