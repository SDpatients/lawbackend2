package com.lawbackend2.lawbackend2.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "案件搜索请求")
public class CaseSearchRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "关键词（支持案号、案件名称、受理法院、指定机构、主要负责人、案件来源、案件原因、指定法官等多字段模糊搜索）")
    private String keyword;

    @Schema(description = "案件编号")
    private String caseNumber;

    @Schema(description = "案件名称")
    private String caseName;

    @Schema(description = "案件状态")
    private String caseStatus;

    @Schema(description = "案件进度")
    private String caseProgress;

    @Schema(description = "受理法院")
    private String acceptanceCourt;

    @Schema(description = "指定机构")
    private String designatedInstitution;

    @Schema(description = "主要负责人")
    private String mainResponsiblePerson;

    @Schema(description = "案件来源")
    private String caseSource;

    @Schema(description = "案件原因")
    private String caseReason;

    @Schema(description = "指定法官")
    private String designatedJudge;

    @Schema(description = "受理日期开始")
    private LocalDate acceptanceDateStart;

    @Schema(description = "受理日期结束")
    private LocalDate acceptanceDateEnd;

    @Schema(description = "立案日期开始")
    private LocalDate filingDateStart;

    @Schema(description = "立案日期结束")
    private LocalDate filingDateEnd;

    @Schema(description = "创建日期开始")
    private LocalDate createDateStart;

    @Schema(description = "创建日期结束")
    private LocalDate createDateEnd;

    @Schema(description = "审核状态")
    private String reviewStatus;

    @Schema(description = "是否简易程序")
    private Boolean isSimplifiedTrial;

    @Schema(description = "承办人员")
    private String undertakingPersonnel;

    @Schema(description = "排序字段", example = "createTime")
    private String sortField = "createTime";

    @Schema(description = "排序方向", example = "DESC")
    private String sortOrder = "DESC";
}
