-- H2 Database Schema for Testing

-- User table
CREATE TABLE IF NOT EXISTS tb_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  mobile VARCHAR(20),
  email VARCHAR(100),
  phone VARCHAR(20),
  is_valid CHAR(1) DEFAULT '1',
  status VARCHAR(20) DEFAULT 'ACTIVE',
  login_type CHAR(1) DEFAULT '1',
  bind_device VARCHAR(500),
  last_login_time TIMESTAMP,
  last_login_ip VARCHAR(50),
  login_count INT DEFAULT 0,
  pwd_error_count INT DEFAULT 0,
  pwd_error_time TIMESTAMP,
  pwd_expire_time TIMESTAMP,
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Role table
CREATE TABLE IF NOT EXISTS tb_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  role_name VARCHAR(100) NOT NULL,
  role_desc VARCHAR(500),
  is_system CHAR(1) DEFAULT '0',
  status VARCHAR(20) DEFAULT 'ACTIVE',
  sort_order INT DEFAULT 0,
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Permission table
CREATE TABLE IF NOT EXISTS tb_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  perm_code VARCHAR(100) NOT NULL UNIQUE,
  perm_name VARCHAR(100) NOT NULL,
  perm_type CHAR(1) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  path VARCHAR(200),
  component VARCHAR(200),
  icon VARCHAR(100),
  sort_order INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- BankruptCase table
CREATE TABLE IF NOT EXISTS tb_bankrupt_case (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  case_number VARCHAR(100) UNIQUE,
  case_name VARCHAR(255),
  acceptance_date DATE,
  case_source VARCHAR(100),
  acceptance_court VARCHAR(255),
  designated_institution VARCHAR(255),
  main_responsible_person VARCHAR(100),
  is_simplified_trial BOOLEAN DEFAULT FALSE,
  case_reason VARCHAR(255),
  case_progress VARCHAR(50),
  debt_claim_deadline TIMESTAMP,
  filing_date DATE,
  closing_date DATE,
  bankruptcy_date DATE,
  termination_date DATE,
  cancellation_date DATE,
  archiving_date DATE,
  remarks TEXT,
  file_upload_path VARCHAR(1000),
  undertaking_personnel VARCHAR(255),
  creator_id BIGINT,
  creator_name VARCHAR(100),
  reviewer_id BIGINT,
  review_status VARCHAR(20) DEFAULT 'PENDING',
  review_time TIMESTAMP,
  review_opinion TEXT,
  review_count INT DEFAULT 0,
  case_status VARCHAR(50) DEFAULT 'PENDING',
  designated_judge VARCHAR(100),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- FundAccount table
CREATE TABLE IF NOT EXISTS tb_fund_account (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id BIGINT,
  case_id BIGINT,
  case_name VARCHAR(200),
  account_name VARCHAR(100),
  account_type VARCHAR(50),
  initial_balance DECIMAL(18,2) DEFAULT 0,
  current_balance DECIMAL(18,2) DEFAULT 0,
  bank_name VARCHAR(100),
  bank_account VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- FundFlow table
CREATE TABLE IF NOT EXISTS tb_fund_flow (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  case_id BIGINT,
  case_name VARCHAR(200),
  account_id BIGINT,
  flow_type VARCHAR(20),
  amount DECIMAL(18,2),
  balance_before DECIMAL(18,2),
  balance_after DECIMAL(18,2),
  transaction_date TIMESTAMP,
  description VARCHAR(500),
  related_document VARCHAR(200),
  operator_id BIGINT,
  operation_time TIMESTAMP,
  remark VARCHAR(500),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- FundApproval table
CREATE TABLE IF NOT EXISTS tb_fund_approval (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  flow_id BIGINT,
  case_id BIGINT,
  amount DECIMAL(18,2),
  approval_status VARCHAR(20),
  approval_content TEXT,
  approver_id BIGINT,
  approval_time TIMESTAMP,
  approval_opinion VARCHAR(500),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- WorkPlan table
CREATE TABLE IF NOT EXISTS tb_work_plan (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  plan_number VARCHAR(100) UNIQUE,
  plan_type VARCHAR(50),
  plan_content TEXT,
  start_date DATE,
  end_date DATE,
  responsible_user_id BIGINT,
  responsible_user_name VARCHAR(100),
  execution_status VARCHAR(50) DEFAULT 'NOT_STARTED',
  case_id BIGINT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Todo table
CREATE TABLE IF NOT EXISTS tb_todo (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  user_account VARCHAR(50),
  user_name VARCHAR(100),
  title VARCHAR(200),
  description TEXT,
  type VARCHAR(50),
  priority VARCHAR(20) DEFAULT 'NORMAL',
  deadline TIMESTAMP,
  completed_time TIMESTAMP,
  status VARCHAR(20) DEFAULT 'PENDING',
  related_type VARCHAR(50),
  related_id BIGINT,
  assigned_user_id BIGINT,
  assigned_user_name VARCHAR(100),
  creator_id BIGINT,
  creator_name VARCHAR(100),
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Activity table
CREATE TABLE IF NOT EXISTS tb_activity (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  user_account VARCHAR(50),
  user_name VARCHAR(100),
  type VARCHAR(50),
  content TEXT,
  related_type VARCHAR(50),
  related_id BIGINT,
  ip_address VARCHAR(50),
  user_agent VARCHAR(500),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Notification table
CREATE TABLE IF NOT EXISTS tb_notification (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  user_account VARCHAR(50),
  user_name VARCHAR(100),
  title VARCHAR(200),
  content TEXT,
  type VARCHAR(50),
  is_read BOOLEAN DEFAULT FALSE,
  read_time TIMESTAMP,
  priority VARCHAR(20) DEFAULT 'NORMAL',
  related_type VARCHAR(50),
  related_id BIGINT,
  sender_id BIGINT,
  sender_name VARCHAR(100),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- LoginRecord table
CREATE TABLE IF NOT EXISTS tb_login_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  user_account VARCHAR(50),
  user_name VARCHAR(100),
  login_type VARCHAR(50),
  login_ip VARCHAR(50),
  login_location VARCHAR(100),
  login_device VARCHAR(100),
  login_browser VARCHAR(100),
  login_os VARCHAR(100),
  login_status VARCHAR(20),
  error_msg VARCHAR(500),
  risk_level VARCHAR(20),
  is_known_device CHAR(1),
  login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- SystemConfig table
CREATE TABLE IF NOT EXISTS tb_system_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_key VARCHAR(100) UNIQUE,
  config_value TEXT,
  config_desc VARCHAR(500),
  config_type VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- SmsCode table
CREATE TABLE IF NOT EXISTS tb_sms_code (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  mobile VARCHAR(20),
  code VARCHAR(10),
  type VARCHAR(50),
  expire_time TIMESTAMP,
  is_used BOOLEAN DEFAULT FALSE,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- WorkTeam table
CREATE TABLE IF NOT EXISTS tb_work_team (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  team_name VARCHAR(100),
  team_leader_id BIGINT,
  case_id BIGINT,
  team_description TEXT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- WorkTeamMember table
CREATE TABLE IF NOT EXISTS tb_work_team_member (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  team_id BIGINT,
  user_id BIGINT,
  user_name VARCHAR(100),
  role VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- WorkTeamPermission table
CREATE TABLE IF NOT EXISTS tb_work_team_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  team_id BIGINT,
  user_id BIGINT,
  permission_code VARCHAR(100),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- FileRecord table
CREATE TABLE IF NOT EXISTS tb_file_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  file_name VARCHAR(255),
  file_path VARCHAR(500),
  file_size BIGINT,
  file_type VARCHAR(50),
  upload_user_id BIGINT,
  upload_user_name VARCHAR(100),
  related_type VARCHAR(50),
  related_id BIGINT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Administrator table
CREATE TABLE IF NOT EXISTS tb_administrator (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  admin_number VARCHAR(50) UNIQUE,
  admin_name VARCHAR(100),
  admin_type VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- AdministratorStaff table
CREATE TABLE IF NOT EXISTS tb_administrator_staff (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  administrator_id BIGINT,
  staff_name VARCHAR(100),
  staff_type VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- AnnouncementViewRecord table
CREATE TABLE IF NOT EXISTS tb_case_announcement_view (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  announcement_id BIGINT,
  case_id BIGINT,
  viewer_id BIGINT,
  viewer_name VARCHAR(100),
  view_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ip_address VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- CaseAnnouncement table
CREATE TABLE IF NOT EXISTS tb_case_announcement (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  case_id BIGINT,
  announcement_number VARCHAR(100) UNIQUE,
  announcement_title VARCHAR(255),
  announcement_content TEXT,
  publish_status VARCHAR(20) DEFAULT 'DRAFT',
  publish_time TIMESTAMP,
  publisher_id BIGINT,
  publisher_name VARCHAR(100),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- CaseProgress table
CREATE TABLE IF NOT EXISTS tb_case_progress (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  case_id BIGINT,
  progress_number VARCHAR(100) UNIQUE,
  progress_type VARCHAR(50),
  progress_content TEXT,
  progress_date TIMESTAMP,
  operator_id BIGINT,
  operator_name VARCHAR(100),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Court table
CREATE TABLE IF NOT EXISTS tb_court (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  court_name VARCHAR(255),
  court_code VARCHAR(50) UNIQUE,
  court_level VARCHAR(50),
  court_address VARCHAR(500),
  contact_person VARCHAR(100),
  contact_phone VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- CreditorInfo table
CREATE TABLE IF NOT EXISTS tb_creditor_info (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  creditor_number VARCHAR(50) UNIQUE,
  creditor_name VARCHAR(255),
  creditor_type VARCHAR(50),
  id_card VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(500),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- CreditorClaim table
CREATE TABLE IF NOT EXISTS tb_creditor_claim (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_number VARCHAR(50) UNIQUE,
  case_id BIGINT,
  creditor_id BIGINT,
  claim_amount DECIMAL(18,2),
  claim_type VARCHAR(50),
  claim_reason TEXT,
  evidence_files VARCHAR(1000),
  review_status VARCHAR(20) DEFAULT 'PENDING',
  review_time TIMESTAMP,
  reviewer_id BIGINT,
  reviewer_name VARCHAR(100),
  review_opinion TEXT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- DebtorEnterprise table
CREATE TABLE IF NOT EXISTS tb_debtor_enterprise (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  enterprise_number VARCHAR(50) UNIQUE,
  enterprise_name VARCHAR(255),
  unified_social_credit_code VARCHAR(50),
  legal_representative VARCHAR(100),
  registered_address VARCHAR(500),
  business_scope TEXT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- BankAccount table
CREATE TABLE IF NOT EXISTS tb_bank_account (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_number VARCHAR(50) UNIQUE,
  account_name VARCHAR(100),
  bank_name VARCHAR(100),
  account_type VARCHAR(50),
  balance DECIMAL(18,2),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- Token table
CREATE TABLE IF NOT EXISTS tb_token (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(500),
  user_id BIGINT,
  username VARCHAR(50),
  expire_time TIMESTAMP,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);

-- LoginFail table
CREATE TABLE IF NOT EXISTS tb_login_fail (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_account VARCHAR(50),
  fail_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fail_reason VARCHAR(500),
  ip_address VARCHAR(50),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  is_deleted BOOLEAN DEFAULT FALSE,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_user_id BIGINT,
  update_user_id BIGINT
);