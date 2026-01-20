-- ========================================
-- 权限系统完整数据插入脚本
-- ========================================

-- ========================================
-- 1. 角色权限关联数据 (TB_ROLE_PERMISSION)
-- ========================================

-- 超级管理员 (SUPER_ADMIN - role_id=1) - 拥有所有权限
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`) VALUES
(1, 1, NOW(), 1), (1, 2, NOW(), 1), (1, 3, NOW(), 1), (1, 4, NOW(), 1), (1, 5, NOW(), 1),
(1, 6, NOW(), 1), (1, 7, NOW(), 1), (1, 8, NOW(), 1), (1, 9, NOW(), 1), (1, 10, NOW(), 1),
(1, 11, NOW(), 1), (1, 12, NOW(), 1), (1, 13, NOW(), 1), (1, 14, NOW(), 1), (1, 15, NOW(), 1),
(1, 16, NOW(), 1), (1, 17, NOW(), 1), (1, 18, NOW(), 1), (1, 19, NOW(), 1), (1, 24, NOW(), 1),
(1, 25, NOW(), 1), (1, 26, NOW(), 1), (1, 27, NOW(), 1), (1, 28, NOW(), 1), (1, 29, NOW(), 1),
(1, 30, NOW(), 1), (1, 31, NOW(), 1), (1, 32, NOW(), 1), (1, 33, NOW(), 1), (1, 34, NOW(), 1),
(1, 35, NOW(), 1), (1, 36, NOW(), 1), (1, 37, NOW(), 1), (1, 38, NOW(), 1), (1, 39, NOW(), 1),
(1, 40, NOW(), 1), (1, 41, NOW(), 1), (1, 42, NOW(), 1), (1, 43, NOW(), 1);

-- 管理员 (ADMIN - role_id=2) - 拥有大部分管理权限，包括查看全部案件
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`) VALUES
(2, 1, NOW(), 1), (2, 2, NOW(), 1), (2, 3, NOW(), 1), (2, 4, NOW(), 1), (2, 5, NOW(), 1),
(2, 6, NOW(), 1), (2, 7, NOW(), 1), (2, 8, NOW(), 1), (2, 9, NOW(), 1), (2, 10, NOW(), 1),
(2, 11, NOW(), 1), (2, 12, NOW(), 1), (2, 13, NOW(), 1), (2, 14, NOW(), 1), (2, 15, NOW(), 1),
(2, 16, NOW(), 1), (2, 17, NOW(), 1), (2, 18, NOW(), 1), (2, 19, NOW(), 1), (2, 24, NOW(), 1),
(2, 25, NOW(), 1), (2, 26, NOW(), 1), (2, 27, NOW(), 1), (2, 28, NOW(), 1), (2, 29, NOW(), 1),
(2, 30, NOW(), 1), (2, 31, NOW(), 1), (2, 32, NOW(), 1), (2, 33, NOW(), 1), (2, 34, NOW(), 1),
(2, 35, NOW(), 1), (2, 36, NOW(), 1), (2, 37, NOW(), 1), (2, 38, NOW(), 1), (2, 39, NOW(), 1),
(2, 40, NOW(), 1), (2, 41, NOW(), 1), (2, 42, NOW(), 1), (2, 43, NOW(), 1), (2, 44, NOW(), 1);

-- 律师 (LAWYER - role_id=3) - 可处理案件相关业务，但只能查看自己案件
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`) VALUES
(3, 2, NOW(), 1), (3, 10, NOW(), 1), (3, 11, NOW(), 1), (3, 12, NOW(), 1),
(3, 13, NOW(), 1), (3, 14, NOW(), 1), (3, 15, NOW(), 1), (3, 16, NOW(), 1),
(3, 17, NOW(), 1), (3, 18, NOW(), 1), (3, 19, NOW(), 1), (3, 24, NOW(), 1),
(3, 25, NOW(), 1), (3, 27, NOW(), 1), (3, 28, NOW(), 1), (3, 29, NOW(), 1), (3, 45, NOW(), 1);

-- 工作人员 (STAFF - role_id=4) - 可查看和编辑部分数据
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`) VALUES
(4, 2, NOW(), 1), (4, 10, NOW(), 1), (4, 11, NOW(), 1), (4, 12, NOW(), 1),
(4, 13, NOW(), 1), (4, 14, NOW(), 1), (4, 15, NOW(), 1), (4, 16, NOW(), 1),
(4, 17, NOW(), 1), (4, 18, NOW(), 1), (4, 19, NOW(), 1), (4, 27, NOW(), 1);

-- 访客 (GUEST - role_id=5) - 仅有查看权限
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`) VALUES
(5, 2, NOW(), 1), (5, 10, NOW(), 1), (5, 11, NOW(), 1), (5, 13, NOW(), 1),
(5, 15, NOW(), 1), (5, 16, NOW(), 1), (5, 18, NOW(), 1), (5, 27, NOW(), 1);

-- ========================================
-- 2. 用户角色关联数据 (TB_USER_ROLE)
-- ========================================

-- 用户1: admin - 超级管理员
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(1, 1, NOW(), 1);

-- 用户2: string - 律师
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(2, 3, NOW(), 1);

-- 用户3: lawyer02 - 律师
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(3, 3, NOW(), 1);

-- 用户4: staff01 - 工作人员
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(4, 4, NOW(), 1);

-- 用户5: staff02 - 工作人员
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(5, 4, NOW(), 1);

-- 用户6-10: 刘备、关羽、张飞、赵云、曹操 - 律师
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(6, 3, NOW(), 1), (7, 3, NOW(), 1), (8, 3, NOW(), 1), (9, 3, NOW(), 1), (10, 3, NOW(), 1);

-- 用户11-15: 司马懿、典韦、孙权、周瑜、陆逊 - 律师
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(11, 3, NOW(), 1), (12, 3, NOW(), 1), (13, 3, NOW(), 1), (14, 3, NOW(), 1), (15, 3, NOW(), 1);

-- 用户16: wanan - 管理员
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(16, 2, NOW(), 1);

-- 用户17: 123 - 访客
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(17, 5, NOW(), 1);

-- 用户18: ggggg - 访客
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_time`, `create_user_id`) VALUES
(18, 5, NOW(), 1);

-- ========================================
-- 3. 工作团队权限数据 (tb_work_team_permission)
-- ========================================

-- 团队成员3: team_id=2, case_id=2, user_id=3, 负责人, ADMIN权限
-- 拥有所有模块的完全管理权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
(3, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(3, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(3, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(3, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(3, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(3, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(3, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(3, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(3, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(3, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(3, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(3, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(3, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(3, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(3, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(3, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1);

-- 团队成员4: team_id=2, case_id=2, user_id=5, 成员, EDIT权限
-- 拥有查看和编辑权限，无删除和审批权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
(4, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(4, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(4, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(4, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(4, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(4, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(4, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(4, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(4, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(4, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(4, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(4, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(4, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(4, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(4, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(4, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1);

-- 团队成员5: team_id=3, case_id=3, user_id=2, 负责人, ADMIN权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
(5, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(5, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(5, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(5, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(5, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(5, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(5, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(5, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(5, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(5, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(5, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(5, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(5, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(5, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(5, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(5, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1);

-- 团队成员10: team_id=1, case_id=1, user_id=1, LEADER, EDIT权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
(10, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(10, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(10, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(10, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(10, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(10, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(10, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(10, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(10, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(10, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(10, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(10, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(10, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(10, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(10, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(10, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1);

-- 团队成员11: team_id=6, case_id=1, user_id=12, 负责人, ADMIN权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
(11, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(11, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(11, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(11, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(11, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(11, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(11, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(11, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(11, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(11, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(11, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(11, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(11, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(11, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(11, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(11, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1);

-- 团队成员15: team_id=6, case_id=1, user_id=4, LEADER, ADMIN权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
(15, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(15, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(15, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(15, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(15, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(15, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(15, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(15, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(15, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(15, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(15, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(15, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(15, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(15, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(15, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(15, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1);

-- 团队成员17-23: team_id=7, case_id=5, user_id=4,5,9,12,11,16,17, MEMBER, ADMIN权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
-- 成员17 (user_id=4)
(17, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(17, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(17, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(17, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(17, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(17, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(17, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(17, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(17, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(17, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(17, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(17, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(17, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(17, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(17, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(17, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1),
-- 成员18 (user_id=5)
(18, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(18, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(18, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(18, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(18, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(18, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(18, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(18, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(18, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(18, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(18, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(18, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(18, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(18, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(18, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(18, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1),
-- 成员19 (user_id=9)
(19, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(19, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(19, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(19, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(19, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(19, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(19, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(19, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(19, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(19, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(19, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(19, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(19, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(19, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(19, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(19, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1),
-- 成员20 (user_id=12)
(20, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(20, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(20, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(20, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(20, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(20, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(20, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(20, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(20, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(20, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(20, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(20, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(20, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(20, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(20, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(20, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1),
-- 成员21 (user_id=11)
(21, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(21, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(21, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(21, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(21, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(21, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(21, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(21, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(21, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(21, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(21, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(21, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(21, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(21, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(21, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(21, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1),
-- 成员22 (user_id=16)
(22, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(22, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(22, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(22, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(22, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(22, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(22, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(22, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(22, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(22, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(22, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(22, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(22, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(22, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(22, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(22, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1),
-- 成员23 (user_id=17)
(23, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(23, 'CASE', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(23, 'CASE', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(23, 'CASE', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(23, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(23, 'CREDITOR', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(23, 'CREDITOR', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(23, 'CREDITOR', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(23, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(23, 'FUND', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(23, 'FUND', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(23, 'FUND', 'APPROVE', 1, 'ACTIVE', NOW(), 1),
(23, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(23, 'ANNOUNCEMENT', 'EDIT', 1, 'ACTIVE', NOW(), 1),
(23, 'ANNOUNCEMENT', 'DELETE', 1, 'ACTIVE', NOW(), 1),
(23, 'ANNOUNCEMENT', 'PUBLISH', 1, 'ACTIVE', NOW(), 1);

-- 团队成员24-31: team_id=7, case_id=5, user_id=1,6,7,8,10,13,14,15, MEMBER, VIEW权限
INSERT INTO `tb_work_team_permission` (`team_member_id`, `module_type`, `permission_type`, `is_allowed`, `status`, `create_time`, `create_user_id`) VALUES
-- 成员24 (user_id=1) - VIEW权限
(24, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(24, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(24, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(24, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(24, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(24, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(24, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(24, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(24, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(24, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(24, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(24, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(24, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(24, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(24, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(24, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员25 (user_id=6) - VIEW权限
(25, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(25, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(25, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(25, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(25, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(25, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(25, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(25, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(25, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(25, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(25, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(25, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(25, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(25, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(25, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(25, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员26 (user_id=7) - VIEW权限
(26, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(26, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(26, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(26, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(26, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(26, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(26, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(26, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(26, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(26, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(26, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(26, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(26, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(26, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(26, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(26, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员27 (user_id=8) - VIEW权限
(27, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(27, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(27, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(27, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(27, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(27, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(27, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(27, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(27, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(27, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(27, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(27, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(27, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(27, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(27, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(27, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员28 (user_id=10) - VIEW权限
(28, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(28, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(28, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(28, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(28, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(28, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(28, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(28, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(28, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(28, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(28, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(28, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(28, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(28, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(28, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(28, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员29 (user_id=13) - VIEW权限
(29, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(29, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(29, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(29, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(29, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(29, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(29, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(29, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(29, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(29, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(29, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(29, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(29, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(29, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(29, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(29, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员30 (user_id=14) - VIEW权限
(30, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(30, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(30, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(30, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(30, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(30, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(30, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(30, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(30, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(30, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(30, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(30, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(30, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(30, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(30, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(30, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1),
-- 成员31 (user_id=15) - VIEW权限
(31, 'CASE', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(31, 'CASE', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(31, 'CASE', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(31, 'CASE', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(31, 'CREDITOR', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(31, 'CREDITOR', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(31, 'CREDITOR', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(31, 'CREDITOR', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(31, 'FUND', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(31, 'FUND', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(31, 'FUND', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(31, 'FUND', 'APPROVE', 0, 'ACTIVE', NOW(), 1),
(31, 'ANNOUNCEMENT', 'VIEW', 1, 'ACTIVE', NOW(), 1),
(31, 'ANNOUNCEMENT', 'EDIT', 0, 'ACTIVE', NOW(), 1),
(31, 'ANNOUNCEMENT', 'DELETE', 0, 'ACTIVE', NOW(), 1),
(31, 'ANNOUNCEMENT', 'PUBLISH', 0, 'ACTIVE', NOW(), 1);

-- ========================================
-- 权限数据插入完成
-- ========================================
