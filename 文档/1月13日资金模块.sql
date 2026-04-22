-- 资金模块数据库表结构
-- 创建日期: 2026-01-13
-- 版本: v1.0

-- ========================================
-- 破产费用管理表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_bankruptcy_expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_no VARCHAR(50) UNIQUE NOT NULL COMMENT '费用编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    expense_type VARCHAR(50) NOT NULL COMMENT '费用类型',
    expense_category VARCHAR(50) COMMENT '费用分类',
    expense_name VARCHAR(200) NOT NULL COMMENT '费用项目名称',
    expense_description TEXT COMMENT '费用描述',
    applied_amount DECIMAL(18,2) COMMENT '申请金额',
    approved_amount DECIMAL(18,2) COMMENT '批准金额',
    paid_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '已支付金额',
    unpaid_amount DECIMAL(18,2) COMMENT '未支付金额',
    expense_basis VARCHAR(50) COMMENT '费用依据',
    basis_document VARCHAR(200) COMMENT '依据文件',
    basis_description TEXT COMMENT '依据描述',
    expense_purpose TEXT COMMENT '费用用途',
    approval_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已批准, REJECTED-已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    approval_date TIMESTAMP NULL COMMENT '审批日期',
    approval_opinion TEXT COMMENT '审批意见',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态: UNPAID-未支付, PAID-已支付, PARTIAL-部分支付',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_date TIMESTAMP NULL COMMENT '支付日期',
    payment_account_id BIGINT COMMENT '支付账户ID',
    payee_name VARCHAR(200) COMMENT '收款人名称',
    payee_account VARCHAR(100) COMMENT '收款账户',
    payee_bank VARCHAR(200) COMMENT '收款银行',
    payment_voucher VARCHAR(200) COMMENT '支付凭证',
    related_business_type VARCHAR(50) COMMENT '关联业务类型',
    related_business_id BIGINT COMMENT '关联业务ID',
    related_flow_id BIGINT COMMENT '关联流水ID',
    attachments TEXT COMMENT '附件',
    expense_date TIMESTAMP NULL COMMENT '费用发生日期',
    apply_date TIMESTAMP NULL COMMENT '申请日期',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='破产费用管理表';

CREATE INDEX idx_expense_no ON tb_bankruptcy_expense(expense_no);
CREATE INDEX idx_case_id ON tb_bankruptcy_expense(case_id);
CREATE INDEX idx_expense_type ON tb_bankruptcy_expense(expense_type);
CREATE INDEX idx_approval_status ON tb_bankruptcy_expense(approval_status);
CREATE INDEX idx_payment_status ON tb_bankruptcy_expense(payment_status);
CREATE INDEX idx_approver_id ON tb_bankruptcy_expense(approver_id);
CREATE INDEX idx_payment_date ON tb_bankruptcy_expense(payment_date);
CREATE INDEX idx_status ON tb_bankruptcy_expense(status);
CREATE INDEX idx_create_time ON tb_bankruptcy_expense(create_time);

-- ========================================
-- 共益债务管理表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_common_debt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debt_no VARCHAR(50) UNIQUE NOT NULL COMMENT '债务编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    debt_type VARCHAR(50) NOT NULL COMMENT '债务类型',
    debt_name VARCHAR(200) NOT NULL COMMENT '债务名称',
    debt_description TEXT COMMENT '债务描述',
    creditor_name VARCHAR(200) NOT NULL COMMENT '债权人名称',
    creditor_type VARCHAR(50) COMMENT '债权人类型',
    creditor_contact VARCHAR(100) COMMENT '债权人联系方式',
    debt_amount DECIMAL(18,2) NOT NULL COMMENT '债务金额',
    repaid_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '已清偿金额',
    unrepaid_amount DECIMAL(18,2) COMMENT '未清偿金额',
    debt_basis VARCHAR(50) COMMENT '债务依据',
    basis_document VARCHAR(200) COMMENT '依据文件',
    basis_description TEXT COMMENT '依据描述',
    debt_start_date TIMESTAMP NULL COMMENT '债务开始日期',
    debt_due_date TIMESTAMP NULL COMMENT '债务到期日期',
    is_overdue BOOLEAN DEFAULT FALSE COMMENT '是否逾期',
    approval_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已批准, REJECTED-已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    approval_date TIMESTAMP NULL COMMENT '审批日期',
    approval_opinion TEXT COMMENT '审批意见',
    repayment_status VARCHAR(20) DEFAULT 'UNREPAID' COMMENT '清偿状态: UNREPAID-未清偿, REPAID-已清偿, PARTIAL-部分清偿',
    repayment_method VARCHAR(50) COMMENT '清偿方式',
    repayment_date TIMESTAMP NULL COMMENT '清偿日期',
    repayment_account_id BIGINT COMMENT '清偿账户ID',
    repayment_voucher VARCHAR(200) COMMENT '清偿凭证',
    related_business_type VARCHAR(50) COMMENT '关联业务类型',
    related_business_id BIGINT COMMENT '关联业务ID',
    related_flow_id BIGINT COMMENT '关联流水ID',
    attachments TEXT COMMENT '附件',
    debt_date TIMESTAMP NULL COMMENT '债务发生日期',
    apply_date TIMESTAMP NULL COMMENT '申请日期',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共益债务管理表';

CREATE INDEX idx_debt_no ON tb_common_debt(debt_no);
CREATE INDEX idx_case_id ON tb_common_debt(case_id);
CREATE INDEX idx_debt_type ON tb_common_debt(debt_type);
CREATE INDEX idx_creditor_name ON tb_common_debt(creditor_name);
CREATE INDEX idx_approval_status ON tb_common_debt(approval_status);
CREATE INDEX idx_repayment_status ON tb_common_debt(repayment_status);
CREATE INDEX idx_approver_id ON tb_common_debt(approver_id);
CREATE INDEX idx_repayment_date ON tb_common_debt(repayment_date);
CREATE INDEX idx_status ON tb_common_debt(status);
CREATE INDEX idx_create_time ON tb_common_debt(create_time);

-- ========================================
-- 分配执行表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_distribution_execution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    distribution_no VARCHAR(50) UNIQUE NOT NULL COMMENT '分配编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    distribution_plan_id BIGINT COMMENT '分配计划ID',
    distribution_plan_name VARCHAR(200) COMMENT '分配计划名称',
    distribution_batch VARCHAR(50) NOT NULL COMMENT '分配批次',
    distribution_date TIMESTAMP NULL COMMENT '分配日期',
    distribution_method VARCHAR(50) COMMENT '分配方式',
    total_distributable_amount DECIMAL(18,2) NOT NULL COMMENT '可分配财产总额',
    total_distribution_amount DECIMAL(18,2) NOT NULL COMMENT '本次分配总额',
    accumulated_distribution_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '累计分配金额',
    expense_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '破产费用金额',
    expense_ratio DECIMAL(5,2) COMMENT '破产费用比例',
    expense_count INT DEFAULT 0 COMMENT '破产费用数量',
    common_debt_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '共益债务金额',
    common_debt_ratio DECIMAL(5,2) COMMENT '共益债务比例',
    common_debt_count INT DEFAULT 0 COMMENT '共益债务数量',
    employee_claim_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '职工债权金额',
    employee_claim_ratio DECIMAL(5,2) COMMENT '职工债权比例',
    employee_claim_count INT DEFAULT 0 COMMENT '职工债权数量',
    tax_claim_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '税务债权金额',
    tax_claim_ratio DECIMAL(5,2) COMMENT '税务债权比例',
    tax_claim_count INT DEFAULT 0 COMMENT '税务债权数量',
    secured_claim_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '担保债权金额',
    secured_claim_ratio DECIMAL(5,2) COMMENT '担保债权比例',
    secured_claim_count INT DEFAULT 0 COMMENT '担保债权数量',
    unsecured_claim_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '无担保债权金额',
    unsecured_claim_ratio DECIMAL(5,2) COMMENT '无担保债权比例',
    unsecured_claim_count INT DEFAULT 0 COMMENT '无担保债权数量',
    approval_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已批准, REJECTED-已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    approval_date TIMESTAMP NULL COMMENT '审批日期',
    approval_opinion TEXT COMMENT '审批意见',
    execution_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '执行状态: PENDING-待执行, EXECUTING-执行中, COMPLETED-已完成, CANCELLED-已取消',
    related_flow_id BIGINT COMMENT '关联流水ID',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分配执行表';

CREATE INDEX idx_distribution_no ON tb_distribution_execution(distribution_no);
CREATE INDEX idx_case_id ON tb_distribution_execution(case_id);
CREATE INDEX idx_distribution_batch ON tb_distribution_execution(distribution_batch);
CREATE INDEX idx_approval_status ON tb_distribution_execution(approval_status);
CREATE INDEX idx_execution_status ON tb_distribution_execution(execution_status);
CREATE INDEX idx_distribution_date ON tb_distribution_execution(distribution_date);
CREATE INDEX idx_status ON tb_distribution_execution(status);
CREATE INDEX idx_create_time ON tb_distribution_execution(create_time);

-- ========================================
-- 分配明细表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_distribution_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    distribution_execution_id BIGINT NOT NULL COMMENT '分配执行ID',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    creditor_claim_id BIGINT COMMENT '债权人债权ID',
    creditor_name VARCHAR(200) NOT NULL COMMENT '债权人名称',
    creditor_type VARCHAR(50) COMMENT '债权人类型',
    claim_amount DECIMAL(18,2) NOT NULL COMMENT '债权金额',
    confirmed_amount DECIMAL(18,2) NOT NULL COMMENT '确认债权金额',
    current_distribution_amount DECIMAL(18,2) NOT NULL COMMENT '本次分配金额',
    accumulated_distribution_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '累计分配金额',
    distribution_ratio DECIMAL(5,2) COMMENT '分配比例',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态: UNPAID-未支付, PAID-已支付, PARTIAL-部分支付',
    payment_date TIMESTAMP NULL COMMENT '支付日期',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_account_id BIGINT COMMENT '支付账户ID',
    payment_voucher VARCHAR(200) COMMENT '支付凭证',
    payee_account_name VARCHAR(200) COMMENT '收款账户名称',
    payee_account_number VARCHAR(100) COMMENT '收款账户号码',
    payee_bank_name VARCHAR(200) COMMENT '收款银行名称',
    is_escrowed BOOLEAN DEFAULT FALSE COMMENT '是否提存',
    escrow_id BIGINT COMMENT '提存ID',
    escrow_reason TEXT COMMENT '提存原因',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分配明细表';

CREATE INDEX idx_distribution_execution_id ON tb_distribution_detail(distribution_execution_id);
CREATE INDEX idx_case_id ON tb_distribution_detail(case_id);
CREATE INDEX idx_creditor_claim_id ON tb_distribution_detail(creditor_claim_id);
CREATE INDEX idx_creditor_name ON tb_distribution_detail(creditor_name);
CREATE INDEX idx_creditor_type ON tb_distribution_detail(creditor_type);
CREATE INDEX idx_payment_status ON tb_distribution_detail(payment_status);
CREATE INDEX idx_payment_date ON tb_distribution_detail(payment_date);
CREATE INDEX idx_status ON tb_distribution_detail(status);
CREATE INDEX idx_create_time ON tb_distribution_detail(create_time);

-- ========================================
-- 提存管理表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_escrow_management (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    escrow_no VARCHAR(50) UNIQUE NOT NULL COMMENT '提存编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    escrow_type VARCHAR(50) NOT NULL COMMENT '提存类型',
    escrow_name VARCHAR(200) NOT NULL COMMENT '提存名称',
    escrow_description TEXT COMMENT '提存描述',
    creditor_name VARCHAR(200) COMMENT '债权人名称',
    creditor_claim_id BIGINT COMMENT '债权人债权ID',
    escrow_amount DECIMAL(18,2) NOT NULL COMMENT '提存金额',
    released_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '已释放金额',
    unreleased_amount DECIMAL(18,2) COMMENT '未释放金额',
    escrow_reason TEXT COMMENT '提存原因',
    escrow_institution VARCHAR(200) COMMENT '提存机构',
    escrow_account VARCHAR(100) COMMENT '提存账户',
    escrow_date TIMESTAMP NULL COMMENT '提存日期',
    release_condition TEXT COMMENT '释放条件',
    is_condition_met BOOLEAN DEFAULT FALSE COMMENT '是否满足释放条件',
    condition_met_date TIMESTAMP NULL COMMENT '条件满足日期',
    release_status VARCHAR(20) DEFAULT 'UNRELEASED' COMMENT '释放状态: UNRELEASED-未释放, RELEASED-已释放, PARTIAL-部分释放',
    release_date TIMESTAMP NULL COMMENT '释放日期',
    release_account_id BIGINT COMMENT '释放账户ID',
    release_voucher VARCHAR(200) COMMENT '释放凭证',
    related_distribution_id BIGINT COMMENT '关联分配ID',
    related_flow_id BIGINT COMMENT '关联流水ID',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提存管理表';

CREATE INDEX idx_escrow_no ON tb_escrow_management(escrow_no);
CREATE INDEX idx_case_id ON tb_escrow_management(case_id);
CREATE INDEX idx_escrow_type ON tb_escrow_management(escrow_type);
CREATE INDEX idx_creditor_name ON tb_escrow_management(creditor_name);
CREATE INDEX idx_release_status ON tb_escrow_management(release_status);
CREATE INDEX idx_escrow_date ON tb_escrow_management(escrow_date);
CREATE INDEX idx_release_date ON tb_escrow_management(release_date);
CREATE INDEX idx_status ON tb_escrow_management(status);
CREATE INDEX idx_create_time ON tb_escrow_management(create_time);

-- ========================================
-- 资金预算表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_fund_budget (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    budget_no VARCHAR(50) UNIQUE NOT NULL COMMENT '预算编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    budget_type VARCHAR(50) NOT NULL COMMENT '预算类型',
    budget_name VARCHAR(200) NOT NULL COMMENT '预算名称',
    budget_description TEXT COMMENT '预算描述',
    budget_start_date TIMESTAMP NULL COMMENT '预算开始日期',
    budget_end_date TIMESTAMP NULL COMMENT '预算结束日期',
    total_budget_amount DECIMAL(18,2) NOT NULL COMMENT '预算总额',
    used_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '已使用金额',
    remaining_amount DECIMAL(18,2) COMMENT '剩余金额',
    litigation_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '诉讼费预算',
    arbitration_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '仲裁费预算',
    manager_fee_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '管理人报酬预算',
    execution_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '执行费预算',
    evaluation_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '评估费预算',
    auction_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '拍卖费预算',
    announcement_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '公告费预算',
    storage_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '保管费预算',
    insurance_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '保险费预算',
    meeting_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '会议费预算',
    other_budget DECIMAL(18,2) DEFAULT 0.00 COMMENT '其他费用预算',
    approval_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已批准, REJECTED-已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    approval_date TIMESTAMP NULL COMMENT '审批日期',
    approval_opinion TEXT COMMENT '审批意见',
    budget_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '预算状态: ACTIVE-有效, EXPIRED-已过期, CANCELLED-已取消',
    related_budget_id BIGINT COMMENT '关联预算ID',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金预算表';

CREATE INDEX idx_budget_no ON tb_fund_budget(budget_no);
CREATE INDEX idx_case_id ON tb_fund_budget(case_id);
CREATE INDEX idx_budget_type ON tb_fund_budget(budget_type);
CREATE INDEX idx_budget_status ON tb_fund_budget(budget_status);
CREATE INDEX idx_approval_status ON tb_fund_budget(approval_status);
CREATE INDEX idx_budget_start_date ON tb_fund_budget(budget_start_date);
CREATE INDEX idx_budget_end_date ON tb_fund_budget(budget_end_date);
CREATE INDEX idx_status ON tb_fund_budget(status);
CREATE INDEX idx_create_time ON tb_fund_budget(create_time);

-- ========================================
-- 费用报销表
-- ========================================
CREATE TABLE IF NOT EXISTS tb_fund_reimbursement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reimbursement_no VARCHAR(50) UNIQUE NOT NULL COMMENT '报销编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    reimbursement_type VARCHAR(50) NOT NULL COMMENT '报销类型',
    reimbursement_name VARCHAR(200) NOT NULL COMMENT '报销名称',
    reimbursement_description TEXT COMMENT '报销描述',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    applicant_name VARCHAR(100) NOT NULL COMMENT '申请人姓名',
    department VARCHAR(100) COMMENT '部门',
    applied_amount DECIMAL(18,2) NOT NULL COMMENT '申请金额',
    approved_amount DECIMAL(18,2) COMMENT '批准金额',
    reimbursed_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '已报销金额',
    expense_date TIMESTAMP NULL COMMENT '费用发生日期',
    expense_location VARCHAR(200) COMMENT '费用发生地点',
    expense_purpose TEXT COMMENT '费用用途',
    budget_id BIGINT COMMENT '预算ID',
    budget_item VARCHAR(50) COMMENT '预算项目',
    approval_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态: PENDING-待审批, APPROVED-已批准, REJECTED-已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    approval_date TIMESTAMP NULL COMMENT '审批日期',
    approval_opinion TEXT COMMENT '审批意见',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态: UNPAID-未支付, PAID-已支付, PARTIAL-部分支付',
    payment_date TIMESTAMP NULL COMMENT '支付日期',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_account_id BIGINT COMMENT '支付账户ID',
    payment_voucher VARCHAR(200) COMMENT '支付凭证',
    payee_account_name VARCHAR(200) COMMENT '收款账户名称',
    payee_account_number VARCHAR(100) COMMENT '收款账户号码',
    payee_bank_name VARCHAR(200) COMMENT '收款银行名称',
    related_expense_id BIGINT COMMENT '关联费用ID',
    related_flow_id BIGINT COMMENT '关联流水ID',
    attachments TEXT COMMENT '附件',
    apply_date TIMESTAMP NULL COMMENT '申请日期',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, CANCELLED-已取消',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用报销表';

CREATE INDEX idx_reimbursement_no ON tb_fund_reimbursement(reimbursement_no);
CREATE INDEX idx_case_id ON tb_fund_reimbursement(case_id);
CREATE INDEX idx_reimbursement_type ON tb_fund_reimbursement(reimbursement_type);
CREATE INDEX idx_applicant_id ON tb_fund_reimbursement(applicant_id);
CREATE INDEX idx_approval_status ON tb_fund_reimbursement(approval_status);
CREATE INDEX idx_payment_status ON tb_fund_reimbursement(payment_status);
CREATE INDEX idx_apply_date ON tb_fund_reimbursement(apply_date);
CREATE INDEX idx_status ON tb_fund_reimbursement(status);
CREATE INDEX idx_create_time ON tb_fund_reimbursement(create_time);

-- ========================================
-- 资产管理相关表
-- ========================================

-- 财产表
CREATE TABLE IF NOT EXISTS tb_property (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_no VARCHAR(50) UNIQUE NOT NULL COMMENT '财产编号',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    property_type VARCHAR(50) NOT NULL COMMENT '财产类型',
    property_name VARCHAR(200) NOT NULL COMMENT '财产名称',
    property_description TEXT COMMENT '财产描述',
    property_location VARCHAR(500) COMMENT '财产位置',
    property_address VARCHAR(500) COMMENT '财产地址',
    owner_name VARCHAR(200) NOT NULL COMMENT '所有权人名称',
    owner_type VARCHAR(50) COMMENT '所有权人类型',
    ownership_certificate VARCHAR(200) COMMENT '所有权证书',
    certificate_no VARCHAR(100) COMMENT '证书编号',
    registration_date TIMESTAMP NULL COMMENT '登记日期',
    acquisition_date TIMESTAMP NULL COMMENT '取得日期',
    acquisition_method VARCHAR(50) COMMENT '取得方式',
    acquisition_cost DECIMAL(18,2) COMMENT '取得成本',
    current_value DECIMAL(18,2) COMMENT '当前价值',
    valuation_date TIMESTAMP NULL COMMENT '评估日期',
    valuation_method VARCHAR(50) COMMENT '评估方法',
    valuation_institution VARCHAR(200) COMMENT '评估机构',
    valuation_report VARCHAR(200) COMMENT '评估报告',
    is_encumbered BOOLEAN DEFAULT FALSE COMMENT '是否有权利负担',
    encumbrance_type VARCHAR(50) COMMENT '权利负担类型',
    encumbrance_amount DECIMAL(18,2) COMMENT '权利负担金额',
    encumbrance_date TIMESTAMP NULL COMMENT '权利负担日期',
    property_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '财产状态',
    management_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '管理状态',
    custodian VARCHAR(100) COMMENT '保管人',
    custodian_contact VARCHAR(100) COMMENT '保管人联系方式',
    insurance_status VARCHAR(20) DEFAULT 'UNINSURED' COMMENT '保险状态',
    insurance_company VARCHAR(200) COMMENT '保险公司',
    insurance_policy_no VARCHAR(100) COMMENT '保险单号',
    insurance_amount DECIMAL(18,2) COMMENT '保险金额',
    insurance_start_date TIMESTAMP NULL COMMENT '保险开始日期',
    insurance_end_date TIMESTAMP NULL COMMENT '保险结束日期',
    related_debt_id BIGINT COMMENT '关联债务ID',
    related_claim_id BIGINT COMMENT '关联债权ID',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财产表';

CREATE INDEX idx_property_no ON tb_property(property_no);
CREATE INDEX idx_case_id ON tb_property(case_id);
CREATE INDEX idx_property_type ON tb_property(property_type);
CREATE INDEX idx_property_status ON tb_property(property_status);
CREATE INDEX idx_registration_date ON tb_property(registration_date);
CREATE INDEX idx_status ON tb_property(status);
CREATE INDEX idx_create_time ON tb_property(create_time);

-- 房产表
CREATE TABLE IF NOT EXISTS tb_real_estate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estate_no VARCHAR(50) UNIQUE NOT NULL COMMENT '房产编号',
    property_id BIGINT COMMENT '财产ID',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    estate_type VARCHAR(50) NOT NULL COMMENT '房产类型',
    estate_name VARCHAR(200) NOT NULL COMMENT '房产名称',
    estate_address VARCHAR(500) NOT NULL COMMENT '房产地址',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    street VARCHAR(200) COMMENT '街道',
    building_no VARCHAR(50) COMMENT '门牌号',
    unit_no VARCHAR(50) COMMENT '单元号',
    floor VARCHAR(20) COMMENT '楼层',
    room_no VARCHAR(50) COMMENT '房间号',
    land_use_type VARCHAR(50) COMMENT '土地用途',
    land_area DECIMAL(18,2) COMMENT '土地面积',
    building_area DECIMAL(18,2) COMMENT '建筑面积',
    usable_area DECIMAL(18,2) COMMENT '使用面积',
    construction_area DECIMAL(18,2) COMMENT '建筑总面积',
    building_structure VARCHAR(50) COMMENT '建筑结构',
    building_year INT COMMENT '建造年份',
    floors_above_ground INT COMMENT '地上层数',
    floors_below_ground INT COMMENT '地下层数',
    ownership_type VARCHAR(50) COMMENT '所有权类型',
    land_certificate_no VARCHAR(100) COMMENT '土地证号',
    property_certificate_no VARCHAR(100) COMMENT '房产证号',
    certificate_date TIMESTAMP NULL COMMENT '证书日期',
    registration_date TIMESTAMP NULL COMMENT '登记日期',
    acquisition_date TIMESTAMP NULL COMMENT '取得日期',
    acquisition_cost DECIMAL(18,2) COMMENT '取得成本',
    current_value DECIMAL(18,2) COMMENT '当前价值',
    valuation_date TIMESTAMP NULL COMMENT '评估日期',
    valuation_method VARCHAR(50) COMMENT '评估方法',
    valuation_institution VARCHAR(200) COMMENT '评估机构',
    valuation_report VARCHAR(200) COMMENT '评估报告',
    is_encumbered BOOLEAN DEFAULT FALSE COMMENT '是否有权利负担',
    encumbrance_type VARCHAR(50) COMMENT '权利负担类型',
    encumbrance_amount DECIMAL(18,2) COMMENT '权利负担金额',
    encumbrance_date TIMESTAMP NULL COMMENT '权利负担日期',
    estate_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '房产状态',
    management_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '管理状态',
    custodian VARCHAR(100) COMMENT '保管人',
    custodian_contact VARCHAR(100) COMMENT '保管人联系方式',
    insurance_status VARCHAR(20) DEFAULT 'UNINSURED' COMMENT '保险状态',
    insurance_company VARCHAR(200) COMMENT '保险公司',
    insurance_policy_no VARCHAR(100) COMMENT '保险单号',
    insurance_amount DECIMAL(18,2) COMMENT '保险金额',
    insurance_start_date TIMESTAMP NULL COMMENT '保险开始日期',
    insurance_end_date TIMESTAMP NULL COMMENT '保险结束日期',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房产表';

CREATE INDEX idx_estate_no ON tb_real_estate(estate_no);
CREATE INDEX idx_case_id ON tb_real_estate(case_id);
CREATE INDEX idx_estate_type ON tb_real_estate(estate_type);
CREATE INDEX idx_estate_status ON tb_real_estate(estate_status);
CREATE INDEX idx_status ON tb_real_estate(status);
CREATE INDEX idx_create_time ON tb_real_estate(create_time);

-- 车辆表
CREATE TABLE IF NOT EXISTS tb_vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_no VARCHAR(50) UNIQUE NOT NULL COMMENT '车辆编号',
    property_id BIGINT COMMENT '财产ID',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    vehicle_type VARCHAR(50) NOT NULL COMMENT '车辆类型',
    vehicle_name VARCHAR(200) NOT NULL COMMENT '车辆名称',
    vehicle_description TEXT COMMENT '车辆描述',
    license_plate VARCHAR(50) NOT NULL COMMENT '车牌号',
    vin VARCHAR(50) COMMENT '车辆识别代号',
    engine_no VARCHAR(50) COMMENT '发动机号',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    color VARCHAR(50) COMMENT '颜色',
    manufacture_date TIMESTAMP NULL COMMENT '制造日期',
    registration_date TIMESTAMP NULL COMMENT '登记日期',
    acquisition_date TIMESTAMP NULL COMMENT '取得日期',
    acquisition_cost DECIMAL(18,2) COMMENT '取得成本',
    current_value DECIMAL(18,2) COMMENT '当前价值',
    valuation_date TIMESTAMP NULL COMMENT '评估日期',
    valuation_method VARCHAR(50) COMMENT '评估方法',
    valuation_institution VARCHAR(200) COMMENT '评估机构',
    valuation_report VARCHAR(200) COMMENT '评估报告',
    mileage DECIMAL(18,2) COMMENT '里程',
    fuel_type VARCHAR(50) COMMENT '燃料类型',
    displacement DECIMAL(10,2) COMMENT '排量',
    seating_capacity INT COMMENT '座位数',
    load_capacity DECIMAL(18,2) COMMENT '载重',
    is_encumbered BOOLEAN DEFAULT FALSE COMMENT '是否有权利负担',
    encumbrance_type VARCHAR(50) COMMENT '权利负担类型',
    encumbrance_amount DECIMAL(18,2) COMMENT '权利负担金额',
    encumbrance_date TIMESTAMP NULL COMMENT '权利负担日期',
    vehicle_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '车辆状态',
    management_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '管理状态',
    custodian VARCHAR(100) COMMENT '保管人',
    custodian_contact VARCHAR(100) COMMENT '保管人联系方式',
    insurance_status VARCHAR(20) DEFAULT 'UNINSURED' COMMENT '保险状态',
    insurance_company VARCHAR(200) COMMENT '保险公司',
    insurance_policy_no VARCHAR(100) COMMENT '保险单号',
    insurance_amount DECIMAL(18,2) COMMENT '保险金额',
    insurance_start_date TIMESTAMP NULL COMMENT '保险开始日期',
    insurance_end_date TIMESTAMP NULL COMMENT '保险结束日期',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

CREATE INDEX idx_vehicle_no ON tb_vehicle(vehicle_no);
CREATE INDEX idx_case_id ON tb_vehicle(case_id);
CREATE INDEX idx_vehicle_type ON tb_vehicle(vehicle_type);
CREATE INDEX idx_vehicle_status ON tb_vehicle(vehicle_status);
CREATE INDEX idx_status ON tb_vehicle(status);
CREATE INDEX idx_create_time ON tb_vehicle(create_time);

-- 设备表
CREATE TABLE IF NOT EXISTS tb_equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_no VARCHAR(50) UNIQUE NOT NULL COMMENT '设备编号',
    property_id BIGINT COMMENT '财产ID',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    equipment_type VARCHAR(50) NOT NULL COMMENT '设备类型',
    equipment_name VARCHAR(200) NOT NULL COMMENT '设备名称',
    equipment_description TEXT COMMENT '设备描述',
    equipment_model VARCHAR(100) COMMENT '设备型号',
    equipment_spec VARCHAR(200) COMMENT '设备规格',
    brand VARCHAR(100) COMMENT '品牌',
    manufacturer VARCHAR(200) COMMENT '制造商',
    manufacture_date TIMESTAMP NULL COMMENT '制造日期',
    purchase_date TIMESTAMP NULL COMMENT '购买日期',
    purchase_cost DECIMAL(18,2) COMMENT '购买成本',
    current_value DECIMAL(18,2) COMMENT '当前价值',
    valuation_date TIMESTAMP NULL COMMENT '评估日期',
    valuation_method VARCHAR(50) COMMENT '评估方法',
    valuation_institution VARCHAR(200) COMMENT '评估机构',
    valuation_report VARCHAR(200) COMMENT '评估报告',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit VARCHAR(20) COMMENT '单位',
    `condition` VARCHAR(20) COMMENT '设备状况',
    location VARCHAR(500) COMMENT '存放位置',
    is_encumbered BOOLEAN DEFAULT FALSE COMMENT '是否有权利负担',
    encumbrance_type VARCHAR(50) COMMENT '权利负担类型',
    encumbrance_amount DECIMAL(18,2) COMMENT '权利负担金额',
    encumbrance_date TIMESTAMP NULL COMMENT '权利负担日期',
    equipment_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '设备状态',
    management_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '管理状态',
    custodian VARCHAR(100) COMMENT '保管人',
    custodian_contact VARCHAR(100) COMMENT '保管人联系方式',
    insurance_status VARCHAR(20) DEFAULT 'UNINSURED' COMMENT '保险状态',
    insurance_company VARCHAR(200) COMMENT '保险公司',
    insurance_policy_no VARCHAR(100) COMMENT '保险单号',
    insurance_amount DECIMAL(18,2) COMMENT '保险金额',
    insurance_start_date TIMESTAMP NULL COMMENT '保险开始日期',
    insurance_end_date TIMESTAMP NULL COMMENT '保险结束日期',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

CREATE INDEX idx_equipment_no ON tb_equipment(equipment_no);
CREATE INDEX idx_case_id ON tb_equipment(case_id);
CREATE INDEX idx_equipment_type ON tb_equipment(equipment_type);
CREATE INDEX idx_equipment_status ON tb_equipment(equipment_status);
CREATE INDEX idx_status ON tb_equipment(status);
CREATE INDEX idx_create_time ON tb_equipment(create_time);

-- 存货表
CREATE TABLE IF NOT EXISTS tb_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_no VARCHAR(50) UNIQUE NOT NULL COMMENT '存货编号',
    property_id BIGINT COMMENT '财产ID',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    inventory_type VARCHAR(50) NOT NULL COMMENT '存货类型',
    inventory_name VARCHAR(200) NOT NULL COMMENT '存货名称',
    inventory_description TEXT COMMENT '存货描述',
    category VARCHAR(50) COMMENT '分类',
    specification VARCHAR(200) COMMENT '规格型号',
    unit VARCHAR(20) COMMENT '单位',
    quantity DECIMAL(18,2) NOT NULL COMMENT '数量',
    unit_price DECIMAL(18,2) NOT NULL COMMENT '单价',
    total_value DECIMAL(18,2) NOT NULL COMMENT '总价值',
    current_quantity DECIMAL(18,2) COMMENT '当前数量',
    current_value DECIMAL(18,2) COMMENT '当前价值',
    valuation_date TIMESTAMP NULL COMMENT '评估日期',
    valuation_method VARCHAR(50) COMMENT '评估方法',
    valuation_institution VARCHAR(200) COMMENT '评估机构',
    valuation_report VARCHAR(200) COMMENT '评估报告',
    storage_location VARCHAR(500) COMMENT '存放位置',
    `condition` VARCHAR(20) COMMENT '存货状况',
    is_encumbered BOOLEAN DEFAULT FALSE COMMENT '是否有权利负担',
    encumbrance_type VARCHAR(50) COMMENT '权利负担类型',
    encumbrance_amount DECIMAL(18,2) COMMENT '权利负担金额',
    encumbrance_date TIMESTAMP NULL COMMENT '权利负担日期',
    inventory_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '存货状态',
    management_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '管理状态',
    custodian VARCHAR(100) COMMENT '保管人',
    custodian_contact VARCHAR(100) COMMENT '保管人联系方式',
    insurance_status VARCHAR(20) DEFAULT 'UNINSURED' COMMENT '保险状态',
    insurance_company VARCHAR(200) COMMENT '保险公司',
    insurance_policy_no VARCHAR(100) COMMENT '保险单号',
    insurance_amount DECIMAL(18,2) COMMENT '保险金额',
    insurance_start_date TIMESTAMP NULL COMMENT '保险开始日期',
    insurance_end_date TIMESTAMP NULL COMMENT '保险结束日期',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存货表';

CREATE INDEX idx_inventory_no ON tb_inventory(inventory_no);
CREATE INDEX idx_case_id ON tb_inventory(case_id);
CREATE INDEX idx_inventory_type ON tb_inventory(inventory_type);
CREATE INDEX idx_inventory_status ON tb_inventory(inventory_status);
CREATE INDEX idx_status ON tb_inventory(status);
CREATE INDEX idx_create_time ON tb_inventory(create_time);

-- 知识产权表
CREATE TABLE IF NOT EXISTS tb_intellectual_property (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_no VARCHAR(50) UNIQUE NOT NULL COMMENT '知识产权编号',
    property_id BIGINT COMMENT '财产ID',
    case_id BIGINT NOT NULL COMMENT '案件ID',
    case_name VARCHAR(200) NOT NULL COMMENT '案件名称',
    ip_type VARCHAR(50) NOT NULL COMMENT '知识产权类型',
    ip_name VARCHAR(200) NOT NULL COMMENT '知识产权名称',
    ip_description TEXT COMMENT '知识产权描述',
    registration_no VARCHAR(100) COMMENT '登记号',
    registration_date TIMESTAMP NULL COMMENT '登记日期',
    registration_authority VARCHAR(200) COMMENT '登记机关',
    expiry_date TIMESTAMP NULL COMMENT '有效期',
    registration_cost DECIMAL(18,2) COMMENT '登记成本',
    current_value DECIMAL(18,2) COMMENT '当前价值',
    valuation_date TIMESTAMP NULL COMMENT '评估日期',
    valuation_method VARCHAR(50) COMMENT '评估方法',
    valuation_institution VARCHAR(200) COMMENT '评估机构',
    valuation_report VARCHAR(200) COMMENT '评估报告',
    is_encumbered BOOLEAN DEFAULT FALSE COMMENT '是否有权利负担',
    encumbrance_type VARCHAR(50) COMMENT '权利负担类型',
    encumbrance_amount DECIMAL(18,2) COMMENT '权利负担金额',
    encumbrance_date TIMESTAMP NULL COMMENT '权利负担日期',
    ip_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '知识产权状态',
    management_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '管理状态',
    custodian VARCHAR(100) COMMENT '保管人',
    custodian_contact VARCHAR(100) COMMENT '保管人联系方式',
    insurance_status VARCHAR(20) DEFAULT 'UNINSURED' COMMENT '保险状态',
    insurance_company VARCHAR(200) COMMENT '保险公司',
    insurance_policy_no VARCHAR(100) COMMENT '保险单号',
    insurance_amount DECIMAL(18,2) COMMENT '保险金额',
    insurance_start_date TIMESTAMP NULL COMMENT '保险开始日期',
    insurance_end_date TIMESTAMP NULL COMMENT '保险结束日期',
    attachments TEXT COMMENT '附件',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user_id BIGINT COMMENT '创建人ID',
    update_user_id BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识产权表';

CREATE INDEX idx_ip_no ON tb_intellectual_property(ip_no);
CREATE INDEX idx_case_id ON tb_intellectual_property(case_id);
CREATE INDEX idx_ip_type ON tb_intellectual_property(ip_type);
CREATE INDEX idx_ip_status ON tb_intellectual_property(ip_status);
CREATE INDEX idx_status ON tb_intellectual_property(status);
CREATE INDEX idx_create_time ON tb_intellectual_property(create_time);

-- ========================================
-- 初始化数据
-- ========================================

-- 插入默认系统配置
INSERT INTO tb_system_config (config_key, config_value, config_desc, config_type, status) VALUES
('fund_budget_default_period', '12', '资金预算默认周期（月）', 'FUND', 'ACTIVE'),
('fund_approval_auto_approve', 'false', '是否自动审批小额费用', 'FUND', 'ACTIVE'),
('fund_reimbursement_max_amount', '50000', '费用报销最大金额', 'FUND', 'ACTIVE')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value), update_time = CURRENT_TIMESTAMP;

-- ========================================
-- 说明
-- ========================================
-- 本SQL文件包含资金模块和资产模块的所有数据表
-- 资金模块包含：
--   1. tb_bankruptcy_expense - 破产费用管理表
--   2. tb_common_debt - 共益债务管理表
--   3. tb_distribution_execution - 分配执行表
--   4. tb_distribution_detail - 分配明细表
--   5. tb_escrow_management - 提存管理表
--   6. tb_fund_budget - 资金预算表
--   7. tb_fund_reimbursement - 费用报销表
-- 资产模块包含：
--   8. tb_property - 财产表
--   9. tb_real_estate - 房产表
--   10. tb_vehicle - 车辆表
--   11. tb_equipment - 设备表
--   12. tb_inventory - 存货表
--   13. tb_intellectual_property - 知识产权表
-- 所有表都包含标准的审计字段：
--   - id: 主键
--   - is_deleted: 逻辑删除标记
--   - create_time: 创建时间
--   - update_time: 更新时间
--   - create_user_id: 创建人ID
--   - update_user_id: 更新人ID
--   - status: 状态
-- 所有金额字段使用 DECIMAL(18,2) 类型
-- 所有时间字段使用 TIMESTAMP 类型
-- 所有表使用 InnoDB 引擎，字符集为 utf8mb4
-- 所有表都创建了必要的索引以提高查询性能