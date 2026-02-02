package com.lawbackend2.lawbackend2.listener;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
@HeadRowHeight(20)
@ContentRowHeight(18)
public class DeclaredClaimsRegisterExcelImportDTO {

    @ExcelProperty(value = "收件编号")
    private String receiptNumber;

    @ExcelProperty(value = "债权人")
    private String creditorName;

    @ExcelProperty(value = "申报时间")
    private String declarationTime;

    @ExcelProperty(value = "住所/邮编")
    private String addressAndPostalCode;

    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    @ExcelProperty(value = "申报金额")
    private String declaredAmount;

    @ExcelProperty(value = "性质")
    private String nature;

    @ExcelProperty(value = "法定代表人")
    private String legalRepresentative;

    @ExcelProperty(value = "代理人")
    private String agentName;

    @ExcelProperty(value = "联系电话")
    private String agentPhone;

    @ExcelProperty(value = "债权性质")
    private String claimNature;

    @ExcelProperty(value = "债权种类")
    private String claimType;

    @ExcelProperty(value = "开户名")
    private String accountName;

    @ExcelProperty(value = "开户行")
    private String bankName;

    @ExcelProperty(value = "账号")
    private String bankAccount;

    @ExcelProperty(value = "涉讼")
    private String litigationStatus;

    @ExcelProperty(value = "备注")
    private String remarks;
}
