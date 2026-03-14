-- 修改 approval_attachment 字段类型为 TEXT，解决数据过长问题
-- 执行时间: 2026-03-14

-- 修改 tb_approval 表
ALTER TABLE tb_approval MODIFY COLUMN approval_attachment TEXT;

-- 修改 tb_approval_history 表
ALTER TABLE tb_approval_history MODIFY COLUMN approval_attachment TEXT;
