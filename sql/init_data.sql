-- =============================================
-- 初始数据插入脚本
-- 创建时间: 2026-01-08
-- 说明: 为主要表插入初始测试数据
-- =============================================

-- =============================================
-- 1. 角色表初始数据
-- =============================================
INSERT INTO `tb_role` (`role_code`, `role_name`, `role_desc`, `is_system`, `status`, `sort_order`) VALUES
('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', '1', 'ACTIVE', 1),
('ADMIN', '管理员', '系统管理员，拥有大部分管理权限', '1', 'ACTIVE', 2),
('LAWYER', '律师', '律师角色，可处理案件相关业务', '1', 'ACTIVE', 3),
('STAFF', '工作人员', '工作人员角色，可查看和编辑部分数据', '1', 'ACTIVE', 4),
('GUEST', '访客', '访客角色，仅有查看权限', '1', 'ACTIVE', 5);

-- =============================================
-- 2. 权限表初始数据
-- =============================================
INSERT INTO `tb_permission` (`perm_code`, `perm_name`, `perm_type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`) VALUES
-- 一级菜单
('system', '系统管理', 'M', 0, '/system', NULL, 'Setting', 1, 'ACTIVE'),
('case', '案件管理', 'M', 0, '/case', NULL, 'FolderOpened', 2, 'ACTIVE'),
('creditor', '债权人管理', 'M', 0, '/creditor', NULL, 'User', 3, 'ACTIVE'),
('fund', '资金管理', 'M', 0, '/fund', NULL, 'Money', 4, 'ACTIVE'),
('announcement', '公告管理', 'M', 0, '/announcement', NULL, 'Bell', 5, 'ACTIVE'),

-- 系统管理子菜单
('system:user', '用户管理', 'M', 1, '/system/user', 'system/user/index', 'User', 1, 'ACTIVE'),
('system:role', '角色管理', 'M', 1, '/system/role', 'system/role/index', 'Team', 2, 'ACTIVE'),
('system:permission', '权限管理', 'M', 1, '/system/permission', 'system/permission/index', 'Key', 3, 'ACTIVE'),
('system:config', '系统配置', 'M', 1, '/system/config', 'system/config/index', 'Setting', 4, 'ACTIVE'),

-- 案件管理子菜单
('case:list', '案件列表', 'M', 2, '/case/list', 'case/list/index', 'List', 1, 'ACTIVE'),
('case:detail', '案件详情', 'M', 2, '/case/detail', 'case/detail/index', 'Document', 2, 'ACTIVE'),
('case:admin', '管理人管理', 'M', 2, '/case/admin', 'case/admin/index', 'UserGroup', 3, 'ACTIVE'),

-- 债权人管理子菜单
('creditor:list', '债权人列表', 'M', 3, '/creditor/list', 'creditor/list/index', 'User', 1, 'ACTIVE'),
('creditor:claim', '债权申报', 'M', 3, '/creditor/claim', 'creditor/claim/index', 'FileAdd', 2, 'ACTIVE'),

-- 资金管理子菜单
('fund:account', '资金账户', 'M', 4, '/fund/account', 'fund/account/index', 'Wallet', 1, 'ACTIVE'),
('fund:flow', '资金流水', 'M', 4, '/fund/flow', 'fund/flow/index', 'Transaction', 2, 'ACTIVE'),
('fund:approval', '资金审批', 'M', 4, '/fund/approval', 'fund/approval/index', 'Check', 3, 'ACTIVE'),

-- 公告管理子菜单
('announcement:list', '公告列表', 'M', 5, '/announcement/list', 'announcement/list/index', 'Bell', 1, 'ACTIVE'),
('announcement:publish', '发布公告', 'M', 5, '/announcement/publish', 'announcement/publish/index', 'Send', 2, 'ACTIVE'),

-- 操作权限
('system:user:add', '添加用户', 'B', 6, NULL, NULL, NULL, 1, 'ACTIVE'),
('system:user:edit', '编辑用户', 'B', 6, NULL, NULL, NULL, 2, 'ACTIVE'),
('system:user:delete', '删除用户', 'B', 6, NULL, NULL, NULL, 3, 'ACTIVE'),
('system:user:query', '查询用户', 'B', 6, NULL, NULL, NULL, 4, 'ACTIVE'),
('case:add', '添加案件', 'B', 10, NULL, NULL, NULL, 1, 'ACTIVE'),
('case:edit', '编辑案件', 'B', 10, NULL, NULL, NULL, 2, 'ACTIVE'),
('case:delete', '删除案件', 'B', 10, NULL, NULL, NULL, 3, 'ACTIVE'),
('case:query', '查询案件', 'B', 10, NULL, NULL, NULL, 4, 'ACTIVE'),
('fund:transfer', '资金转账', 'B', 14, NULL, NULL, NULL, 1, 'ACTIVE'),
('fund:approve', '资金审批', 'B', 16, NULL, NULL, NULL, 1, 'ACTIVE');

-- =============================================
-- 3. 用户表初始数据
-- =============================================
INSERT INTO `tb_user` (`username`, `password`, `real_name`, `mobile`, `email`, `phone`, `is_valid`, `status`, `login_type`, `login_count`, `create_user_id`) VALUES
('admin', '$2a$10$4gyUon33ugZxdhAqFrhxgucg8ywGzwCCqF/5Ygp.12XpA.4Ypk4KC', '系统管理员', '13800138000', 'admin@law.com', '010-12345678', '1', 'ACTIVE', '1', 0, NULL),
('lawyer01', '$2a$10$4gyUon33ugZxdhAqFrhxgucg8ywGzwCCqF/5Ygp.12XpA.4Ypk4KC', '张律师', '13800138001', 'zhang@law.com', '010-12345679', '1', 'ACTIVE', '1', 0, NULL),
('lawyer02', '$2a$10$4gyUon33ugZxdhAqFrhxgucg8ywGzwCCqF/5Ygp.12XpA.4Ypk4KC', '李律师', '13800138002', 'li@law.com', '010-12345680', '1', 'ACTIVE', '1', 0, NULL),
('staff01', '$2a$10$4gyUon33ugZxdhAqFrhxgucg8ywGzwCCqF/5Ygp.12XpA.4Ypk4KC', '王工作人员', '13800138003', 'wang@law.com', '010-12345681', '1', 'ACTIVE', '1', 0, NULL),
('staff02', '$2a$10$4gyUon33ugZxdhAqFrhxgucg8ywGzwCCqF/5Ygp.12XpA.4Ypk4KC', '赵工作人员', '13800138004', 'zhao@law.com', '010-12345682', '1', 'ACTIVE', '1', 0, NULL);

-- =============================================
-- 4. 用户角色关联表初始数据
-- =============================================
INSERT INTO `tb_user_role` (`user_id`, `role_id`, `create_user_id`) VALUES
(1, 1, NULL),
(2, 3, NULL),
(3, 3, NULL),
(4, 4, NULL),
(5, 4, NULL);

-- =============================================
-- 5. 角色权限关联表初始数据
-- =============================================
-- 超级管理员拥有所有权限
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_user_id`)
SELECT 1, id, NULL FROM `tb_permission` WHERE status = 'ACTIVE';

-- 管理员拥有大部分权限（除了系统用户管理）
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_user_id`)
SELECT 2, id, NULL FROM `tb_permission` WHERE status = 'ACTIVE' AND perm_code NOT IN ('system:user', 'system:user:add', 'system:user:edit', 'system:user:delete');

-- 律师拥有案件和债权人管理权限
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_user_id`)
SELECT 3, id, NULL FROM `tb_permission` WHERE status = 'ACTIVE' AND (perm_code LIKE 'case:%' OR perm_code LIKE 'creditor:%' OR perm_code LIKE 'fund:%');

-- 工作人员拥有查看和部分编辑权限
INSERT INTO `tb_role_permission` (`role_id`, `perm_id`, `create_user_id`)
SELECT 4, id, NULL FROM `tb_permission` WHERE status = 'ACTIVE' AND (perm_code LIKE '%:query' OR perm_code LIKE '%:list' OR perm_code LIKE '%:detail');

-- =============================================
-- 6. 法院信息表初始数据
-- =============================================
INSERT INTO `tb_court` (`full_name`, `short_name`, `court_level`, `address`, `contact_phone`, `responsible_user_id`, `undertaking_judge`, `status`, `create_user_id`) VALUES
('北京市第一中级人民法院', '北京一中院', '中级人民法院', '北京市朝阳区建国路88号', '010-12345678', 1, '张法官', 'ACTIVE', 1),
('北京市第二中级人民法院', '北京二中院', '中级人民法院', '北京市东城区正义路9号', '010-12345679', 1, '李法官', 'ACTIVE', 1),
('上海市高级人民法院', '上海高院', '高级人民法院', '上海市黄浦区福州路209号', '021-12345678', 1, '王法官', 'ACTIVE', 1),
('广州市中级人民法院', '广州中院', '中级人民法院', '广州市天河区体育东路106号', '020-12345678', 1, '赵法官', 'ACTIVE', 1);

-- =============================================
-- 7. 案件信息表初始数据
-- =============================================
INSERT INTO `tb_bankrupt_case` (`case_number`, `case_name`, `acceptance_date`, `case_source`, `acceptance_court`, `designated_institution`, `main_responsible_person`, `is_simplified_trial`, `case_reason`, `case_progress`, `debt_claim_deadline`, `filing_date`, `case_status`, `designated_judge`, `creator_id`, `creator_name`, `status`, `create_user_id`) VALUES
('（2024）京01破1号', '北京某科技有限公司破产清算案', '2024-01-15', '法院指定', '北京市第一中级人民法院', '北京某律师事务所', '张律师', 0, '资不抵债', 'SECOND', '2024-03-15 23:59:59', '2024-01-10', 'IN_PROGRESS', '张法官', 2, '张律师', 'ACTIVE', 1),
('（2024）沪01破1号', '上海某贸易公司破产重整案', '2024-02-20', '债权人申请', '上海市高级人民法院', '上海某会计师事务所', '李律师', 0, '经营困难', 'FIRST', '2024-04-20 23:59:59', '2024-02-15', 'IN_PROGRESS', '王法官', 3, '李律师', 'ACTIVE', 1),
('（2024）粤01破1号', '广州某制造企业破产和解案', '2024-03-10', '债务人申请', '广州市中级人民法院', '广州某律师事务所', '赵律师', 1, '资金链断裂', 'THIRD', '2024-05-10 23:59:59', '2024-03-05', 'PENDING', '赵法官', 2, '张律师', 'ACTIVE', 1);

-- =============================================
-- 8. 债务人信息表初始数据
-- =============================================
INSERT INTO `tb_debtor_enterprise` (`case_id`, `enterprise_name`, `unified_social_credit_code`, `legal_representative`, `registration_authority`, `establishment_date`, `registered_capital`, `business_scope`, `enterprise_type`, `industry`, `registered_address`, `contact_phone`, `contact_person`, `status`, `create_user_id`) VALUES
(1, '北京某科技有限公司', '91110108MA01XXXXXXXX', '张三', '北京市工商行政管理局朝阳分局', '2015-06-15', 5000000.0000, '技术开发、技术服务、技术咨询、技术转让、技术推广', '有限责任公司', '软件和信息技术服务业', '北京市朝阳区建国路88号', '010-87654321', '张三', 'ACTIVE', 2),
(2, '上海某贸易公司', '91310000MA02XXXXXXXX', '李四', '上海市工商行政管理局浦东新区分局', '2018-03-20', 10000000.0000, '货物进出口、技术进出口、国内贸易', '有限责任公司', '批发和零售业', '上海市浦东新区陆家嘴环路1000号', '021-87654321', '李四', 'ACTIVE', 3),
(3, '广州某制造企业', '91440101MA03XXXXXXXX', '王五', '广州市工商行政管理局天河分局', '2016-08-10', 8000000.0000, '机械制造、设备销售、技术服务', '有限责任公司', '制造业', '广州市天河区体育东路106号', '020-87654321', '王五', 'ACTIVE', 2);

-- =============================================
-- 9. 债权人信息表初始数据
-- =============================================
INSERT INTO `tb_creditor_info` (`case_id`, `creditor_name`, `creditor_type`, `contact_phone`, `contact_email`, `address`, `id_number`, `legal_representative`, `registered_capital`, `status`, `create_user_id`) VALUES
(1, '北京某银行', '金融机构', '010-12345678', 'bank1@bank.com', '北京市西城区金融街1号', '91110000MA04XXXXXXXX', '赵六', 1000000000.0000, 'ACTIVE', 2),
(1, '北京某供应商', '企业', '010-87654322', 'supplier1@company.com', '北京市海淀区中关村大街1号', '91110108MA05XXXXXXXX', '钱七', 5000000.0000, 'ACTIVE', 2),
(2, '上海某投资公司', '金融机构', '021-12345678', 'invest@invest.com', '上海市黄浦区南京东路100号', '91310000MA06XXXXXXXX', '孙八', 50000000.0000, 'ACTIVE', 3),
(2, '上海某物流公司', '企业', '021-87654322', 'logistics@company.com', '上海市浦东新区张江高科技园区', '91310000MA07XXXXXXXX', '周九', 3000000.0000, 'ACTIVE', 3),
(3, '广州某银行', '金融机构', '020-12345678', 'bank2@bank.com', '广州市天河区珠江新城', '91440101MA08XXXXXXXX', '吴十', 800000000.0000, 'ACTIVE', 2),
(3, '广州某材料供应商', '企业', '020-87654322', 'material@company.com', '广州市白云区机场路1号', '91440101MA09XXXXXXXX', '郑十一', 2000000.0000, 'ACTIVE', 2);

-- =============================================
-- 10. 债权申报表初始数据
-- =============================================
INSERT INTO `tb_creditor_claim` (`case_id`, `case_name`, `debtor`, `bank_account`, `creditor_name`, `creditor_type`, `credit_code`, `legal_representative`, `service_address`, `agent_name`, `agent_phone`, `agent_id_card`, `agent_address`, `account_name`, `creditor_bank_account`, `bank_name`, `principal`, `interest`, `penalty`, `other_losses`, `total_amount`, `has_court_judgment`, `has_execution`, `has_collateral`, `claim_nature`, `claim_type`, `claim_facts`, `claim_nature_manager`, `claim_identifier`, `evidence_list`, `evidence_materials`, `remarks`, `registration_status`, `status`, `create_user_id`) VALUES
(1, '北京某科技有限公司破产清算案', '北京某科技有限公司', '北京某科技有限公司账户', '北京某银行', '金融机构', '91110000MA04XXXXXXXX', '赵六', '北京市西城区金融街1号', '李律师', '13800138001', '110101199001011234', '北京市朝阳区建国路88号', '北京某银行', '6222020200001234567', '工商银行北京分行', 5000000.00, 200000.00, 0.00, 50000.00, 5250000.00, 1, 1, 1, '有担保债权', '贷款债权', '债务人于2023年1月向债权人借款500万元，用于企业经营', '有担保债权', 'CLM2024001', '借款合同、担保合同、还款记录', '借款合同、担保合同、还款记录', '已提供担保物', 'REGISTERED', 'ACTIVE', 2),
(1, '北京某科技有限公司破产清算案', '北京某科技有限公司', '北京某科技有限公司账户', '北京某供应商', '企业', '91110108MA05XXXXXXXX', '钱七', '北京市海淀区中关村大街1号', '张律师', '13800138002', '110101199002021234', '北京市朝阳区建国路88号', '北京某供应商', '6222020200002345678', '建设银行北京分行', 1000000.00, 50000.00, 0.00, 10000.00, 1060000.00, 0, 0, 0, '普通债权', '货款债权', '债务人欠付货款100万元', '普通债权', 'CLM2024002', '采购合同、送货单、发票', '采购合同、送货单、发票', '无担保', 'REGISTERED', 'ACTIVE', 2),
(2, '上海某贸易公司破产重整案', '上海某贸易公司', '上海某贸易公司账户', '上海某投资公司', '金融机构', '91310000MA06XXXXXXXX', '孙八', '上海市黄浦区南京东路100号', '李律师', '13800138002', '310101199003031234', '上海市浦东新区陆家嘴环路1000号', '上海某投资公司', '6222020200003456789', '工商银行上海分行', 3000000.00, 150000.00, 0.00, 30000.00, 3180000.00, 1, 0, 1, '有担保债权', '投资债权', '债务人于2022年向债权人借款300万元', '有担保债权', 'CLM2024003', '投资协议、担保合同', '投资协议、担保合同', '已提供担保物', 'REGISTERED', 'ACTIVE', 3),
(3, '广州某制造企业破产和解案', '广州某制造企业', '广州某制造企业账户', '广州某银行', '金融机构', '91440101MA08XXXXXXXX', '吴十', '广州市天河区珠江新城', '张律师', '13800138001', '440101199004041234', '广州市天河区体育东路106号', '广州某银行', '6222020200004567890', '工商银行广州分行', 2000000.00, 80000.00, 0.00, 20000.00, 2100000.00, 1, 1, 1, '有担保债权', '贷款债权', '债务人于2023年向债权人借款200万元', '有担保债权', 'CLM2024004', '借款合同、担保合同、还款记录', '借款合同、担保合同、还款记录', '已提供担保物', 'PENDING', 'ACTIVE', 2);

-- =============================================
-- 11. 系统配置表初始数据
-- =============================================
INSERT INTO `tb_system_config` (`config_key`, `config_value`, `config_desc`, `config_group`, `sort_order`, `status`, `create_user_id`) VALUES
('system.name', '破产案件管理系统', '系统名称', '系统', 1, 'ACTIVE', 1),
('system.version', '1.0.0', '系统版本', '系统', 2, 'ACTIVE', 1),
('system.company', '某某律师事务所', '所属机构', '系统', 3, 'ACTIVE', 1),
('login.max_fail_count', '5', '最大登录失败次数', '登录', 1, 'ACTIVE', 1),
('login.lock_time', '30', '账号锁定时间（分钟）', '登录', 2, 'ACTIVE', 1),
('login.session_timeout', '120', '会话超时时间（分钟）', '登录', 3, 'ACTIVE', 1),
('sms.expire_time', '5', '短信验证码过期时间（分钟）', '短信', 1, 'ACTIVE', 1),
('file.max_size', '104857600', '文件最大大小（字节）', '文件', 1, 'ACTIVE', 1),
('file.allowed_types', 'doc,docx,pdf,xls,xlsx,jpg,jpeg,png', '允许上传的文件类型', '文件', 2, 'ACTIVE', 1),
('password.min_length', '6', '密码最小长度', '密码', 1, 'ACTIVE', 1),
('password.expire_days', '90', '密码过期天数', '密码', 2, 'ACTIVE', 1),
('case.debt_claim_days', '30', '债权申报天数', '案件', 1, 'ACTIVE', 1),
('notification.email_enabled', 'true', '是否启用邮件通知', '通知', 1, 'ACTIVE', 1),
('notification.sms_enabled', 'true', '是否启用短信通知', '通知', 2, 'ACTIVE', 1);

-- =============================================
-- 12. 银行账户表初始数据
-- =============================================
INSERT INTO `tb_bank_account` (`account_name`, `bank_name`, `account_number`, `account_type`, `currency`, `current_balance`, `opening_date`, `status`, `create_user_id`) VALUES
('北京某科技有限公司破产清算账户', '工商银行北京分行', '6222020200009999999', '专用账户', 'CNY', 1000000.00, '2024-01-15', 'ACTIVE', 2),
('上海某贸易公司破产重整账户', '工商银行上海分行', '6222020200008888888', '专用账户', 'CNY', 2000000.00, '2024-02-20', 'ACTIVE', 3),
('广州某制造企业破产和解账户', '工商银行广州分行', '6222020200007777777', '专用账户', 'CNY', 500000.00, '2024-03-10', 'ACTIVE', 2);

-- =============================================
-- 13. 资金账户表初始数据
-- =============================================
INSERT INTO `tb_fund_account` (`case_id`, `case_name`, `account_name`, `account_type`, `initial_balance`, `current_balance`, `bank_name`, `bank_account`, `status`, `create_user_id`) VALUES
(1, '北京某科技有限公司破产清算案', '北京某科技有限公司破产清算账户', '专用账户', 1000000.00, 1000000.00, '工商银行北京分行', '6222020200009999999', 'ACTIVE', 2),
(2, '上海某贸易公司破产重整案', '上海某贸易公司破产重整账户', '专用账户', 2000000.00, 2000000.00, '工商银行上海分行', '6222020200008888888', 'ACTIVE', 3),
(3, '广州某制造企业破产和解案', '广州某制造企业破产和解账户', '专用账户', 500000.00, 500000.00, '工商银行广州分行', '6222020200007777777', 'ACTIVE', 2);

-- =============================================
-- 14. 管理人信息表初始数据
-- =============================================
INSERT INTO `tb_administrator` (`case_id`, `administrator_type`, `responsible_person_id`, `contact_phone`, `contact_email`, `office_address`, `status`, `create_user_id`) VALUES
(1, '律师事务所', NULL, '13800138001', 'zhang@law.com', '北京市朝阳区建国路88号', 'ACTIVE', 1),
(2, '会计师事务所', NULL, '13800138002', 'li@law.com', '上海市浦东新区陆家嘴环路1000号', 'ACTIVE', 1),
(3, '律师事务所', NULL, '13800138001', 'zhang@law.com', '广州市天河区体育东路106号', 'ACTIVE', 1);

-- 获取管理人ID
SET @admin1_id = (SELECT id FROM `tb_administrator` WHERE case_id = 1 LIMIT 1);
SET @admin2_id = (SELECT id FROM `tb_administrator` WHERE case_id = 2 LIMIT 1);
SET @admin3_id = (SELECT id FROM `tb_administrator` WHERE case_id = 3 LIMIT 1);

-- =============================================
-- 15. 管理人员工信息表初始数据
-- =============================================
INSERT INTO `tb_administrator_staff` (`administrator_id`, `name`, `staff_type`, `id_number`, `lawyer_license_number`, `contact_phone`, `email`, `responsibility`, `appointment_date`, `status`, `create_user_id`) VALUES
(@admin1_id, '张律师', '负责人', '110101198001011234', '110119850000001', '13800138001', 'zhang@law.com', '全面负责案件管理工作', '2024-01-15', 'ACTIVE', 1),
(@admin1_id, '王工作人员', '工作人员', '110101198502021234', NULL, '13800138003', 'wang@law.com', '负责债权登记和审核', '2024-01-15', 'ACTIVE', 1),
(@admin2_id, '李律师', '负责人', '310101198203031234', '310119850000002', '13800138002', 'li@law.com', '全面负责案件管理工作', '2024-02-20', 'ACTIVE', 1),
(@admin2_id, '赵工作人员', '工作人员', '310101198604041234', NULL, '13800138004', 'zhao@law.com', '负责资产清查和评估', '2024-02-20', 'ACTIVE', 1),
(@admin3_id, '张律师', '负责人', '110101198001011234', '110119850000001', '13800138001', 'zhang@law.com', '全面负责案件管理工作', '2024-03-10', 'ACTIVE', 1);

-- 获取管理人员工ID
SET @staff1_id = (SELECT id FROM `tb_administrator_staff` WHERE administrator_id = @admin1_id AND name = '张律师' LIMIT 1);
SET @staff3_id = (SELECT id FROM `tb_administrator_staff` WHERE administrator_id = @admin2_id AND name = '李律师' LIMIT 1);
SET @staff5_id = (SELECT id FROM `tb_administrator_staff` WHERE administrator_id = @admin3_id AND name = '张律师' LIMIT 1);

-- 更新 tb_administrator 表的 responsible_person_id 字段
UPDATE `tb_administrator` SET `responsible_person_id` = @staff1_id WHERE `id` = @admin1_id;
UPDATE `tb_administrator` SET `responsible_person_id` = @staff3_id WHERE `id` = @admin2_id;
UPDATE `tb_administrator` SET `responsible_person_id` = @staff5_id WHERE `id` = @admin3_id;

-- =============================================
-- 16. 案件公告表初始数据
-- =============================================
INSERT INTO `tb_case_announcement` (`case_id`, `title`, `content`, `announcement_type`, `status`, `publisher_id`, `publisher_name`, `publish_time`, `view_count`, `is_top`, `create_user_id`) VALUES
(1, '关于北京某科技有限公司破产清算案的债权申报公告', '根据《中华人民共和国企业破产法》相关规定，现就北京某科技有限公司破产清算案债权申报事宜公告如下：一、债权申报截止时间：2024年3月15日；二、债权申报地点：北京市朝阳区建国路88号；三、联系人：张律师，联系电话：13800138001。', 'ANNOUNCEMENT', 'PUBLISHED', 2, '张律师', '2024-01-16 10:00:00', 150, 1, 1),
(2, '关于上海某贸易公司破产重整案的第一次债权人会议通知', '根据《中华人民共和国企业破产法》相关规定，现定于2024年3月20日召开上海某贸易公司破产重整案第一次债权人会议。会议地点：上海市黄浦区福州路209号。请各位债权人准时参加。', 'NOTICE', 'PUBLISHED', 3, '李律师', '2024-02-21 09:00:00', 80, 0, 1),
(3, '关于广州某制造企业破产和解案的重要提示', '广州某制造企业破产和解案已受理，请相关债权人在规定期限内申报债权。如有疑问，请联系管理人张律师，联系电话：13800138001。', 'WARNING', 'PUBLISHED', 2, '张律师', '2024-03-11 14:00:00', 50, 0, 1);

-- =============================================
-- 17. 工作计划表初始数据
-- =============================================
INSERT INTO `tb_work_plan` (`plan_number`, `plan_type`, `plan_content`, `start_date`, `end_date`, `responsible_user_id`, `execution_status`, `case_id`, `status`, `create_user_id`) VALUES
('WP2024001', 'MONTHLY', '完成北京某科技有限公司破产清算案的债权登记工作，审核所有债权申报材料，编制债权表', '2024-01-15', '2024-02-15', 2, 'COMPLETED', 1, 'ACTIVE', 2),
('WP2024002', 'MONTHLY', '完成上海某贸易公司破产重整案的资产清查工作，评估企业资产价值', '2024-02-20', '2024-03-20', 3, 'IN_PROGRESS', 2, 'ACTIVE', 3),
('WP2024003', 'MONTHLY', '完成广州某制造企业破产和解案的债权申报工作，组织债权人会议', '2024-03-10', '2024-04-10', 2, 'NOT_STARTED', 3, 'ACTIVE', 2);

-- =============================================
-- 18. 工作团队表初始数据
-- =============================================
INSERT INTO `tb_work_team` (`team_name`, `team_leader_id`, `case_id`, `team_description`, `status`, `create_user_id`) VALUES
('北京某科技有限公司破产清算团队', 2, 1, '负责北京某科技有限公司破产清算案的全面工作', 'ACTIVE', 1),
('上海某贸易公司破产重整团队', 3, 2, '负责上海某贸易公司破产重整案的全面工作', 'ACTIVE', 1),
('广州某制造企业破产和解团队', 2, 3, '负责广州某制造企业破产和解案的全面工作', 'ACTIVE', 1);

-- =============================================
-- 19. 工作团队成员表初始数据
-- =============================================
INSERT INTO `tb_work_team_member` (`team_id`, `case_id`, `user_id`, `team_role`, `permission_level`, `is_active`, `status`, `create_user_id`) VALUES
(1, 1, 2, '负责人', 'ADMIN', 1, 'ACTIVE', 1),
(1, 1, 4, '成员', 'EDIT', 1, 'ACTIVE', 1),
(2, 2, 3, '负责人', 'ADMIN', 1, 'ACTIVE', 1),
(2, 2, 5, '成员', 'EDIT', 1, 'ACTIVE', 1),
(3, 3, 2, '负责人', 'ADMIN', 1, 'ACTIVE', 1);

-- =============================================
-- 20. 系统待办事项表初始数据
-- =============================================
INSERT INTO `tb_todo` (`user_id`, `title`, `description`, `priority`, `status`, `deadline`, `source_type`, `source_id`, `related_type`, `related_id`, `create_user_id`) VALUES
(2, '审核北京某科技有限公司破产清算案债权申报', '审核债权人提交的债权申报材料，确认债权金额和性质', 'HIGH', 'PENDING', '2024-02-15 18:00:00', 'CASE', 1, 'CREDITOR_CLAIM', 1, 1),
(3, '完成上海某贸易公司破产重整案资产清查', '清查企业所有资产，编制资产清单', 'HIGH', 'IN_PROGRESS', '2024-03-20 18:00:00', 'CASE', 2, 'BANKRUPT_CASE', 2, 1),
(2, '组织广州某制造企业破产和解案债权人会议', '准备会议材料，通知债权人参加', 'MEDIUM', 'PENDING', '2024-04-10 18:00:00', 'CASE', 3, 'BANKRUPT_CASE', 3, 1),
(4, '整理北京某科技有限公司破产清算案文件', '整理和归档案件相关文件', 'LOW', 'COMPLETED', '2024-02-01 18:00:00', 'CASE', 1, 'BANKRUPT_CASE', 1, 1);

-- =============================================
-- 21. 系统通知表初始数据
-- =============================================
INSERT INTO `tb_notification` (`user_id`, `type`, `title`, `content`, `related_type`, `related_id`, `is_read`, `status`, `create_user_id`) VALUES
(2, 'SYSTEM', '新案件分配通知', '您已被分配到北京某科技有限公司破产清算案，请尽快开展工作', 'BANKRUPT_CASE', 1, 1, 'ACTIVE', 1),
(3, 'SYSTEM', '新案件分配通知', '您已被分配到上海某贸易公司破产重整案，请尽快开展工作', 'BANKRUPT_CASE', 2, 1, 'ACTIVE', 1),
(2, 'REMINDER', '债权申报截止提醒', '北京某科技有限公司破产清算案债权申报将于2024年3月15日截止', 'BANKRUPT_CASE', 1, 0, 'ACTIVE', 1),
(3, 'REMINDER', '债权人会议提醒', '上海某贸易公司破产重整案第一次债权人会议将于2024年3月20日召开', 'BANKRUPT_CASE', 2, 0, 'ACTIVE', 1);

-- =============================================
-- 22. 系统操作日志表初始数据
-- =============================================
INSERT INTO `tb_activity` (`user_id`, `user_name`, `type`, `content`, `related_type`, `related_id`, `status`, `create_user_id`) VALUES
(1, '系统管理员', 'LOGIN', '用户登录系统', 'USER', 1, 'ACTIVE', 1),
(2, '张律师', 'CREATE', '创建案件：北京某科技有限公司破产清算案', 'BANKRUPT_CASE', 1, 'ACTIVE', 2),
(3, '李律师', 'CREATE', '创建案件：上海某贸易公司破产重整案', 'BANKRUPT_CASE', 2, 'ACTIVE', 3),
(2, '张律师', 'UPDATE', '更新案件状态：北京某科技有限公司破产清算案', 'BANKRUPT_CASE', 1, 'ACTIVE', 2),
(3, '李律师', 'CREATE', '发布公告：关于上海某贸易公司破产重整案的第一次债权人会议通知', 'ANNOUNCEMENT', 2, 'ACTIVE', 3);

-- =============================================
-- 23. 资金流水表初始数据
-- =============================================
INSERT INTO `tb_fund_flow` (`case_id`, `case_name`, `account_id`, `flow_type`, `amount`, `balance_before`, `balance_after`, `transaction_date`, `description`, `related_document`, `operator_id`, `operation_time`, `remark`, `status`, `create_user_id`) VALUES
(1, '北京某科技有限公司破产清算案', 1, 'INCOME', 1000000.00, 0.00, 1000000.00, '2024-01-15 10:00:00', '初始资金转入', '资金转入凭证', 2, '2024-01-15 10:00:00', '案件启动资金', 'ACTIVE', 2),
(2, '上海某贸易公司破产重整案', 2, 'INCOME', 2000000.00, 0.00, 2000000.00, '2024-02-20 09:00:00', '初始资金转入', '资金转入凭证', 3, '2024-02-20 09:00:00', '案件启动资金', 'ACTIVE', 3),
(3, '广州某制造企业破产和解案', 3, 'INCOME', 500000.00, 0.00, 500000.00, '2024-03-10 14:00:00', '初始资金转入', '资金转入凭证', 2, '2024-03-10 14:00:00', '案件启动资金', 'ACTIVE', 2);

-- =============================================
-- 初始数据插入完成
-- =============================================
