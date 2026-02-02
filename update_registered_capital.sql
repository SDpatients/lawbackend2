-- 修改tb_creditor_info表中的registered_capital字段类型
ALTER TABLE `tb_creditor_info` MODIFY COLUMN `registered_capital` decimal(22,4) DEFAULT NULL COMMENT '注册资本';
