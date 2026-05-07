package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.CaseNodeTemplate;
import com.lawbackend2.lawbackend2.enums.CaseStage;
import com.lawbackend2.lawbackend2.enums.CaseType;
import com.lawbackend2.lawbackend2.enums.CalculationBase;
import com.lawbackend2.lawbackend2.enums.ResponsibleRole;
import com.lawbackend2.lawbackend2.repository.CaseNodeTemplateRepository;
import com.lawbackend2.lawbackend2.service.CaseNodeTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CaseNodeTemplateServiceImpl implements CaseNodeTemplateService {

    private final CaseNodeTemplateRepository caseNodeTemplateRepository;

    public CaseNodeTemplateServiceImpl(CaseNodeTemplateRepository caseNodeTemplateRepository) {
        this.caseNodeTemplateRepository = caseNodeTemplateRepository;
    }

    @Override
    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void initDefaultTemplates() {
        log.info("开始初始化案件节点模板数据");

        long count = caseNodeTemplateRepository.count();
        if (count > 0) {
            log.info("节点模板已存在, 跳过初始化, 当前数量: {}", count);
            return;
        }

        List<CaseNodeTemplate> templates = new ArrayList<>();

        // ==================== 通用节点 (所有案件类型共用) ====================
        templates.add(createTemplate("CLAIM_FILING_PERIOD", "债权申报期限",
                "法院受理破产申请后, 应当确定债权人申报债权的期限。债权申报期限自法院发布受理破产申请公告之日起计算, 最短不得少于三十日, 最长不得超过三个月。",
                CaseType.COMMON.name(), CaseStage.ACCEPTANCE.name(), 30, 90,
                CalculationBase.ANNOUNCEMENT_DATE.name(), null, ResponsibleRole.ADMINISTRATOR.name(), 1, true));

        templates.add(createTemplate("FIRST_CREDITOR_MEETING", "第一次债权人会议",
                "第一次债权人会议由人民法院召集, 自债权申报期限届满之日起十五日内召开。",
                CaseType.COMMON.name(), CaseStage.ACCEPTANCE.name(), 15, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "CLAIM_FILING_PERIOD", ResponsibleRole.COURT.name(), 2, true));

        templates.add(createTemplate("TAKEOVER_DEBTOR", "全面接管债务人",
                "管理人应当接管债务人的财产、印章和账簿、文书等资料, 调查债务人财产状况, 制作财产状况报告。",
                CaseType.COMMON.name(), CaseStage.ACCEPTANCE.name(), 7, null,
                CalculationBase.ACCEPTANCE_DATE.name(), null, ResponsibleRole.ADMINISTRATOR.name(), 3, true));

        templates.add(createTemplate("INVESTIGATE_PROPERTY", "调查财产及经营状况",
                "管理人应当对债务人的财产、债权债务、职工安置等情况进行全面调查, 并制作调查报告。",
                CaseType.COMMON.name(), CaseStage.ACCEPTANCE.name(), 30, null,
                CalculationBase.ACCEPTANCE_DATE.name(), null, ResponsibleRole.ADMINISTRATOR.name(), 4, true));

        templates.add(createTemplate("CLAIM_REVIEW", "审查申报债权并编制债权表",
                "管理人应当对申报的债权进行审查, 编制债权表, 并提交第一次债权人会议核查。",
                CaseType.COMMON.name(), CaseStage.ACCEPTANCE.name(), 15, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "CLAIM_FILING_PERIOD", ResponsibleRole.ADMINISTRATOR.name(), 5, true));

        // ==================== 清算案件专用节点 ====================
        templates.add(createTemplate("LIQ_PROPERTY_VALUATION", "破产财产变价方案",
                "管理人应当及时拟订破产财产变价方案, 提交债权人会议讨论。变价出售破产财产应当通过拍卖进行, 但债权人会议另有决议的除外。",
                CaseType.LIQUIDATION.name(), CaseStage.LIQUIDATION.name(), 30, null,
                CalculationBase.MEETING_PASS_DATE.name(), null, ResponsibleRole.ADMINISTRATOR.name(), 10, true));

        templates.add(createTemplate("LIQ_PROPERTY_DISTRIBUTION", "破产财产分配方案",
                "管理人应当及时拟订破产财产分配方案, 提交债权人会议讨论。破产财产分配方案应当载明参加分配的债权人姓名或者名称、住所, 参加分配的债权额, 可供分配的破产财产数额, 破产财产分配的顺序、比例及数额, 实施破产财产分配的方法。",
                CaseType.LIQUIDATION.name(), CaseStage.LIQUIDATION.name(), 60, null,
                CalculationBase.COMPLETION_DATE.name(), "LIQ_PROPERTY_VALUATION", ResponsibleRole.ADMINISTRATOR.name(), 11, true));

        templates.add(createTemplate("LIQ_TERMINATION", "提请终结破产程序",
                "管理人在最后分配完结后, 应当及时向人民法院提交破产财产分配报告, 并提请人民法院裁定终结破产程序。",
                CaseType.LIQUIDATION.name(), CaseStage.TERMINATION.name(), 7, null,
                CalculationBase.COMPLETION_DATE.name(), "LIQ_PROPERTY_DISTRIBUTION", ResponsibleRole.ADMINISTRATOR.name(), 20, true));

        templates.add(createTemplate("LIQ_ENTERPRISE_CANCELLATION", "办理企业注销登记",
                "自破产程序终结之日起十日内, 管理人应当持人民法院终结破产程序的裁定, 向破产人的原登记机关办理注销登记。",
                CaseType.LIQUIDATION.name(), CaseStage.TERMINATION.name(), 10, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "LIQ_TERMINATION", ResponsibleRole.ADMINISTRATOR.name(), 21, true));

        templates.add(createTemplate("LIQ_ARCHIVE", "管理人终止执行职务并归档",
                "于办理注销登记完毕的次日终止执行职务。但是, 存在诉讼或者仲裁未决情况的除外。",
                CaseType.LIQUIDATION.name(), CaseStage.TERMINATION.name(), 1, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "LIQ_ENTERPRISE_CANCELLATION", ResponsibleRole.ADMINISTRATOR.name(), 22, true));

        // ==================== 重整案件专用节点 ====================
        templates.add(createTemplate("REORG_PLAN_DRAFT", "重整计划草案提交",
                "债务人或者管理人应当自人民法院裁定债务人重整之日起六个月内, 同时向人民法院和债权人会议提交重整计划草案。前款规定的期限届满, 经债务人或者管理人请求, 有正当理由的, 人民法院可以裁定延期三个月。",
                CaseType.REORGANIZATION.name(), CaseStage.REORGANIZATION.name(), 180, 270,
                CalculationBase.REORGANIZATION_DATE.name(), null, ResponsibleRole.ADMINISTRATOR.name(), 10, true));

        templates.add(createTemplate("REORG_PLAN_VOTE", "重整计划草案表决",
                "重整计划草案应当在债权人会议上进行表决, 由出席会议的同一表决组的债权人过半数同意, 并且其所代表的债权额占该组债权总额的三分之二以上的, 即为该组通过重整计划草案。",
                CaseType.REORGANIZATION.name(), CaseStage.REORGANIZATION.name(), 15, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "REORG_PLAN_DRAFT", ResponsibleRole.CREDITOR_MEETING.name(), 11, true));

        templates.add(createTemplate("REORG_COURT_APPROVAL", "法院批准重整计划",
                "自重整计划通过之日起十日内, 债务人或者管理人应当向人民法院提出批准重整计划的申请。人民法院经审查认为符合本法规定的, 应当自收到申请之日起三十日内裁定批准, 终止重整程序, 并予以公告。",
                CaseType.REORGANIZATION.name(), CaseStage.REORGANIZATION.name(), 10, null,
                CalculationBase.MEETING_PASS_DATE.name(), null, ResponsibleRole.COURT.name(), 12, true));

        templates.add(createTemplate("REORG_SUPERVISION", "重整计划执行监督",
                "自人民法院裁定批准重整计划之日起, 在重整计划规定的监督期内, 由管理人监督重整计划的执行。监督期届满时, 管理人应当向人民法院提交监督报告。",
                CaseType.REORGANIZATION.name(), CaseStage.REORGANIZATION.name(), 365, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "REORG_COURT_APPROVAL", ResponsibleRole.ADMINISTRATOR.name(), 13, false));

        // ==================== 和解案件专用节点 ====================
        templates.add(createTemplate("COMP_DRAFT", "和解协议草案提交",
                "债务人申请和解, 应当提出和解协议草案。和解协议草案应当载明债务人的财产状况、债务清偿方案、债务减免方案等内容。",
                CaseType.COMPROMISE.name(), CaseStage.COMPROMISE.name(), 30, null,
                CalculationBase.ACCEPTANCE_DATE.name(), null, ResponsibleRole.APPLICANT.name(), 10, true));

        templates.add(createTemplate("COMP_VOTE", "和解协议草案表决",
                "债权人会议通过和解协议的决议, 由出席会议的有表决权的债权人过半数同意, 并且其所代表的债权额占无财产担保债权总额的三分之二以上。",
                CaseType.COMPROMISE.name(), CaseStage.COMPROMISE.name(), 15, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "COMP_DRAFT", ResponsibleRole.CREDITOR_MEETING.name(), 11, true));

        templates.add(createTemplate("COMP_COURT_APPROVAL", "法院裁定认可和解协议",
                "债权人会议通过和解协议的, 由人民法院裁定认可, 终止和解程序, 并予以公告。",
                CaseType.COMPROMISE.name(), CaseStage.COMPROMISE.name(), 10, null,
                CalculationBase.MEETING_PASS_DATE.name(), null, ResponsibleRole.COURT.name(), 12, true));

        templates.add(createTemplate("COMP_EXECUTION", "和解协议执行",
                "经人民法院裁定认可的和解协议, 对债务人和全体和解债权人均有约束力。债务人应当按照和解协议规定的条件清偿债务。",
                CaseType.COMPROMISE.name(), CaseStage.COMPROMISE.name(), 180, null,
                CalculationBase.PREV_NODE_COMPLETE.name(), "COMP_COURT_APPROVAL", ResponsibleRole.ADMINISTRATOR.name(), 13, false));

        caseNodeTemplateRepository.saveAll(templates);
        log.info("节点模板初始化完成, 共创建 {} 个模板", templates.size());
    }

    private CaseNodeTemplate createTemplate(String nodeCode, String nodeName, String description,
                                             String caseType, String caseStage, Integer legalDays, Integer legalDaysMax,
                                             String calculationBase, String prevNodeCode, String responsibleRole,
                                             Integer sortOrder, Boolean isMandatory) {
        CaseNodeTemplate template = new CaseNodeTemplate();
        template.setNodeCode(nodeCode);
        template.setNodeName(nodeName);
        template.setNodeDescription(description);
        template.setCaseType(caseType);
        template.setCaseStage(caseStage);
        template.setLegalDeadlineDays(legalDays);
        template.setLegalDeadlineDaysMax(legalDaysMax);
        template.setCalculationBase(calculationBase);
        template.setPrevNodeCode(prevNodeCode);
        template.setResponsibleRole(responsibleRole);
        template.setSortOrder(sortOrder);
        template.setIsMandatory(isMandatory);
        return template;
    }

    @Override
    public List<CaseNodeTemplate> getTemplatesByCaseType(String caseType) {
        List<CaseNodeTemplate> templates = new ArrayList<>();
        templates.addAll(caseNodeTemplateRepository.findActiveByCaseTypeOrderBySortOrder(CaseType.COMMON.name()));
        if (!CaseType.COMMON.name().equals(caseType)) {
            templates.addAll(caseNodeTemplateRepository.findActiveByCaseTypeOrderBySortOrder(caseType));
        }
        templates.sort((a, b) -> {
            int stageCompare = a.getCaseStage().compareTo(b.getCaseStage());
            if (stageCompare != 0) return stageCompare;
            return a.getSortOrder().compareTo(b.getSortOrder());
        });
        return templates;
    }

    @Override
    public List<CaseNodeTemplate> getAllActiveTemplates() {
        return caseNodeTemplateRepository.findAllActiveOrderByCaseTypeAndSortOrder();
    }

    @Override
    public CaseNodeTemplate getTemplateById(Long id) {
        return caseNodeTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("节点模板不存在, id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeTemplate createTemplate(CaseNodeTemplate template) {
        Optional<CaseNodeTemplate> existing = caseNodeTemplateRepository.findByNodeCodeAndCaseType(
                template.getNodeCode(), template.getCaseType());
        if (existing.isPresent()) {
            throw new RuntimeException("该案件类型下已存在相同编码的节点模板: " + template.getNodeCode());
        }
        return caseNodeTemplateRepository.save(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeTemplate updateTemplate(Long id, CaseNodeTemplate template) {
        CaseNodeTemplate existing = getTemplateById(id);
        existing.setNodeName(template.getNodeName());
        existing.setNodeDescription(template.getNodeDescription());
        existing.setLegalDeadlineDays(template.getLegalDeadlineDays());
        existing.setLegalDeadlineDaysMax(template.getLegalDeadlineDaysMax());
        existing.setCalculationBase(template.getCalculationBase());
        existing.setPrevNodeCode(template.getPrevNodeCode());
        existing.setResponsibleRole(template.getResponsibleRole());
        existing.setSortOrder(template.getSortOrder());
        existing.setIsMandatory(template.getIsMandatory());
        return caseNodeTemplateRepository.save(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        CaseNodeTemplate template = getTemplateById(id);
        template.setIsDeleted(true);
        template.setStatus("DELETED");
        caseNodeTemplateRepository.save(template);
    }
}
