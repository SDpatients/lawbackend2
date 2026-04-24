-- 安全删除 tb_token 表的 idx_token_value 索引（如果存在）
-- 使用存储过程来避免语法错误

CREATE PROCEDURE IF NOT EXISTS drop_token_index()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;
    ALTER TABLE tb_token DROP INDEX idx_token_value;
END;

CALL drop_token_index();
DROP PROCEDURE IF EXISTS drop_token_index;
