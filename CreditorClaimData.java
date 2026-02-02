import com.alibaba.excel.annotation.ExcelProperty;

public class CreditorClaimData {

    @ExcelProperty("收件编号")
    private String receiptNumber;

    @ExcelProperty("债权人")
    private String creditor;

    @ExcelProperty("申报时间")
    private String declarationTime;

    @ExcelProperty("住所/邮编")
    private String address;

    @ExcelProperty("联系电话")
    private String contactPhone;

    @ExcelProperty("申报金额（元）")
    private String declarationAmount;

    @ExcelProperty("性质")
    private String nature;

    @ExcelProperty("法定代表人")
    private String legalRepresentative;

    @ExcelProperty("代理人")
    private String agent;

    @ExcelProperty("联系电话（代理人）")
    private String agentPhone;

    @ExcelProperty("债权性质")
    private String claimNature;

    @ExcelProperty("债权种类")
    private String claimType;

    @ExcelProperty("开户名")
    private String accountName;

    @ExcelProperty("开户行")
    private String bankName;

    @ExcelProperty("账号")
    private String accountNumber;

    @ExcelProperty("涉讼")
    private String litigation;

    @ExcelProperty("备注")
    private String remarks;

    // Getters and Setters
    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getDeclarationTime() {
        return declarationTime;
    }

    public void setDeclarationTime(String declarationTime) {
        this.declarationTime = declarationTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getDeclarationAmount() {
        return declarationAmount;
    }

    public void setDeclarationAmount(String declarationAmount) {
        this.declarationAmount = declarationAmount;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getLitigation() {
        return litigation;
    }

    public void setLitigation(String litigation) {
        this.litigation = litigation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
