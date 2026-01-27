package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class CaseRelatedDataResponse {
    
    private CaseInfo caseInfo;
    
    private ApprovalData approvalData;
    
    private ProcessData processData;
    
    private DocumentData documentData;
    
    private ArchiveData archiveData;
    
    private AnnouncementData announcementData;
    
    private FundData fundData;
    
    private DistributionData distributionData;
    
    private DebtData debtData;
    
    private ClaimData claimData;
    
    private WorkData workData;
    
    private EnterpriseData enterpriseData;
    
    private TaskData taskData;
    
    private AccountData accountData;
    
    @Data
    public static class CaseInfo {
        private Long id;
        private String caseNumber;
        private String caseName;
        private String caseStatus;
    }
    
    @Data
    public static class ApprovalData {
        private Integer approvalCount;
        private Integer approvalHistoryCount;
    }
    
    @Data
    public static class ProcessData {
        private Integer processStageCount;
    }
    
    @Data
    public static class DocumentData {
        private Integer documentDeliveryCount;
    }
    
    @Data
    public static class ArchiveData {
        private Integer archiveRecordCount;
    }
    
    @Data
    public static class AnnouncementData {
        private Integer announcementCount;
        private Integer announcementViewCount;
    }
    
    @Data
    public static class FundData {
        private Integer fundReimbursementCount;
        private Integer fundFlowCount;
        private Integer fundOperationLogCount;
        private Integer fundBudgetCount;
        private Integer escrowManagementCount;
        private Integer fundAccountCount;
        private Integer fundApprovalCount;
        private Integer bankruptcyExpenseCount;
    }
    
    @Data
    public static class DistributionData {
        private Integer distributionDetailCount;
        private Integer distributionExecutionCount;
    }
    
    @Data
    public static class DebtData {
        private Integer commonDebtCount;
    }
    
    @Data
    public static class ClaimData {
        private Integer claimConfirmationCount;
        private Integer creditorClaimCount;
        private Integer creditorInfoCount;
        private Integer claimRegistrationCount;
        private Integer claimReviewCount;
    }
    
    @Data
    public static class WorkData {
        private Integer administratorCount;
        private Integer workTeamCount;
        private Integer workPlanCount;
        private Integer workLogCount;
        private Integer caseProgressCount;
    }
    
    @Data
    public static class EnterpriseData {
        private Integer debtorEnterpriseCount;
    }
    
    @Data
    public static class TaskData {
        private Integer caseTaskCount;
        private Integer caseTaskSubmissionCount;
    }
    
    @Data
    public static class AccountData {
        private Integer bankAccountCount;
    }
}
