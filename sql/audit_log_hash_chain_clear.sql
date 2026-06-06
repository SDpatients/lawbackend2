-- =============================================
-- 清除审计日志哈希链数据并安装 v3 触发器
-- 创建时间: 2026-05-14
-- 说明: 清除所有哈希链相关字段，安装 v3 触发器，然后通过 Java API 重新生成
-- =============================================

-- =============================================
-- 1. 禁用旧触发器
-- =============================================

DROP TRIGGER IF EXISTS prevent_audit_log_delete;
DROP TRIGGER IF EXISTS prevent_audit_log_update;

-- =============================================
-- 2. 清除所有哈希链字段
-- =============================================

UPDATE tb_audit_log
SET hash_value = NULL,
    previous_hash = NULL,
    digital_signature = NULL,
    chain_sequence = NULL,
    integrity_status = 'PENDING',
    signed_by = NULL,
    signed_time = NULL;

SELECT CONCAT('已清除 ', ROW_COUNT(), ' 条审计日志的哈希链数据') AS result;

-- =============================================
-- 3. 安装 v3 触发器（允许 null→值 初始化）
-- =============================================

DELIMITER $$

CREATE TRIGGER prevent_audit_log_delete
BEFORE DELETE ON tb_audit_log
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止删除审计日志';
END$$

CREATE TRIGGER prevent_audit_log_update
BEFORE UPDATE ON tb_audit_log
FOR EACH ROW
BEGIN
    IF OLD.id <=> NEW.id
       AND OLD.user_id <=> NEW.user_id AND OLD.user_account <=> NEW.user_account AND OLD.user_name <=> NEW.user_name
       AND OLD.module <=> NEW.module AND OLD.module_name <=> NEW.module_name
       AND OLD.operation_type <=> NEW.operation_type AND OLD.operation_name <=> NEW.operation_name
       AND OLD.business_type <=> NEW.business_type AND OLD.business_id <=> NEW.business_id AND OLD.business_name <=> NEW.business_name
       AND OLD.request_method <=> NEW.request_method AND OLD.request_url <=> NEW.request_url AND OLD.request_params <=> NEW.request_params
       AND OLD.data_before <=> NEW.data_before AND OLD.data_after <=> NEW.data_after
       AND OLD.status <=> NEW.status AND OLD.error_message <=> NEW.error_message
       AND OLD.ip_address <=> NEW.ip_address AND OLD.location <=> NEW.location
       AND OLD.browser <=> NEW.browser AND OLD.os <=> NEW.os AND OLD.duration <=> NEW.duration
       AND OLD.create_time <=> NEW.create_time
    THEN
        IF (OLD.hash_value IS NULL OR OLD.hash_value = '') AND (NEW.hash_value IS NOT NULL AND NEW.hash_value != '') THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSEIF OLD.hash_value <=> NEW.hash_value THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改哈希值';
        END IF;

        IF (OLD.previous_hash IS NULL OR OLD.previous_hash = '') AND (NEW.previous_hash IS NOT NULL AND NEW.previous_hash != '') THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSEIF OLD.previous_hash <=> NEW.previous_hash THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改前驱哈希';
        END IF;

        IF (OLD.digital_signature IS NULL OR OLD.digital_signature = '') AND (NEW.digital_signature IS NOT NULL AND NEW.digital_signature != '') THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSEIF OLD.digital_signature <=> NEW.digital_signature THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改数字签名';
        END IF;

        IF (OLD.chain_sequence IS NULL) AND (NEW.chain_sequence IS NOT NULL) THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSEIF OLD.chain_sequence <=> NEW.chain_sequence THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改链序列号';
        END IF;

        SET NEW.integrity_status = NEW.integrity_status;
        SET NEW.signed_by = NEW.signed_by;
        SET NEW.signed_time = NEW.signed_time;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改审计日志';
    END IF;
END$$

DELIMITER ;

-- =============================================
-- 4. 验证结果
-- =============================================

SELECT
    COUNT(*) AS total_logs,
    SUM(CASE WHEN hash_value IS NULL THEN 1 ELSE 0 END) AS cleared_logs,
    SUM(CASE WHEN integrity_status = 'PENDING' THEN 1 ELSE 0 END) AS pending_logs
FROM tb_audit_log;

SELECT '哈希链数据已清除，v3 触发器已安装！' AS status;
SELECT '下一步：调用 Java API 重新生成哈希链' AS next_step;
SELECT 'POST http://localhost:5779/api/v1/system/audit-log/integrity/migrate' AS api_endpoint;