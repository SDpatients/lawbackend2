-- =============================================
-- 审计日志历史数据哈希链迁移脚本（修正版）
-- 创建时间: 2026-05-14
-- 说明: 修正哈希计算逻辑，与 Java HashChainUtil.buildHashData 完全一致
-- =============================================

-- =============================================
-- 1. 禁用触发器（允许更新所有字段）
-- =============================================

DROP TRIGGER IF EXISTS prevent_audit_log_delete;
DROP TRIGGER IF EXISTS prevent_audit_log_update;

-- =============================================
-- 2. 创建存储过程生成哈希链
-- 与 Java HashChainUtil.buildHashData 逻辑完全一致
-- =============================================

DELIMITER $$

DROP PROCEDURE IF EXISTS generate_audit_log_hash_chain$$

CREATE PROCEDURE generate_audit_log_hash_chain()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id BIGINT;
    DECLARE v_prev_id BIGINT;
    DECLARE v_user_id BIGINT;
    DECLARE v_user_account VARCHAR(100);
    DECLARE v_user_name VARCHAR(100);
    DECLARE v_module VARCHAR(100);
    DECLARE v_module_name VARCHAR(200);
    DECLARE v_operation_type VARCHAR(50);
    DECLARE v_operation_name VARCHAR(200);
    DECLARE v_business_type VARCHAR(100);
    DECLARE v_business_id BIGINT;
    DECLARE v_business_name VARCHAR(500);
    DECLARE v_request_method VARCHAR(10);
    DECLARE v_request_url VARCHAR(500);
    DECLARE v_request_params TEXT;
    DECLARE v_data_before LONGTEXT;
    DECLARE v_data_after LONGTEXT;
    DECLARE v_status VARCHAR(20);
    DECLARE v_error_message TEXT;
    DECLARE v_ip_address VARCHAR(50);
    DECLARE v_create_time DATETIME;
    DECLARE v_prev_hash VARCHAR(128);
    DECLARE v_current_hash VARCHAR(128);
    DECLARE v_digital_signature VARCHAR(256);
    DECLARE v_chain_sequence BIGINT;
    DECLARE v_data_to_hash TEXT;
    DECLARE v_processed_count INT DEFAULT 0;
    DECLARE v_skip_count INT DEFAULT 0;

    DECLARE cur CURSOR FOR
        SELECT id, user_id, user_account, user_name, module, module_name, operation_type, operation_name,
               business_type, business_id, business_name, request_method, request_url, request_params,
               data_before, data_after, status, error_message, ip_address, create_time
        FROM tb_audit_log
        WHERE hash_value IS NULL OR hash_value = ''
        ORDER BY id ASC;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 初始化创世哈希
    SET v_prev_hash = '0000000000000000000000000000000000000000000000000000000000000000';

    -- 统计需要处理的记录数
    SELECT COUNT(*) INTO v_processed_count FROM tb_audit_log WHERE hash_value IS NULL OR hash_value = '';

    SELECT CONCAT('需要处理的审计日志数量: ', v_processed_count) AS status;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_id, v_user_id, v_user_account, v_user_name, v_module, v_module_name,
                       v_operation_type, v_operation_name, v_business_type, v_business_id,
                       v_business_name, v_request_method, v_request_url, v_request_params,
                       v_data_before, v_data_after, v_status, v_error_message, v_ip_address, v_create_time;

        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 构建哈希数据（与 Java HashChainUtil.buildHashData 完全一致）
        -- 字段顺序: id|userId|userAccount|userName|module|moduleName|operationType|operationName|
        --          businessType|businessId|businessName|requestMethod|requestUrl|requestParams|
        --          dataBefore|dataAfter|status|errorMessage|ipAddress|createTime|previousHash
        SET v_data_to_hash = CONCAT(
            COALESCE(CAST(v_id AS CHAR), ''), '|',
            COALESCE(CAST(v_user_id AS CHAR), ''), '|',
            COALESCE(v_user_account, ''), '|',
            COALESCE(v_user_name, ''), '|',
            COALESCE(v_module, ''), '|',
            COALESCE(v_module_name, ''), '|',
            COALESCE(v_operation_type, ''), '|',
            COALESCE(v_operation_name, ''), '|',
            COALESCE(v_business_type, ''), '|',
            COALESCE(CAST(v_business_id AS CHAR), ''), '|',
            COALESCE(v_business_name, ''), '|',
            COALESCE(v_request_method, ''), '|',
            COALESCE(v_request_url, ''), '|',
            COALESCE(v_request_params, ''), '|',
            COALESCE(v_data_before, ''), '|',
            COALESCE(v_data_after, ''), '|',
            COALESCE(v_status, ''), '|',
            COALESCE(v_error_message, ''), '|',
            COALESCE(v_ip_address, ''), '|',
            COALESCE(DATE_FORMAT(v_create_time, '%Y-%m-%d %H:%i:%s'), ''), '|',
            COALESCE(v_prev_hash, '')
        );

        -- 生成 SHA-256 哈希值
        SET v_current_hash = SHA2(v_data_to_hash, 256);

        -- 生成数字签名（与 Java applyRSAWithPrivateKey 逻辑一致）
        SET v_digital_signature = SHA2(CONCAT(
            COALESCE(CAST(v_id AS CHAR), ''), '|',
            COALESCE(v_current_hash, ''), '|',
            COALESCE(CAST(v_user_id AS CHAR), ''), '|',
            COALESCE(v_operation_type, ''), '|',
            COALESCE(v_business_type, ''), '|',
            COALESCE(CAST(v_business_id AS CHAR), ''), '|',
            COALESCE(DATE_FORMAT(v_create_time, '%Y-%m-%d %H:%i:%s'), ''), '|',
            'lawbackend2-private-key'
        ), 256);

        -- 获取链序列号
        SELECT IFNULL(MAX(chain_sequence), 0) + 1 INTO v_chain_sequence
        FROM tb_audit_log
        WHERE chain_sequence IS NOT NULL;

        -- 更新记录
        UPDATE tb_audit_log
        SET hash_value = v_current_hash,
            previous_hash = v_prev_hash,
            digital_signature = v_digital_signature,
            chain_sequence = v_chain_sequence,
            integrity_status = 'VERIFIED'
        WHERE id = v_id;

        -- 更新前一条哈希值
        SET v_prev_hash = v_current_hash;

        -- 每100条输出一次进度
        SET v_skip_count = v_skip_count + 1;
        IF v_skip_count MOD 100 = 0 THEN
            SELECT CONCAT('已处理: ', v_skip_count, ' / ', v_processed_count) AS progress;
        END IF;

    END LOOP;

    CLOSE cur;

    SELECT CONCAT('迁移完成！共处理 ', v_skip_count, ' 条审计日志') AS result;
END$$

DELIMITER ;

-- =============================================
-- 3. 执行迁移
-- =============================================

CALL generate_audit_log_hash_chain();

-- =============================================
-- 4. 重新创建触发器（v3）
-- =============================================

DELIMITER $$

CREATE TRIGGER prevent_audit_log_delete
BEFORE DELETE ON tb_audit_log
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = '禁止删除审计日志：审计日志记录不可删除，以确保合规审查的完整性';
END$$

CREATE TRIGGER prevent_audit_log_update
BEFORE UPDATE ON tb_audit_log
FOR EACH ROW
BEGIN
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
        IF (OLD.hash_value IS NULL OR OLD.hash_value = '') AND (NEW.hash_value IS NOT NULL AND NEW.hash_value != '') THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSEIF OLD.hash_value <=> NEW.hash_value THEN
            SET NEW.hash_value = NEW.hash_value;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改审计日志的哈希值：已存在的哈希值不允许修改';
        END IF;

        IF (OLD.previous_hash IS NULL OR OLD.previous_hash = '') AND (NEW.previous_hash IS NOT NULL AND NEW.previous_hash != '') THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSEIF OLD.previous_hash <=> NEW.previous_hash THEN
            SET NEW.previous_hash = NEW.previous_hash;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改审计日志的前驱哈希值：已存在的前驱哈希值不允许修改';
        END IF;

        IF (OLD.digital_signature IS NULL OR OLD.digital_signature = '') AND (NEW.digital_signature IS NOT NULL AND NEW.digital_signature != '') THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSEIF OLD.digital_signature <=> NEW.digital_signature THEN
            SET NEW.digital_signature = NEW.digital_signature;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改审计日志的数字签名：已存在的数字签名不允许修改';
        END IF;

        IF (OLD.chain_sequence IS NULL) AND (NEW.chain_sequence IS NOT NULL) THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSEIF OLD.chain_sequence <=> NEW.chain_sequence THEN
            SET NEW.chain_sequence = NEW.chain_sequence;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '禁止修改审计日志的链序列号：已存在的链序列号不允许修改';
        END IF;

        SET NEW.integrity_status = NEW.integrity_status;
        SET NEW.signed_by = NEW.signed_by;
        SET NEW.signed_time = NEW.signed_time;
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '禁止修改审计日志：审计日志记录不可修改，以确保合规审查的完整性';
    END IF;
END$$

DELIMITER ;

-- =============================================
-- 5. 验证迁移结果
-- =============================================

SELECT '迁移完成！验证结果：' AS message;

SELECT
    COUNT(*) AS total_logs,
    SUM(CASE WHEN hash_value IS NOT NULL AND hash_value != '' THEN 1 ELSE 0 END) AS logs_with_hash,
    SUM(CASE WHEN integrity_status = 'VERIFIED' THEN 1 ELSE 0 END) AS verified_logs,
    SUM(CASE WHEN integrity_status = 'PENDING' THEN 1 ELSE 0 END) AS pending_logs,
    SUM(CASE WHEN integrity_status = 'TAMPERED' THEN 1 ELSE 0 END) AS tampered_logs
FROM tb_audit_log;

-- 显示前10条记录作为样例
SELECT
    id,
    LEFT(user_account, 20) AS user_account,
    module,
    operation_type,
    LEFT(request_url, 50) AS request_url,
    LEFT(hash_value, 20) AS hash_value,
    LEFT(previous_hash, 20) AS previous_hash,
    chain_sequence,
    integrity_status,
    create_time
FROM tb_audit_log
ORDER BY id ASC
LIMIT 10;

-- 验证哈希链连续性（检查前10条）
SELECT '验证哈希链连续性：' AS check_type;
SELECT
    a.id AS current_id,
    LEFT(a.hash_value, 20) AS current_hash,
    a.previous_hash AS stored_prev_hash,
    LEFT(b.hash_value, 20) AS expected_prev_hash,
    CASE WHEN a.previous_hash = b.hash_value OR (a.id = 1 AND a.previous_hash = '0000000000000000000000000000000000000000000000000000000000000000')
         THEN 'OK' ELSE 'BROKEN' END AS chain_status
FROM tb_audit_log a
LEFT JOIN tb_audit_log b ON a.id = b.id + 1
WHERE a.id <= 10
ORDER BY a.id ASC;

SELECT '审计日志历史数据哈希链迁移完成！' AS final_status;
SELECT '注意：如果 chain_status 显示 BROKEN，请检查数据是否被篡改' AS warning;
