-- ========================================================
-- 破产案件法定节点模板初始化数据
-- 版本: 1.0
-- 日期: 2026-04-27
-- 描述: 初始化各类破产案件的法定节点模板数据
-- ========================================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- --------------------------------------------------------
-- 插入通用节点模板 (适用于所有案件类型)
-- --------------------------------------------------------
INSERT INTO `tb_case_node_template` (`node_code`, `node_name`, `node_description`, `case_type`, `case_stage`, `legal_deadline_days`, `legal_deadline_days_max`, `calculation_base`, `prev_node_code`, `responsible_role`, `sort_order`, `is_mandatory`, `status`) VALUES
('CLAIM_FILING_PERIOD', '债权申报期限', '法院受理破产申请后, 应当确定债权人申报债权的期限。债权申报期限自法院发布受理破产申请公告之日起计算, 最短不得少于三十日, 最长不得超过三个月。', 'COMMON', 'ACCEPTANCE', 30, 90, 'ANNOUNCEMENT_DATE', NULL, 'ADMINISTRATOR', 1, 1, 'ACTIVE'),
('FIRST_CREDITOR_MEETING', '第一次债权人会议', '第一次债权人会议由人民法院召集, 自债权申报期限届满之日起十五日内召开。', 'COMMON', 'ACCEPTANCE', 15, NULL, 'PREV_NODE_COMPLETE', 'CLAIM_FILING_PERIOD', 'COURT', 2, 1, 'ACTIVE'),
('TAKEOVER_DEBTOR', '全面接管债务人', '管理人应当接管债务人的财产、印章和账簿、文书等资料, 调查债务人财产状况, 制作财产状况报告。', 'COMMON', 'ACCEPTANCE', 7, NULL, 'ACCEPTANCE_DATE', NULL, 'ADMINISTRATOR', 3, 1, 'ACTIVE'),
('INVESTIGATE_PROPERTY', '调查财产及经营状况', '管理人应当对债务人的财产、债权债务、职工安置等情况进行全面调查, 并制作调查报告。', 'COMMON', 'ACCEPTANCE', 30, NULL, 'ACCEPTANCE_DATE', NULL, 'ADMINISTRATOR', 4, 1, 'ACTIVE'),
('CLAIM_REVIEW', '审查申报债权并编制债权表', '管理人应当对申报的债权进行审查, 编制债权表, 并提交第一次债权人会议核查。', 'COMMON', 'ACCEPTANCE', 15, NULL, 'PREV_NODE_COMPLETE', 'CLAIM_FILING_PERIOD', 'ADMINISTRATOR', 5, 1, 'ACTIVE');

-- --------------------------------------------------------
-- 插入清算案件专用节点模板
-- --------------------------------------------------------
INSERT INTO `tb_case_node_template` (`node_code`, `node_name`, `node_description`, `case_type`, `case_stage`, `legal_deadline_days`, `legal_deadline_days_max`, `calculation_base`, `prev_node_code`, `responsible_role`, `sort_order`, `is_mandatory`, `status`) VALUES
('LIQ_PROPERTY_VALUATION', '破产财产变价方案', '管理人应当及时拟订破产财产变价方案, 提交债权人会议讨论。变价出售破产财产应当通过拍卖进行, 但债权人会议另有决议的除外。', 'LIQUIDATION', 'LIQUIDATION', 30, NULL, 'MEETING_PASS_DATE', NULL, 'ADMINISTRATOR', 10, 1, 'ACTIVE'),
('LIQ_PROPERTY_DISTRIBUTION', '破产财产分配方案', '管理人应当及时拟订破产财产分配方案, 提交债权人会议讨论。破产财产分配方案应当载明参加分配的债权人姓名或者名称、住所, 参加分配的债权额, 可供分配的破产财产数额, 破产财产分配的顺序、比例及数额, 实施破产财产分配的方法。', 'LIQUIDATION', 'LIQUIDATION', 60, NULL, 'COMPLETION_DATE', 'LIQ_PROPERTY_VALUATION', 'ADMINISTRATOR', 11, 1, 'ACTIVE'),
('LIQ_TERMINATION', '提请终结破产程序', '管理人在最后分配完结后, 应当及时向人民法院提交破产财产分配报告, 并提请人民法院裁定终结破产程序。', 'LIQUIDATION', 'TERMINATION', 7, NULL, 'COMPLETION_DATE', 'LIQ_PROPERTY_DISTRIBUTION', 'ADMINISTRATOR', 20, 1, 'ACTIVE'),
('LIQ_ENTERPRISE_CANCELLATION', '办理企业注销登记', '自破产程序终结之日起十日内, 管理人应当持人民法院终结破产程序的裁定, 向破产人的原登记机关办理注销登记。', 'LIQUIDATION', 'TERMINATION', 10, NULL, 'PREV_NODE_COMPLETE', 'LIQ_TERMINATION', 'ADMINISTRATOR', 21, 1, 'ACTIVE'),
('LIQ_ARCHIVE', '管理人终止执行职务并归档', '于办理注销登记完毕的次日终止执行职务。但是, 存在诉讼或者仲裁未决情况的除外。', 'LIQUIDATION', 'TERMINATION', 1, NULL, 'PREV_NODE_COMPLETE', 'LIQ_ENTERPRISE_CANCELLATION', 'ADMINISTRATOR', 22, 1, 'ACTIVE');

-- --------------------------------------------------------
-- 插入重整案件专用节点模板
-- --------------------------------------------------------
INSERT INTO `tb_case_node_template` (`node_code`, `node_name`, `node_description`, `case_type`, `case_stage`, `legal_deadline_days`, `legal_deadline_days_max`, `calculation_base`, `prev_node_code`, `responsible_role`, `sort_order`, `is_mandatory`, `status`) VALUES
('REORG_PLAN_DRAFT', '重整计划草案提交', '债务人或者管理人应当自人民法院裁定债务人重整之日起六个月内, 同时向人民法院和债权人会议提交重整计划草案。前款规定的期限届满, 经债务人或者管理人请求, 有正当理由的, 人民法院可以裁定延期三个月。', 'REORGANIZATION', 'REORGANIZATION', 180, 270, 'REORGANIZATION_DATE', NULL, 'ADMINISTRATOR', 10, 1, 'ACTIVE'),
('REORG_PLAN_VOTE', '重整计划草案表决', '重整计划草案应当在债权人会议上进行表决, 由出席会议的同一表决组的债权人过半数同意, 并且其所代表的债权额占该组债权总额的三分之二以上的, 即为该组通过重整计划草案。', 'REORGANIZATION', 'REORGANIZATION', 15, NULL, 'PREV_NODE_COMPLETE', 'REORG_PLAN_DRAFT', 'CREDITOR_MEETING', 11, 1, 'ACTIVE'),
('REORG_COURT_APPROVAL', '法院批准重整计划', '自重整计划通过之日起十日内, 债务人或者管理人应当向人民法院提出批准重整计划的申请。人民法院经审查认为符合本法规定的, 应当自收到申请之日起三十日内裁定批准, 终止重整程序, 并予以公告。', 'REORGANIZATION', 'REORGANIZATION', 10, NULL, 'MEETING_PASS_DATE', NULL, 'COURT', 12, 1, 'ACTIVE'),
('REORG_SUPERVISION', '重整计划执行监督', '自人民法院裁定批准重整计划之日起, 在重整计划规定的监督期内, 由管理人监督重整计划的执行。监督期届满时, 管理人应当向人民法院提交监督报告。', 'REORGANIZATION', 'REORGANIZATION', 365, NULL, 'PREV_NODE_COMPLETE', 'REORG_COURT_APPROVAL', 'ADMINISTRATOR', 13, 0, 'ACTIVE');

-- --------------------------------------------------------
-- 插入和解案件专用节点模板
-- --------------------------------------------------------
INSERT INTO `tb_case_node_template` (`node_code`, `node_name`, `node_description`, `case_type`, `case_stage`, `legal_deadline_days`, `legal_deadline_days_max`, `calculation_base`, `prev_node_code`, `responsible_role`, `sort_order`, `is_mandatory`, `status`) VALUES
('COMP_DRAFT', '和解协议草案提交', '债务人申请和解, 应当提出和解协议草案。和解协议草案应当载明债务人的财产状况、债务清偿方案、债务减免方案等内容。', 'COMPROMISE', 'COMPROMISE', 30, NULL, 'ACCEPTANCE_DATE', NULL, 'APPLICANT', 10, 1, 'ACTIVE'),
('COMP_VOTE', '和解协议草案表决', '债权人会议通过和解协议的决议, 由出席会议的有表决权的债权人过半数同意, 并且其所代表的债权额占无财产担保债权总额的三分之二以上。', 'COMPROMISE', 'COMPROMISE', 15, NULL, 'PREV_NODE_COMPLETE', 'COMP_DRAFT', 'CREDITOR_MEETING', 11, 1, 'ACTIVE'),
('COMP_COURT_APPROVAL', '法院裁定认可和解协议', '债权人会议通过和解协议的, 由人民法院裁定认可, 终止和解程序, 并予以公告。', 'COMPROMISE', 'COMPROMISE', 10, NULL, 'MEETING_PASS_DATE', NULL, 'COURT', 12, 1, 'ACTIVE'),
('COMP_EXECUTION', '和解协议执行', '经人民法院裁定认可的和解协议, 对债务人和全体和解债权人均有约束力。债务人应当按照和解协议规定的条件清偿债务。', 'COMPROMISE', 'COMPROMISE', 180, NULL, 'PREV_NODE_COMPLETE', 'COMP_COURT_APPROVAL', 'ADMINISTRATOR', 13, 0, 'ACTIVE');

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
