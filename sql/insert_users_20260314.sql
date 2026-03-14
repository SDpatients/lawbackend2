-- ========================================
-- 用户数据插入脚本
-- 创建时间: 2026-03-14
-- ========================================

-- ========================================
-- 1. 插入用户数据 (tb_user)
-- ========================================

-- 管理员: ADMIN
-- 用户名: ADMIN
-- 密码: cjtjxz666
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('ADMIN', '$2a$10$ayMd6/tMt9naMzryTf/4Oukt9r9pkY5skax16mULWv7cMhOkdZXxu', '管理员', NULL, '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());

-- 管理人: 李国祥
-- 用户名: 13306820330 (手机号)
-- 密码: 13306820330 (手机号)
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('13306820330', '$2a$10$0YRRlzcz6qXiJJKB9tPtHOBLUOku9pgH5JFyHH0IRgrzy45KNAbcG', '李国祥', '13306820330', '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());

-- 律师: 陈加文
-- 用户名: 13819267779 (手机号)
-- 密码: 13819267779 (默认密码为手机号)
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('13819267779', '$2a$10$xksO8XFK72nmzI4b7NJ7wOGJLdUC1pI9NWdxYpcL2WPUKhTSrbXoa', '陈加文', '13819267779', '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());

-- 律师: 陈良琼
-- 用户名: 13305823632 (手机号)
-- 密码: 13305823632 (默认密码为手机号)
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('13305823632', '$2a$10$YdB09e.MXr/6pH2cgsXERuF6iv27L8M7woPtL6ibClzhYR.Lvny76', '陈良琼', '13305823632', '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());

-- 律师: 金德龙
-- 用户名: 13857250610 (手机号)
-- 密码: 13857250610 (默认密码为手机号)
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('13857250610', '$2a$10$M0aH5i2F0q1s57JPGXCGPOctm1xNJhXJbOHBkwrUQO6wYONH35qgO', '金德龙', '13857250610', '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());

-- 律师: 陶江
-- 用户名: 19857275080 (手机号)
-- 密码: 19857275080 (默认密码为手机号)
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('19857275080', '$2a$10$qA1u.DOfWuJA8VJnQCxY7edN2oKn2hA9u8/YGePr7kEMRWHcM2wtO', '陶江', '19857275080', '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());

-- 律师: 李卓鑫
-- 用户名: 18157266287 (手机号)
-- 密码: 18157266287 (默认密码为手机号)
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `is_valid`, `status`, `login_type`, `login_count`, `pwd_error_count`, `is_deleted`, `create_time`, `update_time`) VALUES
('18157266287', '$2a$10$J5xrsYJDXGwwhLacl7m6zO2/VmHxyJics4hOFg7dSAJx6S11QMOr2', '李卓鑫', '18157266287', '1', 'ACTIVE', '1', 0, 0, 0, NOW(), NOW());


-- ========================================
-- 2. 分配用户角色 (tb_user_role)
-- ========================================
-- 角色说明:
-- role_id=1: SUPER_ADMIN (超级管理员)
-- role_id=2: ADMIN (管理员)
-- role_id=3: LAWYER (律师)
-- role_id=4: STAFF (工作人员)
-- role_id=5: GUEST (访客)

-- ADMIN - 管理员 (ADMIN)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 2, NOW(), 1 FROM `tb_user` WHERE `username` = 'ADMIN';

-- 李国祥 - 管理员 (ADMIN)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 2, NOW(), 1 FROM `tb_user` WHERE `mobile` = '13306820330';

-- 陈加文 - 律师 (LAWYER)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 3, NOW(), 1 FROM `tb_user` WHERE `mobile` = '13819267779';

-- 陈良琼 - 律师 (LAWYER)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 3, NOW(), 1 FROM `tb_user` WHERE `mobile` = '13305823632';

-- 金德龙 - 律师 (LAWYER)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 3, NOW(), 1 FROM `tb_user` WHERE `mobile` = '13857250610';

-- 陶江 - 律师 (LAWYER)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 3, NOW(), 1 FROM `tb_user` WHERE `mobile` = '19857275080';

-- 李卓鑫 - 律师 (LAWYER)
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`)
SELECT id, 3, NOW(), 1 FROM `tb_user` WHERE `mobile` = '18157266287';


-- ========================================
-- 用户数据插入完成
-- ========================================
-- 
-- 用户列表:
-- ┌──────────┬─────────────┬──────────────┬──────────────┐
-- │ 姓名     │ 用户名      │ 密码         │ 角色         │
-- ├──────────┼─────────────┼──────────────┼──────────────┤
-- │ 管理员   │ ADMIN       │ cjtjxz666    │ ADMIN        │
-- │ 李国祥   │ 13306820330 │ 13306820330  │ ADMIN        │
-- │ 陈加文   │ 13819267779 │ 13819267779  │ LAWYER       │
-- │ 陈良琼   │ 13305823632 │ 13305823632  │ LAWYER       │
-- │ 金德龙   │ 13857250610 │ 13857250610  │ LAWYER       │
-- │ 陶江     │ 19857275080 │ 19857275080  │ LAWYER       │
-- │ 李卓鑫   │ 18157266287 │ 18157266287  │ LAWYER       │
-- └──────────┴─────────────┴──────────────┴──────────────┘
--
-- 权限说明:
-- ADMIN (管理员): 拥有大部分管理权限，包括查看全部案件
-- LAWYER (律师): 可处理案件相关业务，但只能查看自己案件
-- ========================================
