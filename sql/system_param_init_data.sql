-- ========================================
-- 系统参数初始化数据
-- 创建时间: 2026-05-05
-- 用途: 初始化 tb_system_config 表
-- ========================================

INSERT INTO `tb_system_config` (`config_key`, `config_value`, `config_desc`, `config_group`, `sort_order`, `status`, `is_deleted`, `create_time`, `update_time`, `create_user_id`, `update_user_id`) VALUES
-- 基础配置
('system.name',              '破产案件管理系统',           '系统名称',                     'BASIC',      1,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('system.version',           '2.0.0',                     '系统版本号',                   'BASIC',      2,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('system.logo',              '',                           '系统Logo路径',                 'BASIC',      3,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('system.copyright',         '© 2026 破产案件管理系统',    '版权信息',                     'BASIC',      4,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('system.icp',               '',                           'ICP备案号',                    'BASIC',      5,  'ACTIVE', 0, NOW(), NOW(), 1, 1),

-- 安全配置
('security.password.min_length',    '8',                    '密码最小长度',                 'SECURITY',   1,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.password.require_upper', 'true',                 '密码是否需要大写字母',         'SECURITY',   2,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.password.require_lower', 'true',                 '密码是否需要小写字母',         'SECURITY',   3,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.password.require_digit', 'true',                 '密码是否需要数字',             'SECURITY',   4,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.password.require_special','false',                '密码是否需要特殊字符',         'SECURITY',   5,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.login.max_attempts',     '5',                    '登录最大尝试次数',             'SECURITY',   6,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.login.lock_duration',    '30',                   '账号锁定时长(分钟)',           'SECURITY',   7,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.token.expire_hours',     '24',                   'Token过期时间(小时)',          'SECURITY',   8,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('security.sms.enabled',            'false',                '是否启用短信验证码',           'SECURITY',   9,  'ACTIVE', 0, NOW(), NOW(), 1, 1),

-- 案件配置
('case.default_status',             'PENDING',              '新建案件默认状态',             'CASE',       1,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('case.number.prefix',              'PC',                   '案件编号前缀',                 'CASE',       2,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('case.number.format',              'PC-{YEAR}-{SEQ}',      '案件编号格式',                 'CASE',       3,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('case.number.seq_length',          '4',                    '案件编号序号长度',             'CASE',       4,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('case.claim_deadline_days',        '30',                   '债权申报截止天数',             'CASE',       5,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('case.node_alert_before_days',     '7',                    '节点到期预警天数',             'CASE',       6,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('case.auto_archive_days',          '365',                  '结案后自动归档天数',           'CASE',       7,  'ACTIVE', 0, NOW(), NOW(), 1, 1),

-- 文件配置
('file.upload.max_size_mb',         '50',                   '文件上传最大大小(MB)',         'FILE',       1,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('file.upload.allowed_types',       'WORD,EXCEL,PDF,PPT,TXT,ZIP,IMAGE', '允许上传的文件类型', 'FILE',   2,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('file.storage.path',               '/data/law/files',      '文件存储路径',                 'FILE',       3,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('file.temp.expire_hours',          '24',                   '临时文件过期时间(小时)',       'FILE',       4,  'ACTIVE', 0, NOW(), NOW(), 1, 1),

-- 通知配置
('notification.system.enabled',     'true',                 '是否启用站内消息',             'NOTIFICATION', 1, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
('notification.sms.enabled',        'false',                '是否启用短信通知',             'NOTIFICATION', 2, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
('notification.email.enabled',      'false',                '是否启用邮件通知',             'NOTIFICATION', 3, 'ACTIVE', 0, NOW(), NOW(), 1, 1),
('notification.draft.remind_days',  '3',                    '草稿提醒天数',                 'NOTIFICATION', 4, 'ACTIVE', 0, NOW(), NOW(), 1, 1),

-- 备份配置
('backup.enabled',                  'true',                 '是否启用自动备份',             'BACKUP',     1,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('backup.cron',                     '0 0 2 * * ?',          '自动备份Cron表达式',           'BACKUP',     2,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('backup.path',                     '/data/law/backup',     '备份文件存储路径',             'BACKUP',     3,  'ACTIVE', 0, NOW(), NOW(), 1, 1),
('backup.max_count',                '30',                   '最大备份保留数量',             'BACKUP',     4,  'ACTIVE', 0, NOW(), NOW(), 1, 1);

-- ========================================
-- 系统参数初始化完成
-- 共6个分组, 32个参数
-- ========================================
