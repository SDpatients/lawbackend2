-- ========================================
-- 系统基础配置数据初始化脚本
-- 创建时间: 2026-03-14
-- 用途: 新服务器数据库初始化
-- ========================================

-- ========================================
-- 1. 角色表 (tb_role)
-- ========================================
INSERT INTO `tb_role` (`id`, `role_code`, `role_name`, `role_desc`, `is_system`, `status`, `sort_order`, `is_deleted`, `create_time`, `update_time`) VALUES
(1, 'SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', '1', 'ACTIVE', 1, 0, NOW(), NOW()),
(2, 'ADMIN', '管理员', '系统管理员，拥有大部分管理权限', '1', 'ACTIVE', 2, 0, NOW(), NOW()),
(3, 'LAWYER', '律师', '律师角色，可处理案件相关业务', '1', 'ACTIVE', 3, 0, NOW(), NOW()),
(4, 'STAFF', '工作人员', '工作人员角色，可查看和编辑部分数据', '1', 'ACTIVE', 4, 0, NOW(), NOW()),
(5, 'GUEST', '访客', '访客角色，仅有查看权限', '1', 'ACTIVE', 5, 0, NOW(), NOW());

-- ========================================
-- 2. 权限表 (tb_permission)
-- ========================================
INSERT INTO `tb_permission` (`id`, `perm_code`, `perm_name`, `perm_type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `is_external`, `is_deleted`, `create_time`, `update_time`) VALUES
-- 系统管理
(1, 'SYSTEM', '系统管理', '1', 0, '/system', 'Layout', 'setting', 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(2, 'DASHBOARD', '工作台', '1', 0, '/dashboard', 'dashboard/index', 'dashboard', 2, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 用户管理
(3, 'USER_MANAGE', '用户管理', '2', 1, '/system/user', 'system/user/index', 'user', 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(4, 'USER_CREATE', '创建用户', '3', 3, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(5, 'USER_EDIT', '编辑用户', '3', 3, NULL, NULL, NULL, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(6, 'USER_DELETE', '删除用户', '3', 3, NULL, NULL, NULL, 3, 'ACTIVE', '0', 0, NOW(), NOW()),
(7, 'USER_VIEW', '查看用户', '3', 3, NULL, NULL, NULL, 4, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 角色管理
(8, 'ROLE_MANAGE', '角色管理', '2', 1, '/system/role', 'system/role/index', 'peoples', 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(9, 'ROLE_CREATE', '创建角色', '3', 8, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 权限管理
(10, 'PERMISSION_MANAGE', '权限管理', '2', 1, '/system/permission', 'system/permission/index', 'lock', 3, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 案件管理
(11, 'CASE_MANAGE', '案件管理', '1', 0, '/case', 'Layout', 'example', 3, 'ACTIVE', '0', 0, NOW(), NOW()),
(12, 'CASE_LIST', '案件列表', '2', 11, '/case/list', 'case/list/index', 'list', 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(13, 'CASE_CREATE', '创建案件', '3', 12, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(14, 'CASE_EDIT', '编辑案件', '3', 12, NULL, NULL, NULL, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(15, 'CASE_DELETE', '删除案件', '3', 12, NULL, NULL, NULL, 3, 'ACTIVE', '0', 0, NOW(), NOW()),
(16, 'CASE_VIEW', '查看案件', '3', 12, NULL, NULL, NULL, 4, 'ACTIVE', '0', 0, NOW(), NOW()),
(17, 'CASE_EXPORT', '导出案件', '3', 12, NULL, NULL, NULL, 5, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 债权人管理
(18, 'CREDITOR_MANAGE', '债权人管理', '2', 11, '/case/creditor', 'case/creditor/index', 'peoples', 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(19, 'CREDITOR_VIEW', '查看债权人', '3', 18, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 资金管理
(24, 'FUND_MANAGE', '资金管理', '1', 0, '/fund', 'Layout', 'money', 4, 'ACTIVE', '0', 0, NOW(), NOW()),
(25, 'FUND_ACCOUNT', '资金账户', '2', 24, '/fund/account', 'fund/account/index', 'money', 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(26, 'FUND_FLOW', '资金流水', '2', 24, '/fund/flow', 'fund/flow/index', 'money', 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(27, 'FUND_VIEW', '查看资金', '3', 24, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(28, 'FUND_EDIT', '编辑资金', '3', 24, NULL, NULL, NULL, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(29, 'FUND_DELETE', '删除资金', '3', 24, NULL, NULL, NULL, 3, 'ACTIVE', '0', 0, NOW(), NOW()),
(30, 'FUND_APPROVE', '审批资金', '3', 24, NULL, NULL, NULL, 4, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 公告管理
(31, 'ANNOUNCEMENT_MANAGE', '公告管理', '1', 0, '/announcement', 'Layout', 'message', 5, 'ACTIVE', '0', 0, NOW(), NOW()),
(32, 'ANNOUNCEMENT_LIST', '公告列表', '2', 31, '/announcement/list', 'announcement/list/index', 'message', 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(33, 'ANNOUNCEMENT_CREATE', '创建公告', '3', 32, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(34, 'ANNOUNCEMENT_EDIT', '编辑公告', '3', 32, NULL, NULL, NULL, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(35, 'ANNOUNCEMENT_DELETE', '删除公告', '3', 32, NULL, NULL, NULL, 3, 'ACTIVE', '0', 0, NOW(), NOW()),
(36, 'ANNOUNCEMENT_PUBLISH', '发布公告', '3', 32, NULL, NULL, NULL, 4, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 工作日志
(37, 'WORK_LOG', '工作日志', '2', 11, '/case/worklog', 'case/worklog/index', 'edit', 3, 'ACTIVE', '0', 0, NOW(), NOW()),
(38, 'WORK_LOG_CREATE', '创建日志', '3', 37, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(39, 'WORK_LOG_EDIT', '编辑日志', '3', 37, NULL, NULL, NULL, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(40, 'WORK_LOG_DELETE', '删除日志', '3', 37, NULL, NULL, NULL, 3, 'ACTIVE', '0', 0, NOW(), NOW()),
-- 文档管理
(41, 'DOCUMENT_MANAGE', '文档管理', '1', 0, '/document', 'Layout', 'documentation', 6, 'ACTIVE', '0', 0, NOW(), NOW()),
(42, 'DOCUMENT_LIBRARY', '文档库', '2', 41, '/document/library', 'document/library/index', 'documentation', 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(43, 'DOCUMENT_UPLOAD', '上传文档', '3', 42, NULL, NULL, NULL, 1, 'ACTIVE', '0', 0, NOW(), NOW()),
(44, 'DOCUMENT_DOWNLOAD', '下载文档', '3', 42, NULL, NULL, NULL, 2, 'ACTIVE', '0', 0, NOW(), NOW()),
(45, 'DOCUMENT_DELETE', '删除文档', '3', 42, NULL, NULL, NULL, 3, 'ACTIVE', '0', 0, NOW(), NOW());

-- ========================================
-- 3. 角色-权限关联表 (tb_role_permission)
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
(1, 40, NOW(), 1), (1, 41, NOW(), 1), (1, 42, NOW(), 1), (1, 43, NOW(), 1), (1, 44, NOW(), 1),
(1, 45, NOW(), 1);

-- 管理员 (ADMIN - role_id=2) - 拥有大部分管理权限，包括查看全部案件
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_time`, `create_user_id`) VALUES
(2, 1, NOW(), 1), (2, 2, NOW(), 1), (2, 3, NOW(), 1), (2, 4, NOW(), 1), (2, 5, NOW(), 1),
(2, 6, NOW(), 1), (2, 7, NOW(), 1), (2, 8, NOW(), 1), (2, 9, NOW(), 1), (2, 10, NOW(), 1),
(2, 11, NOW(), 1), (2, 12, NOW(), 1), (2, 13, NOW(), 1), (2, 14, NOW(), 1), (2, 15, NOW(), 1),
(2, 16, NOW(), 1), (2, 17, NOW(), 1), (2, 18, NOW(), 1), (2, 19, NOW(), 1), (2, 24, NOW(), 1),
(2, 25, NOW(), 1), (2, 26, NOW(), 1), (2, 27, NOW(), 1), (2, 28, NOW(), 1), (2, 29, NOW(), 1),
(2, 30, NOW(), 1), (2, 31, NOW(), 1), (2, 32, NOW(), 1), (2, 33, NOW(), 1), (2, 34, NOW(), 1),
(2, 35, NOW(), 1), (2, 36, NOW(), 1), (2, 37, NOW(), 1), (2, 38, NOW(), 1), (2, 39, NOW(), 1),
(2, 40, NOW(), 1), (2, 41, NOW(), 1), (2, 42, NOW(), 1), (2, 43, NOW(), 1), (2, 44, NOW(), 1),
(2, 45, NOW(), 1);

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
-- 4. 字典表 (tb_dict)
-- ========================================

-- 权限类型字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('PERMISSION_TYPE', 'VIEW', '查看', 'PERMISSION_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('PERMISSION_TYPE', 'EDIT', '编辑', 'PERMISSION_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('PERMISSION_TYPE', 'DELETE', '删除', 'PERMISSION_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('PERMISSION_TYPE', 'APPROVE', '审批', 'PERMISSION_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('PERMISSION_TYPE', 'PUBLISH', '发布', 'PERMISSION_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('PERMISSION_TYPE', 'ADMIN', '管理', 'PERMISSION_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1);

-- 角色字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('ROLE', 'SUPER_ADMIN', '超级管理员', 'ROLE', 'ACTIVE', NOW(), NOW(), 1, 1),
('ROLE', 'ADMIN', '管理员', 'ROLE', 'ACTIVE', NOW(), NOW(), 1, 1),
('ROLE', 'LAWYER', '律师', 'ROLE', 'ACTIVE', NOW(), NOW(), 1, 1),
('ROLE', 'STAFF', '工作人员', 'ROLE', 'ACTIVE', NOW(), NOW(), 1, 1),
('ROLE', 'GUEST', '访客', 'ROLE', 'ACTIVE', NOW(), NOW(), 1, 1);

-- 状态字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('STATUS', 'ACTIVE', '激活', 'STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('STATUS', 'INACTIVE', '停用', 'STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('STATUS', 'DELETED', '删除', 'STATUS', 'ACTIVE', NOW(), NOW(), 1, 1);

-- 通知公告状态字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('ANNOUNCEMENT_STATUS', 'DRAFT', '草稿', 'ANNOUNCEMENT_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('ANNOUNCEMENT_STATUS', 'PUBLISHED', '已发布', 'ANNOUNCEMENT_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('ANNOUNCEMENT_STATUS', 'EXPIRED', '已过期', 'ANNOUNCEMENT_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1);

-- 档案状态字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('ARCHIVE_STATUS', 'DRAFT', '草稿', 'ARCHIVE_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('ARCHIVE_STATUS', 'ACTIVE', '激活', 'ARCHIVE_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('ARCHIVE_STATUS', 'INACTIVE', '停用', 'ARCHIVE_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('ARCHIVE_STATUS', 'DELETED', '删除', 'ARCHIVE_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1);

-- 索引状态字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('INDEX_STATUS', 'NOT_INDEXED', '未索引', 'INDEX_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('INDEX_STATUS', 'INDEXED', '已索引', 'INDEX_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1),
('INDEX_STATUS', 'INDEX_FAILED', '索引失败', 'INDEX_STATUS', 'ACTIVE', NOW(), NOW(), 1, 1);

-- 文件类型字典
INSERT INTO `tb_dict` (`dict_code`, `dict_value`, `dict_label`, `dict_type`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
('FILE_TYPE', 'WORD', 'Word文档', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('FILE_TYPE', 'EXCEL', 'Excel文档', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('FILE_TYPE', 'PDF', 'PDF文档', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('FILE_TYPE', 'PPT', 'PowerPoint', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('FILE_TYPE', 'TXT', '纯文本', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('FILE_TYPE', 'ZIP', '压缩包', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1),
('FILE_TYPE', 'OTHER', '其他', 'FILE_TYPE', 'ACTIVE', NOW(), NOW(), 1, 1);

-- ========================================
-- 5. 档案分类表 (tb_archive_category)
-- ========================================
INSERT INTO `tb_archive_category` (`id`, `category_name`, `parent_id`, `path`, `category_level`, `is_public`, `create_time`, `update_time`, `create_user_id`, `update_user_id`, `is_deleted`, `status`) VALUES
(1, '公司文档', NULL, '/001', 1, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(2, '合同管理', 1, '/001/002', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(3, '制度文件', 1, '/001/003', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(4, '员工档案', 1, '/001/004', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(5, '项目文档', NULL, '/005', 1, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(6, '竞标文件', 5, '/005/006', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(7, '技术文档', 5, '/005/007', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(8, '财务文档', NULL, '/008', 1, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(9, '费用报销', 8, '/008/009', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(10, '发票归档', 8, '/008/010', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(11, '案件文档', NULL, '/011', 1, 0, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(12, '委托书', 11, '/011/012', 2, 0, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(13, '开庭记录', 11, '/011/013', 2, 0, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(14, '结案报告', 11, '/011/014', 2, 0, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(15, '法务通知', NULL, '/015', 1, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(16, '内部通知', 15, '/015/016', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(17, '系统公告', 15, '/015/017', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(18, '人事档案', 4, '/001/004/018', 3, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(19, '业务档案', 1, '/001/019', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(20, '法务档案', 1, '/001/020', 2, 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE');

-- ========================================
-- 6. 档案类型表 (tb_archive_type)
-- ========================================
INSERT INTO `tb_archive_type` (`id`, `category_id`, `type_name`, `description`, `is_default`, `create_time`, `update_time`, `create_user_id`, `update_user_id`, `is_deleted`, `status`) VALUES
(1, 2, '商业合同', '各类商务合作协议、销售合同、采购合同等', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(2, 2, '劳动合同', '公司与员工签订的劳动合同', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(3, 2, '劳务合同', '与外包人员/临时工签订的劳务合同', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(4, 2, '保密协议', '保密协议和竞业限制协议', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(5, 3, '规章制度', '公司内部制度、员工手册、人事制度等', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(6, 4, '个人档案', '员工个人人事档案', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(7, 4, '薪资档案', '员工薪资结构、历史调整记录等', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(8, 6, '标书文件', '各类竞标、投标用标书文件', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(9, 6, '竞标结果', '各项目竞标结果及说明', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(10, 7, '技术规范', '通用/专用技术规范文档', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(11, 7, '系统文档', '系统设计文档、操作手册等', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(12, 19, '法务咨询', '法务事务相关咨询记录', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(13, 20, '法律意见', '针对业务/项目提供的法律意见书', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(14, 20, '法务流程', '法务流程标准文件', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE');

-- ========================================
-- 7. 档案来源表 (tb_archive_source)
-- ========================================
INSERT INTO `tb_archive_source` (`id`, `source_name`, `source_code`, `type`, `description`, `create_time`, `update_time`, `create_user_id`, `update_user_id`, `is_deleted`, `status`) VALUES
(1, '合同系统', 'CONTRACT', 'SYSTEM', '从合同管理系统自动归档', NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(2, 'OA系统', 'OA', 'SYSTEM', '从OA系统同步的文件', NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(3, '项目管理', 'PROJECT', 'SYSTEM', '项目管理平台的文档', NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(4, '邮箱附件', 'EMAIL', 'MANUAL', '从邮箱中手动下载的附件', NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(5, 'U盘/移动硬盘', 'USB', 'MANUAL', '通过移动存储介质导入的文件', NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(6, '客户端上传', 'CLIENT', 'MANUAL', '通过客户端直接上传的文件', NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(7, '网页上传', 'WEB', 'MANUAL', '在网页界面手动上传的文件', NOW(), NOW(), 1, 1, 0, 'ACTIVE');

-- ========================================
-- 8. 档案模板配置表 (tb_archive_template)
-- ========================================
INSERT INTO `tb_archive_template` (`id`, `category_id`, `template_name`, `description`, `template_path`, `is_default`, `create_time`, `update_time`, `create_user_id`, `update_user_id`, `is_deleted`, `status`) VALUES
(1, 2, '销售合同模板', '标准销售合同模板，适用于各类销售业务', '/template/sales_contract.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(2, 2, '采购合同模板', '公司通用采购合同模板', '/template/purchase_contract.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(3, 19, '法务咨询记录模板', '法务咨询记录的标准格式', '/template/laefu_consultation.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(4, 19, '内部法律事务模板', '内部法务事务处理的模板', '/template/internal_law_case.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(5, 20, '法律意见书模板', '通用法律意见书模板', '/template/legal_opinion.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(6, 20, '法务流程说明模板', '法务流程的详细说明文档', '/template/law_process.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(7, 5, '项目标书模板', '标准项目标书模板', '/template/project_bid.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE'),
(8, 5, '项目立项报告模板', '项目立项阶段的报告模板', '/template/project_initiate.docx', 1, NOW(), NOW(), 1, 1, 0, 'ACTIVE');

-- ========================================
-- 9. 通知公告分类表 (tb_announcement_category)
-- ========================================
INSERT INTO `tb_announcement_category` (`id`, `category_name`, `parent_id`, `path`, `category_level`, `status`, `create_time`, `update_time`, `create_user_id`, `update_user_id`, `is_deleted`) VALUES
(1, '公司通知', NULL, '/001', 1, 'ACTIVE', NOW(), NOW(), 1, 1, 0),
(2, '系统公告', NULL, '/002', 1, 'ACTIVE', NOW(), NOW(), 1, 1, 0),
(3, '内部通知', 1, '/001/003', 2, 'ACTIVE', NOW(), NOW(), 1, 1, 0),
(4, '人事通知', 1, '/001/004', 2, 'ACTIVE', NOW(), NOW(), 1, 1, 0),
(5, '财务通知', 1, '/001/005', 2, 'ACTIVE', NOW(), NOW(), 1, 1, 0);

-- ========================================
-- 系统基础配置数据初始化完成
-- ========================================
