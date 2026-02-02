package com.lawbackend2.lawbackend2.listener;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@HeadRowHeight(20)
@ContentRowHeight(18)
public class ClaimRegistrationExcelImportDTO {

    @ExcelProperty(value = "案件名称")
    private String caseName;

    @ExcelProperty(value = "债务人")
    private String debtor;

    @ExcelProperty(value = "债权人名称")
    private String creditorName;

    @ExcelProperty(value = "债权人类型")
    private String creditorType;

    @ExcelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ExcelProperty(value = "法定代表人")
    private String legalRepresentative;

    @ExcelProperty(value = "送达地址")
    private String serviceAddress;

    @ExcelProperty(value = "代理人姓名")
    private String agentName;

    @ExcelProperty(value = "代理人电话")
    private String agentPhone;

    @ExcelProperty(value = "代理人身份证")
    private String agentIdCard;

    @ExcelProperty(value = "代理人地址")
    private String agentAddress;

    @ExcelProperty(value = "账户名称")
    private String accountName;

    @ExcelProperty(value = "债权人银行账号")
    private String creditorBankAccount;

    @ExcelProperty(value = "开户行")
    private String bankName;

    @ExcelProperty(value = "本金")
    private BigDecimal principal;

    @ExcelProperty(value = "利息")
    private BigDecimal interest;

    @ExcelProperty(value = "违约金")
    private BigDecimal penalty;

    @ExcelProperty(value = "其他损失")
    private BigDecimal otherLosses;

    @ExcelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ExcelProperty(value = "是否有法院判决")
    private String hasCourtJudgment;

    @ExcelProperty(value = "是否有执行")
    private String hasExecution;

    @ExcelProperty(value = "是否有担保")
    private String hasCollateral;

    @ExcelProperty(value = "债权性质")
    private String claimNature;

    @ExcelProperty(value = "债权类型")
    private String claimType;

    @ExcelProperty(value = "债权事实")
    private String claimFacts;

    @ExcelProperty(value = "债权标识")
    private String claimIdentifier;

    @ExcelProperty(value = "证据清单")
    private String evidenceList;

    @ExcelProperty(value = "证据材料")
    private String evidenceMaterials;

    @ExcelProperty(value = "证据附件")
    private String evidenceAttachments;

    @ExcelProperty(value = "登记日期")
    private LocalDateTime registrationDate;

    @ExcelProperty(value = "登记截止日期")
    private LocalDateTime registrationDeadline;

    @ExcelProperty(value = "材料接收人")
    private String materialReceiver;

    @ExcelProperty(value = "材料接收日期")
    private LocalDateTime materialReceiveDate;

    @ExcelProperty(value = "材料完整性")
    private String materialCompleteness;

    @ExcelProperty(value = "备注")
    private String remarks;
}
