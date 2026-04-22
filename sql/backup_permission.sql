-- 数据库备份管理权限配置
-- 执行此脚本添加备份管理相关权限

-- 插入备份管理权限
INSERT INTO tb_permission (perm_code, perm_name, perm_type, parent_id, sort_order, status, create_time, update_time) VALUES
('system:backup:list', '查看备份列表', '2', (SELECT id FROM (SELECT id FROM tb_permission WHERE perm_code = 'system') AS temp), 1, 'ACTIVE', NOW(), NOW()),
('system:backup:execute', '执行备份', '2', (SELECT id FROM (SELECT id FROM tb_permission WHERE perm_code = 'system') AS temp), 2, 'ACTIVE', NOW(), NOW()),
('system:backup:download', '下载备份', '2', (SELECT id FROM (SELECT id FROM tb_permission WHERE perm_code = 'system') AS temp), 3, 'ACTIVE', NOW(), NOW()),
('system:backup:delete', '删除备份', '2', (SELECT id FROM (SELECT id FROM tb_permission WHERE perm_code = 'system') AS temp), 4, 'ACTIVE', NOW(), NOW());

-- 为管理员角色分配备份管理权限（假设管理员角色ID为1）
-- INSERT INTO tb_role_permission (role_id, permission_id, create_time) 
-- SELECT 1, id, NOW() FROM tb_permission WHERE perm_code LIKE 'system:backup:%';
