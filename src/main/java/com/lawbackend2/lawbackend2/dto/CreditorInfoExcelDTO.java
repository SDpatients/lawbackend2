package com.lawbackend2.lawbackend2.dto;

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
public class CreditorInfoExcelDTO {

    @ExcelProperty(value = "案件ID", index = 0)
    @ColumnWidth(15)
    private Long caseId;

    @ExcelProperty(value = "债权人名称", index = 1)
    @ColumnWidth(25)
    private String creditorName;

    @ExcelProperty(value = "债权人类型", index = 2)
    @ColumnWidth(15)
    private String creditorType;

    @ExcelProperty(value = "联系电话", index = 3)
    @ColumnWidth(15)
    private String contactPhone;

    @ExcelProperty(value = "联系邮箱", index = 4)
    @ColumnWidth(20)
    private String contactEmail;

    @ExcelProperty(value = "地址", index = 5)
    @ColumnWidth(30)
    private String address;

    @ExcelProperty(value = "身份证号/统一社会信用代码", index = 6)
    @ColumnWidth(25)
    private String idNumber;

    @ExcelProperty(value = "法定代表人", index = 7)
    @ColumnWidth(15)
    private String legalRepresentative;

    @ExcelProperty(value = "注册资本", index = 8)
    @ColumnWidth(15)
    private BigDecimal registeredCapital;

    @ExcelProperty(value = "债权人状态", index = 9)
    @ColumnWidth(15)
    private String creditorStatus;
}
