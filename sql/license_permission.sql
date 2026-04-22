-- 许可证管理权限配置
-- 执行此脚本添加许可证管理相关权限

-- 插入许可证管理权限
INSERT INTO tb_permission (perm_code, perm_name, perm_type, parent_id, sort_order, status, create_time, update_time) VALUES
('system:license:view', '查看许可证信息', '2', (SELECT id FROM (SELECT id FROM tb_permission WHERE perm_code = 'system') AS temp), 12, 'ACTIVE', NOW(), NOW()),
('system:license:manage', '管理许可证', '2', (SELECT id FROM (SELECT id FROM tb_permission WHERE perm_code = 'system') AS temp), 13, 'ACTIVE', NOW(), NOW());

-- 为管理员角色分配许可证管理权限（假设管理员角色ID为1）
-- INSERT INTO tb_role_permission (role_id, permission_id, create_time) 
-- SELECT 1, id, NOW() FROM tb_permission WHERE perm_code LIKE 'system:license:%';
