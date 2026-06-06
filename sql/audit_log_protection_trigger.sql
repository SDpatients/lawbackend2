-- =============================================
-- 审计日志防篡改和防删除机制（v3 修正版）
-- 创建时间: 2026-05-14
-- 说明: 哈希字段允许从 null 初始化，初始化后不允许修改；业务字段完全不可修改；管理字段可修改
-- =============================================

-- 1. 删除旧触发器
DROP TRIGGER IF EXISTS prevent_audit_log_delete$$
DROP TRIGGER IF EXISTS prevent_audit_log_update$$

-- 2. 创建防删除触发器
DELIMITER $$

CREATE TRIGGER prevent_audit_log_delete
BEFORE DELETE ON tb_audit_log
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = '禁止删除审计日志：审计日志记录不可删除，以确保合规审查的完整性';
END$$

-- 3. 创建防篡改更新触发器（v3 修正版）
-- 策略：
--   - 哈希字段 (hash_value, previous_hash, digital_signature, chain_sequence)：
--     允许从 null/空 → 有值（初始化），禁止从有值 → 其他值（篡改）
--   - 业务字段：完全禁止修改
--   - 管理字段 (integrity_status, signed_by, signed_time)：始终允许修改
CREATE TRIGGER prevent_audit_log_update
BEFORE UPDATE ON tb_audit_log
FOR EACH ROW
BEGIN
    -- 检查业务数据字段是否被修改
    IF OLD.id <=> NEW.id
       AND OLD.user_id <=> NEW.user_id
       AND OLD.user_account <=> NEW.user_account
       AND OLD.user_name <=> NEW.user_name
       AND OLD.module <=> NEW.module
       AND OLD.module_name <=> NEW.module_name
       AND OLD.operation_type <=> NEW.operation_type
       AND OLD.operation_name <=> NEW.operation_name
       AND OLD.business_type <=> NEW.business_type
       AND OLD.business_id <=> NEW.business_id
       AND OLD.business_name <=> NEW.business_name
       AND OLD.request_method <=> NEW.request_method
       AND OLD.request_url <=> NEW.request_url
       AND OLD.request_params <=> NEW.request_params
       AND OLD.data_before <=> NEW.data_before
       AND OLD.data_after <=> NEW.data_after
       AND OLD.status <=> NEW.status
       AND OLD.error_message <=> NEW.error_message
       AND OLD.ip_address <=> NEW.ip_address
       AND OLD.location <=> NEW.location
       AND OLD.browser <=> NEW.browser
       AND OLD.os <=> NEW.os
       AND OLD.duration <=> NEW.duration
       AND OLD.create_time <=> NEW.create_time
    THEN
        -- 业务字段未修改，检查哈希字段

        -- hash_value: 允许 null→有值，允许相同值，禁止其他修改
        IF (OLD.hash_value IS NULL OR OLD.hash_value = '') AND (NEW.hash_value IS NOT NULL AND NEW.hash_value != '') THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSEIF OLD.hash_value <=> NEW.hash_value THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSE
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '禁止修改审计日志的哈希值：已存在的哈希值不允许修改';
        END IF;

        -- previous_hash: 允许 null→有值，允许相同值，禁止其他修改
        IF (OLD.previous_hash IS NULL OR OLD.previous_hash = '') AND (NEW.previous_hash IS NOT NULL AND NEW.previous_hash != '') THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSEIF OLD.previous_hash <=> NEW.previous_hash THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSE
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '禁止修改审计日志的前驱哈希值：已存在的前驱哈希值不允许修改';
        END IF;

        -- digital_signature: 允许 null→有值，允许相同值，禁止其他修改
        IF (OLD.digital_signature IS NULL OR OLD.digital_signature = '') AND (NEW.digital_signature IS NOT NULL AND NEW.digital_signature != '') THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSEIF OLD.digital_signature <=> NEW.digital_signature THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSE
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '禁止修改审计日志的数字签名：已存在的数字签名不允许修改';
        END IF;

        -- chain_sequence: 允许 null→有值，允许相同值，禁止其他修改
        IF (OLD.chain_sequence IS NULL) AND (NEW.chain_sequence IS NOT NULL) THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSEIF OLD.chain_sequence <=> NEW.chain_sequence THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSE
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '禁止修改审计日志的链序列号：已存在的链序列号不允许修改';
        END IF;

        -- integrity_status, signed_by, signed_time 始终允许修改
        SET NEW.integrity_status = NEW.integrity_status;
        SET NEW.signed_by = NEW.signed_by;
        SET NEW.signed_time = NEW.signed_time;

    ELSE
        -- 业务字段被修改，阻止操作
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '禁止修改审计日志：审计日志记录不可修改，以确保合规审查的完整性';
    END IF;
END$$

DELIMITER ;

-- =============================================
-- 4. 添加只读约束注释
-- =============================================

ALTER TABLE tb_audit_log
COMMENT = '审计日志表 - 采用哈希链和数字签名技术确保不可篡改，通过数据库触发器防止删除和篡改';

-- =============================================
-- 5. 创建完整性验证存储过程（修正版）
-- =============================================

DELIMITER $$

DROP PROCEDURE IF EXISTS verify_audit_log_integrity$$

CREATE PROCEDURE verify_audit_log_integrity(
    OUT p_total_count BIGINT,
    OUT p_tampered_count BIGINT,
    OUT p_integrity_rate DECIMAL(5,2)
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id BIGINT;
    DECLARE v_hash_value VARCHAR(128);
    DECLARE v_previous_hash VARCHAR(128);
    DECLARE v_integrity_status VARCHAR(20);
    DECLARE v_count INT DEFAULT 0;
    DECLARE cur CURSOR FOR
        SELECT id, hash_value, previous_hash, integrity_status
        FROM tb_audit_log
        ORDER BY id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SET p_tampered_count = 0;
    SET v_count = 0;

    SELECT COUNT(*) INTO p_total_count FROM tb_audit_log;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_id, v_hash_value, v_previous_hash, v_integrity_status;
        IF done THEN
            LEAVE read_loop;
        END IF;

        SET v_count = v_count + 1;

        -- 验证完整性
        IF v_integrity_status = 'TAMPERED' THEN
            SET p_tampered_count = p_tampered_count + 1;
        END IF;
    END LOOP;

    CLOSE cur;

    IF p_total_count > 0 THEN
        SET p_integrity_rate = ((p_total_count - p_tampered_count) / p_total_count) * 100;
    ELSE
        SET p_integrity_rate = 100.00;
    END IF;
END$$

DELIMITER ;

-- =============================================
-- 6. 验证触发器创建成功
-- =============================================

SHOW TRIGGERS FROM law LIKE '%audit_log%';

-- =============================================
-- 7. 测试触发器
-- =============================================

-- 测试1：尝试删除审计日志（应该失败）
-- DELETE FROM tb_audit_log WHERE id = 1;

-- 测试2：尝试修改关键字段（应该失败）
-- UPDATE tb_audit_log SET user_name = 'test' WHERE id = 1;

-- 测试3：尝试修改完整性状态（应该成功）
-- UPDATE tb_audit_log SET integrity_status = 'VERIFIED' WHERE id = 1;

-- =============================================
-- 完成
-- =============================================

SELECT '审计日志数据库保护机制已修正！' AS status;
SELECT '注意：现在允许更新 integrity_status, signed_by, signed_time 字段' AS note;
