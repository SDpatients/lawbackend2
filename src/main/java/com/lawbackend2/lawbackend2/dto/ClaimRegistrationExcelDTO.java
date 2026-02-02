package com.lawbackend2.lawbackend2.dto;

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
public class ClaimRegistrationExcelDTO {

    @ExcelProperty(value = "案件名称", index = 0)
    @ColumnWidth(20)
    private String caseName;

    @ExcelProperty(value = "债务人", index = 1)
    @ColumnWidth(20)
    private String debtor;

    @ExcelProperty(value = "债权人名称", index = 2)
    @ColumnWidth(20)
    private String creditorName;

    @ExcelProperty(value = "债权人类型", index = 3)
    @ColumnWidth(15)
    private String creditorType;

    @ExcelProperty(value = "统一社会信用代码", index = 4)
    @ColumnWidth(25)
    private String creditCode;

    @ExcelProperty(value = "法定代表人", index = 5)
    @ColumnWidth(15)
    private String legalRepresentative;

    @ExcelProperty(value = "送达地址", index = 6)
    @ColumnWidth(30)
    private String serviceAddress;

    @ExcelProperty(value = "代理人姓名", index = 7)
    @ColumnWidth(15)
    private String agentName;

    @ExcelProperty(value = "代理人电话", index = 8)
    @ColumnWidth(15)
    private String agentPhone;

    @ExcelProperty(value = "代理人身份证", index = 9)
    @ColumnWidth(20)
    private String agentIdCard;

    @ExcelProperty(value = "代理人地址", index = 10)
    @ColumnWidth(30)
    private String agentAddress;

    @ExcelProperty(value = "账户名称", index = 11)
    @ColumnWidth(20)
    private String accountName;

    @ExcelProperty(value = "债权人银行账号", index = 12)
    @ColumnWidth(25)
    private String creditorBankAccount;

    @ExcelProperty(value = "开户行", index = 13)
    @ColumnWidth(20)
    private String bankName;

    @ExcelProperty(value = "本金", index = 14)
    @ColumnWidth(15)
    private BigDecimal principal;

    @ExcelProperty(value = "利息", index = 15)
    @ColumnWidth(15)
    private BigDecimal interest;

    @ExcelProperty(value = "违约金", index = 16)
    @ColumnWidth(15)
    private BigDecimal penalty;

    @ExcelProperty(value = "其他损失", index = 17)
    @ColumnWidth(15)
    private BigDecimal otherLosses;

    @ExcelProperty(value = "总金额", index = 18)
    @ColumnWidth(15)
    private BigDecimal totalAmount;

    @ExcelProperty(value = "是否有法院判决", index = 19)
    @ColumnWidth(15)
    private String hasCourtJudgment;

    @ExcelProperty(value = "是否有执行", index = 20)
    @ColumnWidth(15)
    private String hasExecution;

    @ExcelProperty(value = "是否有担保", index = 21)
    @ColumnWidth(15)
    private String hasCollateral;

    @ExcelProperty(value = "债权性质", index = 22)
    @ColumnWidth(15)
    private String claimNature;

    @ExcelProperty(value = "债权类型", index = 23)
    @ColumnWidth(15)
    private String claimType;

    @ExcelProperty(value = "债权事实", index = 24)
    @ColumnWidth(30)
    private String claimFacts;

    @ExcelProperty(value = "债权标识", index = 25)
    @ColumnWidth(20)
    private String claimIdentifier;

    @ExcelProperty(value = "证据清单", index = 26)
    @ColumnWidth(30)
    private String evidenceList;

    @ExcelProperty(value = "证据材料", index = 27)
    @ColumnWidth(30)
    private String evidenceMaterials;

    @ExcelProperty(value = "证据附件", index = 28)
    @ColumnWidth(30)
    private String evidenceAttachments;

    @ExcelProperty(value = "登记日期", index = 29)
    @ColumnWidth(20)
    private LocalDateTime registrationDate;

    @ExcelProperty(value = "登记截止日期", index = 30)
    @ColumnWidth(20)
    private LocalDateTime registrationDeadline;

    @ExcelProperty(value = "材料接收人", index = 31)
    @ColumnWidth(15)
    private String materialReceiver;

    @ExcelProperty(value = "材料接收日期", index = 32)
    @ColumnWidth(20)
    private LocalDateTime materialReceiveDate;

    @ExcelProperty(value = "材料完整性", index = 33)
    @ColumnWidth(15)
    private String materialCompleteness;

    @ExcelProperty(value = "备注", index = 34)
    @ColumnWidth(30)
    private String remarks;
}
