-- =============================================
-- 审计日志历史数据哈希链迁移脚本
-- 创建时间: 2026-05-14
-- 说明: 为历史审计日志生成哈希链，确保数据完整性
-- =============================================

-- 1. 创建临时表用于存储迁移数据
DROP TEMPORARY TABLE IF EXISTS temp_audit_log_hash_chain;

CREATE TEMPORARY TABLE temp_audit_log_hash_chain AS
SELECT 
    id,
    CONCAT(
        COALESCE(user_account, ''),
        COALESCE(CAST(user_id AS CHAR), ''),
        COALESCE(module, ''),
        COALESCE(operation_type, ''),
        COALESCE(business_type, ''),
        COALESCE(CAST(business_id AS CHAR), ''),
        COALESCE(request_method, ''),
        COALESCE(request_url, ''),
        COALESCE(request_params, ''),
        COALESCE(data_before, ''),
        COALESCE(data_after, ''),
        COALESCE(status, ''),
        COALESCE(error_message, ''),
        COALESCE(ip_address, ''),
        COALESCE(CAST(create_time AS CHAR), ''),
        COALESCE(previous_hash, '')
    ) AS data_to_hash,
    @prev_hash:=CONCAT(SHA2(CONCAT(
        COALESCE(user_account, ''),
        COALESCE(CAST(user_id AS CHAR), ''),
        COALESCE(module, ''),
        COALESCE(operation_type, ''),
        COALESCE(business_type, ''),
        COALESCE(CAST(business_id AS CHAR), ''),
        COALESCE(request_method, ''),
        COALESCE(request_url, ''),
        COALESCE(request_params, ''),
        COALESCE(data_before, ''),
        COALESCE(data_after, ''),
        COALESCE(status, ''),
        COALESCE(error_message, ''),
        COALESCE(ip_address, ''),
        COALESCE(CAST(create_time AS CHAR), ''),
        COALESCE(@prev_hash, '0000000000000000000000000000000000000000000000000000000000000000')
    ), 256)) AS calculated_hash
FROM tb_audit_log
ORDER BY id ASC;

-- 2. 删除临时表（我们不需要存储）
DROP TEMPORARY TABLE IF EXISTS temp_audit_log_hash_chain;

-- =============================================
-- 3. 禁用触发器（允许更新所有字段）
-- =============================================

DROP TRIGGER IF EXISTS prevent_audit_log_delete;
DROP TRIGGER IF EXISTS prevent_audit_log_update;

-- =============================================
-- 4. 为每条历史记录生成哈希链
-- 注意：我们使用存储过程来逐条处理
-- =============================================

DELIMITER $$

DROP PROCEDURE IF EXISTS generate_audit_log_hash_chain$$

CREATE PROCEDURE generate_audit_log_hash_chain()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id BIGINT;
    DECLARE v_prev_id BIGINT;
    DECLARE v_user_account VARCHAR(100);
    DECLARE v_user_id BIGINT;
    DECLARE v_module VARCHAR(100);
    DECLARE v_operation_type VARCHAR(50);
    DECLARE v_business_type VARCHAR(100);
    DECLARE v_business_id BIGINT;
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
    DECLARE v_processed_count INT DEFAULT 0;
    DECLARE v_skip_count INT DEFAULT 0;
    
    DECLARE cur CURSOR FOR
        SELECT id, user_account, user_id, module, operation_type, business_type, business_id,
               request_method, request_url, request_params, data_before, data_after,
               status, error_message, ip_address, create_time
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
        FETCH cur INTO v_id, v_user_account, v_user_id, v_module, v_operation_type, 
                       v_business_type, v_business_id, v_request_method, v_request_url,
                       v_request_params, v_data_before, v_data_after, v_status,
                       v_error_message, v_ip_address, v_create_time;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 生成哈希值
        SET v_current_hash = SHA2(CONCAT(
            COALESCE(v_user_account, ''),
            COALESCE(CAST(v_user_id AS CHAR), ''),
            COALESCE(v_module, ''),
            COALESCE(v_operation_type, ''),
            COALESCE(v_business_type, ''),
            COALESCE(CAST(v_business_id AS CHAR), ''),
            COALESCE(v_request_method, ''),
            COALESCE(v_request_url, ''),
            COALESCE(v_request_params, ''),
            COALESCE(v_data_before, ''),
            COALESCE(v_data_after, ''),
            COALESCE(v_status, ''),
            COALESCE(v_error_message, ''),
            COALESCE(v_ip_address, ''),
            COALESCE(CAST(v_create_time AS CHAR), ''),
            v_prev_hash
        ), 256);
        
        -- 生成数字签名（简化版：使用哈希值+ID的组合）
        SET v_digital_signature = SHA2(CONCAT(v_current_hash, CAST(v_id AS CHAR), 'LAW_AUDIT_SIGNATURE_KEY_2024'), 256);
        
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
-- 5. 执行迁移
-- =============================================

CALL generate_audit_log_hash_chain();

-- =============================================
-- 6. 重新创建触发器
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
    -- 检查是否只修改了允许的字段
    IF OLD.hash_value <=> NEW.hash_value
       AND OLD.previous_hash <=> NEW.previous_hash
       AND OLD.digital_signature <=> NEW.digital_signature
       AND OLD.chain_sequence <=> NEW.chain_sequence
       AND OLD.id <=> NEW.id
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
        -- 只修改了允许的字段，允许通过
        SET NEW.integrity_status = NEW.integrity_status;
        SET NEW.signed_by = NEW.signed_by;
        SET NEW.signed_time = NEW.signed_time;
    ELSE
        -- 尝试修改关键字段，阻止操作
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '禁止修改审计日志：审计日志记录不可修改，以确保合规审查的完整性';
    END IF;
END$$

DELIMITER ;

-- =============================================
-- 7. 验证迁移结果
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

SELECT '审计日志历史数据哈希链迁移完成！' AS final_status;
