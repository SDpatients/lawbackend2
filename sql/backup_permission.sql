-- ========================================
-- 数据库备份管理权限配置
-- 执行此脚本添加备份管理相关权限并授权
-- 可重复执行，不会产生重复数据
-- ========================================

-- ========================================
-- 1. 确保 system/SYSTEM 父权限节点存在
--    兼容数据库中已有的 SYSTEM(大写) 记录
--    若不存在则创建小写 system 记录
-- ========================================
INSERT IGNORE INTO `tb_permission` (`perm_code`, `perm_name`, `perm_type`, `parent_id`, `sort_order`, `status`, `is_external`, `is_deleted`, `create_time`, `update_time`)
SELECT 'system', '系统管理', '1', 0, 1, 'ACTIVE', '0', 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `tb_permission`
    WHERE (`perm_code` = 'system' OR `perm_code` = 'SYSTEM')
      AND `is_deleted` = 0
);

-- 获取 system 父权限的 ID（兼容大小写）
SET @system_parent_id = (
    SELECT `id` FROM `tb_permission`
    WHERE (`perm_code` = 'system' OR `perm_code` = 'SYSTEM')
      AND `is_deleted` = 0
    LIMIT 1
);

-- ========================================
-- 2. 插入备份管理相关的 4 个权限
-- ========================================
INSERT IGNORE INTO `tb_permission` (`perm_code`, `perm_name`, `perm_type`, `parent_id`, `sort_order`, `status`, `is_external`, `is_deleted`, `create_time`, `update_time`) VALUES
('system:backup:list',    '查看备份列表',    '2', @system_parent_id, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
('system:backup:execute', '执行备份',        '2', @system_parent_id, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
('system:backup:download', '下载备份',       '2', @system_parent_id, 3, 'ACTIVE', '0', 0, NOW(), NOW()),
('system:backup:delete',  '删除备份',        '2', @system_parent_id, 4, 'ACTIVE', '0', 0, NOW(), NOW());

-- ========================================
-- 3. 为超级管理员和管理员角色分配备份权限
--    - SUPER_ADMIN (超级管理员)
--    - ADMIN       (管理员)
-- ========================================
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`)
SELECT r.`id`, p.`id`, NOW(), 1
FROM `tb_role` r
CROSS JOIN `tb_permission` p
LEFT JOIN `tb_role_permission` rp ON rp.`role_id` = r.`id` AND rp.`perm_id` = p.`id`
WHERE r.`role_code` IN ('SUPER_ADMIN', 'ADMIN')
  AND r.`is_deleted` = 0
  AND p.`perm_code` LIKE 'system:backup:%'
  AND p.`is_deleted` = 0
  AND rp.`id` IS NULL;

-- ========================================
-- 4. 验证脚本
-- 执行后可运行以下 SELECT 验证结果：
-- ========================================
-- 查看备份权限条目
-- SELECT id, perm_code, perm_name, perm_type, parent_id, status FROM tb_permission WHERE perm_code LIKE 'system:backup:%';

-- 查看已授权的角色及权限
-- SELECT r.role_code, r.role_name, p.perm_code, p.perm_name
-- FROM tb_role_permission rp
-- JOIN tb_role r ON r.id = rp.role_id AND r.is_deleted = 0
-- JOIN tb_permission p ON p.id = rp.perm_id AND p.is_deleted = 0
-- WHERE p.perm_code LIKE 'system:backup:%'
-- ORDER BY r.role_code, p.perm_code;

-- ========================================
-- 权限码与 API 接口对照表
-- ========================================
-- | 权限码                  | 对应 API 接口                          | 说明             |
-- |------------------------|----------------------------------------|------------------|
-- | system:backup:list     | GET /system/backup/list                | 分页查询备份列表 |
-- | system:backup:list     | GET /system/backup/status              | 获取备份状态     |
-- | system:backup:list     | GET /system/backup/detail/{id}         | 查询备份详情     |
-- | system:backup:list     | GET /system/backup/statistics          | 获取备份统计     |
-- | system:backup:execute  | POST /system/backup/execute            | 手动执行备份     |
-- | system:backup:execute  | POST /system/backup/cleanup            | 清理过期备份     |
-- | system:backup:download | GET /system/backup/download/{id}       | 下载备份文件     |
-- | system:backup:delete   | DELETE /system/backup/{id}             | 删除备份记录     |