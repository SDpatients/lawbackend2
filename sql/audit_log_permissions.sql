-- =============================================
-- 审计日志权限配置脚本
-- 创建时间: 2026-05-14
-- 说明: 为管理员角色添加审计日志相关权限
-- =============================================

-- 1. 添加审计日志权限（如果不存在）
INSERT IGNORE INTO `tb_permission` (`perm_code`, `perm_name`, `perm_type`, `parent_id`, `sort_order`, `status`, `is_external`, `is_deleted`, `create_time`, `update_time`)
SELECT 'system:audit:list', '查看审计日志', '2', id, 10, 'ACTIVE', '0', 0, NOW(), NOW()
FROM `tb_permission`
WHERE `perm_code` = 'system' AND `is_deleted` = 0
AND NOT EXISTS (SELECT 1 FROM `tb_permission` WHERE `perm_code` = 'system:audit:list' AND `is_deleted` = 0);

INSERT IGNORE INTO `tb_permission` (`perm_code`, `perm_name`, `perm_type`, `parent_id`, `sort_order`, `status`, `is_external`, `is_deleted`, `create_time`, `update_time`)
SELECT 'system:audit:integrity', '审计日志完整性验证', '2', id, 11, 'ACTIVE', '0', 0, NOW(), NOW()
FROM `tb_permission`
WHERE `perm_code` = 'system' AND `is_deleted` = 0
AND NOT EXISTS (SELECT 1 FROM `tb_permission` WHERE `perm_code` = 'system:audit:integrity' AND `is_deleted` = 0);

-- 2. 为管理员角色（ADMIN - role_id=2）分配审计日志权限
INSERT IGNORE INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`)
SELECT 2, p.id, NOW(), 1
FROM `tb_permission` p
WHERE p.`perm_code` IN ('system:audit:list', 'system:audit:integrity')
AND p.`is_deleted` = 0
AND NOT EXISTS (
    SELECT 1 FROM `tb_role_permission` rp
    WHERE rp.`role_id` = 2 AND rp.`perm_id` = p.id
);

-- 3. 为超级管理员角色（SUPER_ADMIN - role_id=1）也添加权限
INSERT IGNORE INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`)
SELECT 1, p.id, NOW(), 1
FROM `tb_permission` p
WHERE p.`perm_code` IN ('system:audit:list', 'system:audit:integrity')
AND p.`is_deleted` = 0
AND NOT EXISTS (
    SELECT 1 FROM `tb_role_permission` rp
    WHERE rp.`role_id` = 1 AND rp.`perm_id` = p.id
);

-- 4. 验证权限分配
SELECT
    r.role_code,
    r.role_name,
    p.perm_code,
    p.perm_name
FROM `tb_role_permission` rp
INNER JOIN `tb_role` r ON rp.role_id = r.id
INNER JOIN `tb_permission` p ON rp.perm_id = p.id
WHERE r.role_code IN ('SUPER_ADMIN', 'ADMIN')
AND p.perm_code LIKE 'system:audit:%'
ORDER BY r.role_code, p.perm_code;

-- 显示分配结果
SELECT '审计日志权限配置完成！' AS status;
SELECT COUNT(*) AS '已分配的审计日志权限数量'
FROM `tb_role_permission` rp
INNER JOIN `tb_permission` p ON rp.perm_id = p.id
WHERE p.perm_code LIKE 'system:audit:%';
