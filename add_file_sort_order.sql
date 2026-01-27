-- 为tb_file_record表添加sort_order字段
-- 执行日期: 2026-01-26
-- 说明: 添加文件排序字段，用于控制文件显示顺序

ALTER TABLE `tb_file_record` ADD COLUMN `sort_order` INT DEFAULT NULL COMMENT '排序序号，数字越小越靠前';

-- 为sort_order字段添加索引，提高查询性能
CREATE INDEX `idx_sort_order` ON `tb_file_record` (`sort_order`);
