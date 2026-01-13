-- 实时聊天功能数据库表结构
-- 创建时间: 2026-01-12

-- 删除已存在的表（如果需要重新创建）
-- DROP TABLE IF EXISTS tb_chat_message;
-- DROP TABLE IF EXISTS tb_conversation;

-- 1. 聊天会话表
CREATE TABLE IF NOT EXISTS tb_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_id1 BIGINT NOT NULL COMMENT '用户1 ID',
    user_id2 BIGINT NOT NULL COMMENT '用户2 ID',
    last_message_id BIGINT COMMENT '最后一条消息ID',
    last_message_content TEXT COMMENT '最后一条消息内容预览',
    last_message_type VARCHAR(20) COMMENT '最后一条消息类型: TEXT, IMAGE, FILE, VOICE',
    last_message_time DATETIME COMMENT '最后消息时间',
    user1_unread_count INT DEFAULT 0 COMMENT '用户1未读消息数',
    user2_unread_count INT DEFAULT 0 COMMENT '用户2未读消息数',
    user1_deleted TINYINT(1) DEFAULT 0 COMMENT '用户1是否删除会话',
    user2_deleted TINYINT(1) DEFAULT 0 COMMENT '用户2是否删除会话',
    user1_pinned TINYINT(1) DEFAULT 0 COMMENT '用户1是否置顶',
    user2_pinned TINYINT(1) DEFAULT 0 COMMENT '用户2是否置顶',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '会话状态: ACTIVE, ARCHIVED, DELETED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_user_id1 (user_id1),
    INDEX idx_user_id2 (user_id2),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    UNIQUE KEY uk_conversation (user_id1, user_id2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 2. 聊天消息表
CREATE TABLE IF NOT EXISTS tb_chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型: TEXT, IMAGE, FILE, VOICE, VIDEO',
    content TEXT COMMENT '文本内容',
    file_id BIGINT COMMENT '文件ID (关联tb_file_record)',
    file_name VARCHAR(255) COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_url VARCHAR(500) COMMENT '文件URL',
    message_status VARCHAR(20) DEFAULT 'SENT' COMMENT '消息状态: SENT, DELIVERED, READ, FAILED',
    read_time DATETIME COMMENT '阅读时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    deleted_by BIGINT COMMENT '删除者ID',
    deleted_time DATETIME COMMENT '删除时间',
    is_recalled TINYINT(1) DEFAULT 0 COMMENT '是否撤回',
    recall_time DATETIME COMMENT '撤回时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_create_time (create_time),
    INDEX idx_message_status (message_status),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_is_recalled (is_recalled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 插入初始数据（假设已有用户表tb_user，这里使用示例用户ID）
-- 注意：实际使用时需要替换为真实的用户ID

-- 插入测试会话数据
INSERT INTO tb_conversation (user_id1, user_id2, last_message_content, last_message_type, last_message_time, user1_unread_count, user2_unread_count, status, remark) VALUES
(1, 2, '你好，请问案件进展如何？', 'TEXT', '2026-01-12 10:30:00', 0, 1, 'ACTIVE', '测试会话1'),
(1, 3, '文件已发送，请查收', 'FILE', '2026-01-12 11:15:00', 1, 0, 'ACTIVE', '测试会话2'),
(2, 3, '会议时间已确认', 'TEXT', '2026-01-12 09:45:00', 0, 0, 'ACTIVE', '测试会话3'),
(1, 4, '图片预览', 'IMAGE', '2026-01-12 14:20:00', 2, 0, 'ACTIVE', '测试会话4'),
(3, 4, '语音消息', 'VOICE', '2026-01-12 15:30:00', 0, 1, 'ACTIVE', '测试会话5');

-- 插入测试消息数据
INSERT INTO tb_chat_message (conversation_id, sender_id, receiver_id, message_type, content, message_status, create_time) VALUES
-- 会话1的消息 (用户1和用户2)
(1, 1, 2, 'TEXT', '你好，请问案件进展如何？', 'READ', '2026-01-12 10:30:00'),
(1, 2, 1, 'TEXT', '案件正在审理中，预计下周会有结果', 'READ', '2026-01-12 10:32:00'),
(1, 1, 2, 'TEXT', '好的，有消息请及时通知我', 'READ', '2026-01-12 10:35:00'),

-- 会话2的消息 (用户1和用户3)
(2, 1, 3, 'TEXT', '请发送相关文件', 'READ', '2026-01-12 11:10:00'),
(2, 3, 1, 'FILE', '文件已发送，请查收', 'DELIVERED', '2026-01-12 11:15:00'),

-- 会话3的消息 (用户2和用户3)
(3, 2, 3, 'TEXT', '会议时间确定了吗？', 'READ', '2026-01-12 09:40:00'),
(3, 3, 2, 'TEXT', '会议时间已确认，明天下午3点', 'READ', '2026-01-12 09:45:00'),

-- 会话4的消息 (用户1和用户4)
(4, 1, 4, 'IMAGE', '图片预览', 'DELIVERED', '2026-01-12 14:20:00'),
(4, 1, 4, 'TEXT', '这是案件现场照片', 'DELIVERED', '2026-01-12 14:21:00'),

-- 会话5的消息 (用户3和用户4)
(5, 3, 4, 'VOICE', '语音消息', 'SENT', '2026-01-12 15:30:00');

-- 更新会话的最后消息ID
UPDATE tb_conversation SET last_message_id = 3 WHERE id = 1;
UPDATE tb_conversation SET last_message_id = 5 WHERE id = 2;
UPDATE tb_conversation SET last_message_id = 7 WHERE id = 3;
UPDATE tb_conversation SET last_message_id = 9 WHERE id = 4;
UPDATE tb_conversation SET last_message_id = 10 WHERE id = 5;

-- 查询验证
-- SELECT * FROM tb_conversation ORDER BY last_message_time DESC;
-- SELECT * FROM tb_chat_message ORDER BY create_time DESC;

-- 统计查询示例
-- 查询某用户的所有会话
-- SELECT c.*, 
--        u1.username as user1_name,
--        u2.username as user2_name
-- FROM tb_conversation c
-- LEFT JOIN tb_user u1 ON c.user_id1 = u1.id
-- LEFT JOIN tb_user u2 ON c.user_id2 = u2.id
-- WHERE (c.user_id1 = 1 OR c.user_id2 = 1)
--   AND c.user1_deleted = 0 AND c.user2_deleted = 0
--   AND c.status = 'ACTIVE'
-- ORDER BY c.last_message_time DESC;

-- 查询某会话的消息列表
-- SELECT m.*, 
--        s.username as sender_name,
--        r.username as receiver_name
-- FROM tb_chat_message m
-- LEFT JOIN tb_user s ON m.sender_id = s.id
-- LEFT JOIN tb_user r ON m.receiver_id = r.id
-- WHERE m.conversation_id = 1
--   AND m.is_deleted = 0
-- ORDER BY m.create_time DESC
-- LIMIT 20;

-- 统计未读消息数
-- SELECT SUM(CASE WHEN user_id1 = 1 THEN user1_unread_count ELSE user2_unread_count END) as total_unread
-- FROM tb_conversation
-- WHERE (user_id1 = 1 OR user_id2 = 1)
--   AND status = 'ACTIVE';
