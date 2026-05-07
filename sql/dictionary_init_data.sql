-- ========================================
-- 字典分类和字典项初始化数据
-- 创建时间: 2026-05-05
-- 用途: 初始化 tb_dictionary_category 和 tb_dictionary_item 表
-- 数据来源: 项目枚举类 + tb_dict 原有数据 + 硬编码状态值
-- ========================================

-- ========================================
-- 1. 字典分类 (tb_dictionary_category)
-- ========================================
INSERT INTO `tb_dictionary_category` (`id`, `category_code`, `category_name`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(1,  'CASE_STATUS',              '案件状态',       '破产案件的状态流转',                   1,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2,  'CASE_PROGRESS',            '案件进度',       '破产案件的阶段进度',                   2,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(3,  'CASE_STAGE',               '案件阶段',       '破产案件的程序阶段',                   3,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(4,  'CASE_TYPE',                '案件类型',       '破产案件的类型分类',                   4,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(5,  'CREDITOR_STATUS',          '债权人状态',     '债权人的确认状态',                     5,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(6,  'REVIEW_STATUS',            '审核状态',       '通用审核流程状态',                     6,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(7,  'REGISTRATION_STATUS',      '登记状态',       '债权登记的状态',                       7,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(8,  'CASE_TASK_STATUS',         '案件任务状态',   '案件任务的状态流转',                   8,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(9,  'SUBMISSION_STATUS',        '提交状态',       '任务提交的审核状态',                   9,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
(10, 'SUBMISSION_TYPE',          '提交类型',       '任务提交的类型',                       10, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(11, 'ALERT_LEVEL',              '预警级别',       '案件节点预警级别',                     11, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(12, 'NODE_STATUS',              '节点状态',       '案件流程节点的状态',                   12, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(13, 'NOTIFICATION_TYPE',        '通知类型',       '系统通知的发送渠道',                   13, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(14, 'RESPONSIBLE_ROLE',         '责任角色',       '案件节点负责角色',                     14, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(15, 'CALCULATION_BASE',         '计算基准',       '期限计算的基准日期类型',               15, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(16, 'EXTENSION_APPROVAL_STATUS','延期审批状态',   '节点延期申请的审批状态',               16, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(17, 'PERMISSION_TYPE',          '权限类型',       '系统权限操作类型',                     17, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(18, 'ROLE_TYPE',                '角色类型',       '系统用户角色分类',                     18, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(19, 'COMMON_STATUS',            '通用状态',       '系统通用的启用/停用状态',              19, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(20, 'ANNOUNCEMENT_STATUS',      '公告状态',       '通知公告的发布状态',                   20, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(21, 'ARCHIVE_STATUS',           '档案状态',       '档案记录的状态',                       21, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(22, 'INDEX_STATUS',             '索引状态',       '文档索引的处理状态',                   22, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'FILE_TYPE',                '文件类型',       '上传文件的格式分类',                   23, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(24, 'ACCESS_LEVEL',             '访问级别',       '档案文件的访问权限级别',               24, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(25, 'CREDITOR_TYPE',            '债权人类型',     '债权人的主体类型',                     25, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(26, 'CLAIM_NATURE',             '债权性质',       '债权的法律性质分类',                   26, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(27, 'FUND_FLOW_TYPE',           '资金流水类型',   '资金收支的类型',                       27, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'EXPENSE_TYPE',             '费用类型',       '破产费用的类型分类',                   28, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(29, 'APPROVAL_STATUS',          '审批状态',       '通用审批流程状态',                     29, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(30, 'WORK_TEAM_MODULE',         '工作团队模块',   '工作团队权限模块',                     30, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- ========================================
-- 2. 字典项 (tb_dictionary_item)
-- ========================================

-- (1) 案件状态 - 来源: CaseStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(1, 'PENDING',   '待处理', 'PENDING',   '案件已创建，等待处理',   1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(1, 'ONGOING',   '进行中', 'ONGOING',   '案件正在处理中',         2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(1, 'AWAITING',  '报结中', 'AWAITING',  '案件正在报结审核',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(1, 'COMPLETED', '已结案', 'COMPLETED', '案件已结案',             4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(1, 'ARCHIVED',  '已归档', 'ARCHIVED',  '案件已归档',             5, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (2) 案件进度 - 来源: CaseProgress枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(2, 'FIRST',   '第一阶段', 'FIRST',   '破产案件第一阶段',   1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2, 'SECOND',  '第二阶段', 'SECOND',  '破产案件第二阶段',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2, 'THIRD',   '第三阶段', 'THIRD',   '破产案件第三阶段',   3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2, 'FOURTH',  '第四阶段', 'FOURTH',  '破产案件第四阶段',   4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2, 'FIFTH',   '第五阶段', 'FIFTH',   '破产案件第五阶段',   5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2, 'SIXTH',   '第六阶段', 'SIXTH',   '破产案件第六阶段',   6, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(2, 'SEVENTH', '第七阶段', 'SEVENTH', '破产案件第七阶段',   7, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (3) 案件阶段 - 来源: CaseStage枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(3, 'ACCEPTANCE',      '受理阶段', 'ACCEPTANCE',      '法院受理阶段',       1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(3, 'LIQUIDATION',     '清算阶段', 'LIQUIDATION',     '破产清算阶段',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(3, 'REORGANIZATION',  '重整阶段', 'REORGANIZATION',  '破产重整阶段',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(3, 'COMPROMISE',      '和解阶段', 'COMPROMISE',      '破产和解阶段',       4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(3, 'TERMINATION',     '终结阶段', 'TERMINATION',     '破产程序终结',       5, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (4) 案件类型 - 来源: CaseType枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(4, 'LIQUIDATION',     '清算',   'LIQUIDATION',     '破产清算案件',   1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(4, 'REORGANIZATION',  '重整',   'REORGANIZATION',  '破产重整案件',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(4, 'COMPROMISE',      '和解',   'COMPROMISE',      '破产和解案件',   3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(4, 'COMMON',          '通用',   'COMMON',          '通用案件类型',   4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (5) 债权人状态 - 来源: CreditorStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(5, 'KNOWN',     '已知债权人', 'KNOWN',     '已知的债权人',       1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(5, 'CONFIRMED', '确认债权人', 'CONFIRMED', '已确认的债权人',     2, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (6) 审核状态 - 来源: ReviewStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(6, 'PENDING',  '待审核', 'PENDING',  '等待审核',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(6, 'APPROVED', '已通过', 'APPROVED', '审核已通过',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(6, 'REJECTED', '已驳回', 'REJECTED', '审核已驳回',   3, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (7) 登记状态 - 来源: RegistrationStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(7, 'PENDING',     '待登记', 'PENDING',     '等待登记',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(7, 'REGISTERED',  '已登记', 'REGISTERED',  '已完成登记',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(7, 'REJECTED',    '已驳回', 'REJECTED',    '登记被驳回',   3, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (8) 案件任务状态 - 来源: CaseTaskStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(8, 'IN_PROGRESS', '进行中', 'IN_PROGRESS', '任务正在进行中',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(8, 'COMPLETED',   '已完成', 'COMPLETED',   '任务已完成',         2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(8, 'REVIEWING',   '核审中', 'REVIEWING',   '任务正在核审',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(8, 'SKIPPED',     '跳过',   'SKIPPED',     '任务已跳过',         4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(8, 'REJECTED',    '被驳回', 'REJECTED',    '任务被驳回',         5, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (9) 提交状态 - 来源: SubmissionStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(9, 'PENDING',  '待审核', 'PENDING',  '提交等待审核',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(9, 'APPROVED', '已通过', 'APPROVED', '提交审核已通过',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(9, 'REJECTED', '已驳回', 'REJECTED', '提交审核已驳回',   3, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (10) 提交类型 - 来源: SubmissionType枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(10, 'NORMAL',   '普通提交', 'NORMAL',   '首次普通提交',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(10, 'REVISION', '修订提交', 'REVISION', '驳回后修订提交',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (11) 预警级别 - 来源: AlertLevel枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(11, 'NORMAL',    '正常',     'NORMAL',    '节点正常，无预警',         1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(11, 'SOON_DUE',  '即将到期', 'SOON_DUE',  '节点即将到期，黄色预警',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(11, 'DUE_TODAY', '今日到期', 'DUE_TODAY', '节点今日到期，橙色预警',   3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(11, 'OVERDUE',   '已逾期',   'OVERDUE',   '节点已逾期，红色预警',     4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (12) 节点状态 - 来源: NodeStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(12, 'PENDING',             '待启动',     'PENDING',             '节点尚未启动',           1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(12, 'IN_PROGRESS',         '进行中',     'IN_PROGRESS',         '节点正在进行',           2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(12, 'COMPLETED',           '已完成',     'COMPLETED',           '节点已完成',             3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(12, 'EXTENSION_REQUESTED', '延期申请中', 'EXTENSION_REQUESTED', '节点延期申请中',         4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(12, 'EXTENDED',            '已延期',     'EXTENDED',            '节点已延期',             5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(12, 'SKIPPED',             '已跳过',     'SKIPPED',             '节点已跳过',             6, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (13) 通知类型 - 来源: NotificationType枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(13, 'SYSTEM_MSG', '站内消息', 'SYSTEM_MSG', '系统站内消息通知',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(13, 'SMS',        '短信',     'SMS',        '手机短信通知',         2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(13, 'EMAIL',      '邮件',     'EMAIL',      '电子邮件通知',         3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(13, 'MULTI',      '多渠道',   'MULTI',      '多渠道组合通知',       4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (14) 责任角色 - 来源: ResponsibleRole枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(14, 'ADMINISTRATOR',     '管理人',       'ADMINISTRATOR',     '破产管理人',         1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(14, 'COURT',             '法院',         'COURT',             '受理法院',           2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(14, 'CREDITOR_MEETING',  '债权人会议',   'CREDITOR_MEETING',  '债权人会议',         3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(14, 'APPLICANT',         '申请人',       'APPLICANT',         '破产申请方',         4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (15) 计算基准 - 来源: CalculationBase枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(15, 'ACCEPTANCE_DATE',      '受理日',           'ACCEPTANCE_DATE',      '以法院受理日为基准',           1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(15, 'ANNOUNCEMENT_DATE',    '公告日',           'ANNOUNCEMENT_DATE',    '以公告发布日为基准',           2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(15, 'PREV_NODE_COMPLETE',   '前置节点完成日',   'PREV_NODE_COMPLETE',   '以前置节点完成日为基准',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(15, 'MEETING_PASS_DATE',    '会议通过日',       'MEETING_PASS_DATE',    '以债权人会议通过日为基准',     4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(15, 'COMPLETION_DATE',      '完成日',           'COMPLETION_DATE',      '以完成日为基准',               5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(15, 'REORGANIZATION_DATE',  '裁定重整日',       'REORGANIZATION_DATE',  '以法院裁定重整日为基准',       6, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (16) 延期审批状态 - 来源: ExtensionApprovalStatus枚举
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(16, 'PENDING',  '待审批', 'PENDING',  '延期申请等待审批',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(16, 'APPROVED', '已通过', 'APPROVED', '延期申请已通过',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(16, 'REJECTED', '已驳回', 'REJECTED', '延期申请已驳回',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (17) 权限类型 - 来源: tb_dict原有数据 + WorkTeamServiceImpl硬编码
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(17, 'VIEW',    '查看', 'VIEW',    '查看权限',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(17, 'EDIT',    '编辑', 'EDIT',    '编辑权限',     2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(17, 'DELETE',  '删除', 'DELETE',  '删除权限',     3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(17, 'APPROVE', '审批', 'APPROVE', '审批权限',     4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(17, 'PUBLISH', '发布', 'PUBLISH', '发布权限',     5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(17, 'ADMIN',   '管理', 'ADMIN',   '管理权限',     6, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (18) 角色类型 - 来源: tb_dict原有数据
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(18, 'SUPER_ADMIN', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(18, 'ADMIN',       '管理员',     'ADMIN',       '系统管理员，拥有大部分管理权限',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(18, 'LAWYER',      '律师',       'LAWYER',      '律师角色，可处理案件相关业务',     3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(18, 'STAFF',       '工作人员',   'STAFF',       '工作人员角色，可查看和编辑部分数据', 4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(18, 'GUEST',       '访客',       'GUEST',       '访客角色，仅有查看权限',           5, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (19) 通用状态 - 来源: tb_dict原有数据 + 硬编码
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(19, 'ACTIVE',   '激活', 'ACTIVE',   '正常启用状态',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(19, 'INACTIVE', '停用', 'INACTIVE', '已停用状态',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(19, 'DELETED',  '删除', 'DELETED',  '已删除状态',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(19, 'LOCKED',   '锁定', 'LOCKED',   '已锁定状态',       4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (20) 公告状态 - 来源: tb_dict原有数据
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(20, 'DRAFT',     '草稿',   'DRAFT',     '公告草稿，尚未发布',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(20, 'PUBLISHED', '已发布', 'PUBLISHED', '公告已发布',             2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(20, 'EXPIRED',   '已过期', 'EXPIRED',   '公告已过期',             3, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (21) 档案状态 - 来源: tb_dict原有数据
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(21, 'DRAFT',    '草稿', 'DRAFT',    '档案草稿状态',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(21, 'ACTIVE',   '激活', 'ACTIVE',   '档案已激活',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(21, 'INACTIVE', '停用', 'INACTIVE', '档案已停用',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(21, 'DELETED',  '删除', 'DELETED',  '档案已删除',       4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (22) 索引状态 - 来源: tb_dict原有数据
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(22, 'NOT_INDEXED',  '未索引',   'NOT_INDEXED',  '文档尚未建立索引',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(22, 'INDEXED',      '已索引',   'INDEXED',      '文档已建立索引',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(22, 'INDEX_FAILED', '索引失败', 'INDEX_FAILED', '文档索引建立失败',     3, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (23) 文件类型 - 来源: tb_dict原有数据
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(23, 'WORD',  'Word文档',    'WORD',  'Microsoft Word文档',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'EXCEL', 'Excel文档',   'EXCEL', 'Microsoft Excel文档',    2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'PDF',   'PDF文档',     'PDF',   'PDF格式文档',            3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'PPT',   'PowerPoint',  'PPT',   'Microsoft PowerPoint',   4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'TXT',   '纯文本',      'TXT',   '纯文本文件',             5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'ZIP',   '压缩包',      'ZIP',   'ZIP压缩文件',            6, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(23, 'OTHER', '其他',        'OTHER', '其他类型文件',           7, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (24) 访问级别 - 来源: archive_init.sql
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(24, 'PUBLIC',       '公开',   'PUBLIC',       '公开访问，所有人可见',       1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(24, 'INTERNAL',     '内部',   'INTERNAL',     '内部访问，仅内部人员可见',   2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(24, 'CONFIDENTIAL', '机密',   'CONFIDENTIAL', '机密级别，需授权访问',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(24, 'TOP_SECRET',   '绝密',   'TOP_SECRET',   '绝密级别，最高权限访问',     4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (25) 债权人类型 - 来源: Excel模板硬编码
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(25, 'ENTERPRISE', '企业', 'ENTERPRISE', '企业法人债权人',     1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(25, 'INDIVIDUAL', '个人', 'INDIVIDUAL', '自然人债权人',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (26) 债权性质 - 来源: Excel模板字段
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(26, 'SECURED',       '有担保债权',   'SECURED',       '有财产担保的债权',           1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(26, 'UNSECURED',     '无担保债权',   'UNSECURED',     '无财产担保的普通债权',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(26, 'PREFERRED',     '优先债权',     'PREFERRED',     '享有优先受偿权的债权',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(26, 'TAX',           '税收债权',     'TAX',           '欠缴税款的债权',             4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(26, 'LABOR',         '职工债权',     'LABOR',         '职工工资及补偿债权',         5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(26, 'COMMON_DEBT',   '共益债务',     'COMMON_DEBT',   '破产程序中的共益债务',       6, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (27) 资金流水类型 - 来源: FundFlow业务
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(27, 'INCOME',       '收入',   'INCOME',       '资金收入',             1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(27, 'EXPENSE',      '支出',   'EXPENSE',      '资金支出',             2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(27, 'TRANSFER',     '转账',   'TRANSFER',     '账户间转账',           3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(27, 'DISTRIBUTION', '分配',   'DISTRIBUTION', '债权分配发放',         4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(27, 'ESCROW',       '提存',   'ESCROW',       '提存资金',             5, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (28) 费用类型 - 来源: BankruptcyExpense业务
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(28, 'MANAGEMENT_FEE',     '管理费',       'MANAGEMENT_FEE',     '管理人报酬',               1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'AUCTION_FEE',        '拍卖费',       'AUCTION_FEE',        '资产拍卖费用',             2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'ASSESSMENT_FEE',     '评估费',       'ASSESSMENT_FEE',     '资产评估费用',             3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'LEGAL_FEE',          '诉讼费',       'LEGAL_FEE',          '诉讼仲裁费用',             4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'TRAVEL_FEE',         '差旅费',       'TRAVEL_FEE',         '差旅费用',                 5, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'OFFICE_FEE',         '办公费',       'OFFICE_FEE',         '日常办公费用',             6, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(28, 'OTHER_FEE',          '其他费用',     'OTHER_FEE',          '其他破产费用',             7, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (29) 审批状态 - 来源: Approval/FundApproval业务
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(29, 'PENDING',     '待审批',   'PENDING',     '等待审批',         1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(29, 'APPROVED',    '已通过',   'APPROVED',    '审批已通过',       2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(29, 'REJECTED',    '已驳回',   'REJECTED',    '审批已驳回',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(29, 'CANCELLED',   '已撤销',   'CANCELLED',   '审批已撤销',       4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- (30) 工作团队模块 - 来源: WorkTeamServiceImpl硬编码
INSERT INTO `tb_dictionary_item` (`category_id`, `item_code`, `item_name`, `item_value`, `description`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
(30, 'CASE',         '案件管理',   'CASE',         '案件相关权限模块',       1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(30, 'CREDITOR',     '债权人管理', 'CREDITOR',     '债权人相关权限模块',     2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(30, 'FUND',         '资金管理',   'FUND',         '资金相关权限模块',       3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
(30, 'ANNOUNCEMENT', '公告管理',   'ANNOUNCEMENT', '公告相关权限模块',       4, 'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- ========================================
-- 字典数据初始化完成
-- 分类: 30个, 字典项: 约120个
-- ========================================
