package com.lawbackend2.lawbackend2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClaimRegistrationUpdateRequest {
    private String creditorName;

    private String creditorType;

    private String creditCode;

    private String legalRepresentative;

    private String serviceAddress;

    private String agentName;

    private String agentPhone;

    private String agentIdCard;

    private String agentAddress;

    private String accountName;

    private String creditorBankAccount;

    private String bankName;

    private BigDecimal principal;

    private BigDecimal interest;

    private BigDecimal penalty;

    private BigDecimal otherLosses;

    private BigDecimal totalAmount;

    private Integer hasCourtJudgment;

    private Integer hasExecution;

    private Integer hasCollateral;

    private String claimNature;

    private String claimType;

    private String claimFacts;

    private String claimIdentifier;

    private String evidenceList;

    private String evidenceMaterials;

    private String evidenceAttachments;

    private LocalDateTime registrationDate;

    private LocalDateTime registrationDeadline;

    private String materialReceiver;

    private LocalDateTime materialReceiveDate;

    private String materialCompleteness;

    private String registrationStatus;

    private String remarks;

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public String getCreditorType() {
        return creditorType;
    }

    public void setCreditorType(String creditorType) {
        this.creditorType = creditorType;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getAgentIdCard() {
        return agentIdCard;
    }

    public void setAgentIdCard(String agentIdCard) {
        this.agentIdCard = agentIdCard;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCreditorBankAccount() {
        return creditorBankAccount;
    }

    public void setCreditorBankAccount(String creditorBankAccount) {
        this.creditorBankAccount = creditorBankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }

    public BigDecimal getOtherLosses() {
        return otherLosses;
    }

    public void setOtherLosses(BigDecimal otherLosses) {
        this.otherLosses = otherLosses;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getHasCourtJudgment() {
        return hasCourtJudgment;
    }

    public void setHasCourtJudgment(Integer hasCourtJudgment) {
        this.hasCourtJudgment = hasCourtJudgment;
    }

    public Integer getHasExecution() {
        return hasExecution;
    }

    public void setHasExecution(Integer hasExecution) {
        this.hasExecution = hasExecution;
    }

    public Integer getHasCollateral() {
        return hasCollateral;
    }

    public void setHasCollateral(Integer hasCollateral) {
        this.hasCollateral = hasCollateral;
    }

    public String getClaimNature() {
        return claimNature;
    }

    public void setClaimNature(String claimNature) {
        this.claimNature = claimNature;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getClaimFacts() {
        return claimFacts;
    }

    public void setClaimFacts(String claimFacts) {
        this.claimFacts = claimFacts;
    }

    public String getClaimIdentifier() {
        return claimIdentifier;
    }

    public void setClaimIdentifier(String claimIdentifier) {
        this.claimIdentifier = claimIdentifier;
    }

    public String getEvidenceList() {
        return evidenceList;
    }

    public void setEvidenceList(String evidenceList) {
        this.evidenceList = evidenceList;
    }

    public String getEvidenceMaterials() {
        return evidenceMaterials;
    }

    public void setEvidenceMaterials(String evidenceMaterials) {
        this.evidenceMaterials = evidenceMaterials;
    }

    public String getEvidenceAttachments() {
        return evidenceAttachments;
    }

    public void setEvidenceAttachments(String evidenceAttachments) {
        this.evidenceAttachments = evidenceAttachments;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public String getMaterialReceiver() {
        return materialReceiver;
    }

    public void setMaterialReceiver(String materialReceiver) {
        this.materialReceiver = materialReceiver;
    }

    public LocalDateTime getMaterialReceiveDate() {
        return materialReceiveDate;
    }

    public void setMaterialReceiveDate(LocalDateTime materialReceiveDate) {
        this.materialReceiveDate = materialReceiveDate;
    }

    public String getMaterialCompleteness() {
        return materialCompleteness;
    }

    public void setMaterialCompleteness(String materialCompleteness) {
        this.materialCompleteness = materialCompleteness;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
