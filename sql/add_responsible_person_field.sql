-- 为 tb_administrator 表添加 responsible_person 字段

USE law;

ALTER TABLE tb_administrator ADD COLUMN responsible_person VARCHAR(200) COMMENT '负责人' AFTER responsible_person_id;
