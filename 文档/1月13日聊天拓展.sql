-- 聊天功能拓展 - 数据库表结构更新
-- 创建时间: 2026-01-13
-- 功能: 消息搜索、消息引用/回复、消息转发、消息撤回配置

-- 1. 修改 tb_chat_message 表，添加消息引用相关字段
ALTER TABLE tb_chat_message
ADD COLUMN reply_to_message_id BIGINT COMMENT '回复的消息ID（引用消息）' AFTER content,
ADD COLUMN reply_to_content TEXT COMMENT '被回复消息的内容快照' AFTER reply_to_message_id,
ADD COLUMN reply_to_sender_id BIGINT COMMENT '被回复消息的发送者ID' AFTER reply_to_content,
ADD COLUMN reply_to_sender_name VARCHAR(100) COMMENT '被回复消息的发送者名称' AFTER reply_to_sender_id,
ADD COLUMN is_forwarded TINYINT(1) DEFAULT 0 COMMENT '是否为转发消息' AFTER is_recalled,
ADD COLUMN forwarded_from_message_id BIGINT COMMENT '转发源消息ID' AFTER is_forwarded,
ADD COLUMN forwarded_from_conversation_id BIGINT COMMENT '转发源会话ID' AFTER forwarded_from_message_id,
ADD COLUMN forwarded_from_sender_id BIGINT COMMENT '转发源消息发送者ID' AFTER forwarded_from_conversation_id,
ADD COLUMN forwarded_from_sender_name VARCHAR(100) COMMENT '转发源消息发送者名称' AFTER forwarded_from_sender_id,
ADD INDEX idx_reply_to_message_id (reply_to_message_id),
ADD INDEX idx_forwarded_from_message_id (forwarded_from_message_id),
ADD INDEX idx_is_forwarded (is_forwarded);

-- 2. 创建消息撤回配置表
CREATE TABLE IF NOT EXISTS tb_message_recall_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_type VARCHAR(50) NOT NULL COMMENT '配置类型: GLOBAL, USER, ROLE',
    target_id BIGINT COMMENT '目标ID（用户ID或角色ID，GLOBAL类型为null）',
    recall_time_limit INT DEFAULT 120 COMMENT '撤回时间限制（秒），默认120秒（2分钟）',
    allow_recall TINYINT(1) DEFAULT 1 COMMENT '是否允许撤回',
    max_recall_times INT DEFAULT 10 COMMENT '每日最大撤回次数',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '配置状态: ACTIVE, DISABLED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_config_type (config_type),
    INDEX idx_target_id (target_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息撤回配置表';

-- 3. 创建用户撤回记录表（用于统计每日撤回次数）
CREATE TABLE IF NOT EXISTS tb_user_recall_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    recall_date DATE NOT NULL COMMENT '撤回日期',
    recall_count INT DEFAULT 0 COMMENT '撤回次数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_date (user_id, recall_date),
    INDEX idx_user_id (user_id),
    INDEX idx_recall_date (recall_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户撤回记录表';

-- 4. 插入默认的全局撤回配置
INSERT INTO tb_message_recall_config (config_type, target_id, recall_time_limit, allow_recall, max_recall_times, status, remark)
VALUES ('GLOBAL', NULL, 120, 1, 50, 'ACTIVE', '全局默认撤回配置：2分钟内可撤回，每日最多50次')
ON DUPLICATE KEY UPDATE update_time = CURRENT_TIMESTAMP;

-- 5. 更新现有测试数据，添加一些回复和转发示例
-- 假设已有消息ID 1-10，我们添加一些回复和转发

-- 示例：消息11回复消息1
INSERT INTO tb_chat_message (conversation_id, sender_id, receiver_id, message_type, content, reply_to_message_id, reply_to_content, reply_to_sender_id, reply_to_sender_name, message_status, create_time)
VALUES (1, 2, 1, 'TEXT', '好的，我明白了', 1, '你好，请问案件进展如何？', 1, '张三', 'READ', '2026-01-13 09:00:00');

-- 示例：消息12回复消息2
INSERT INTO tb_chat_message (conversation_id, sender_id, receiver_id, message_type, content, reply_to_message_id, reply_to_content, reply_to_sender_id, reply_to_sender_name, message_status, create_time)
VALUES (1, 1, 2, 'TEXT', '收到，谢谢', 2, '案件正在审理中，预计下周会有结果', 2, '李四', 'READ', '2026-01-13 09:05:00');

-- 示例：消息13转发消息3
INSERT INTO tb_chat_message (conversation_id, sender_id, receiver_id, message_type, content, is_forwarded, forwarded_from_message_id, forwarded_from_conversation_id, forwarded_from_sender_id, forwarded_from_sender_name, message_status, create_time)
VALUES (2, 1, 3, 'TEXT', '请发送相关文件', 1, 3, 1, 1, '张三', 'DELIVERED', '2026-01-13 09:10:00');

-- 6. 查询验证
-- 查看表结构
-- DESC tb_chat_message;

-- 查看撤回配置
-- SELECT * FROM tb_message_recall_config;

-- 查看用户撤回记录
-- SELECT * FROM tb_user_recall_record;

-- 查询回复消息
-- SELECT m.*, r.content as reply_content, r.sender_id as reply_sender_id
-- FROM tb_chat_message m
-- LEFT JOIN tb_chat_message r ON m.reply_to_message_id = r.id
-- WHERE m.reply_to_message_id IS NOT NULL;

-- 查询转发消息
-- SELECT m.*, f.content as forward_content, f.sender_id as forward_sender_id
-- FROM tb_chat_message m
-- LEFT JOIN tb_chat_message f ON m.forwarded_from_message_id = f.id
-- WHERE m.is_forwarded = 1;

-- 7. 性能优化建议
-- 对于大量消息的搜索场景，建议添加全文索引
-- ALTER TABLE tb_chat_message ADD FULLTEXT INDEX ft_content (content);

-- 对于时间范围查询，确保索引有效
-- SHOW INDEX FROM tb_chat_message;
