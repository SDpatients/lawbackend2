-- 增加 token_value 字段长度以支持包含权限信息的 JWT Token
-- 修改字段长度到 2000，确保足够存储长 JWT
ALTER TABLE tb_token MODIFY COLUMN token_value VARCHAR(2000) NOT NULL COMMENT 'JWT Token值';
