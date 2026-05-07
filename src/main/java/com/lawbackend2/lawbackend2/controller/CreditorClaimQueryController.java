package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CreditorClaimQueryRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimQueryResponse;
import com.lawbackend2.lawbackend2.service.CreditorClaimQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "债权申报查询管理")
@RestController
@RequestMapping("/creditor-claim-query")
public class CreditorClaimQueryController {

    private final CreditorClaimQueryService creditorClaimQueryService;

    public CreditorClaimQueryController(CreditorClaimQueryService creditorClaimQueryService) {
        this.creditorClaimQueryService = creditorClaimQueryService;
    }

    @Operation(
            summary = "条件查询债权申报信息（分页）",
            description = "根据案件 ID 查询该案件下的债权申报信息，支持按债权人名称、债权人类型、债权类型进行条件筛选，支持分页查询\n\n" +
                    "**请求参数说明：**\n" +
                    "- caseId: 案件 ID（必填）\n" +
                    "- creditorName: 债权人名称（可选，支持模糊查询）\n" +
                    "- creditorType: 债权人类型（可选，精确匹配，如：企业、个人）\n" +
                    "- claimType: 债权类型（可选，精确匹配，如：借款债权、担保债权）\n" +
                    "- pageNum: 页码，从 1 开始（可选，默认 1）\n" +
                    "- pageSize: 每页大小（可选，默认 10）\n\n" +
                    "**返回值说明：**\n" +
                    "- total: 总记录数\n" +
                    "- list: 债权申报列表\n" +
                    "- pageNum: 当前页码\n" +
                    "- pageSize: 每页大小\n\n" +
                    "**list 数组字段说明：**\n" +
                    "- creditorId: 债权人 ID\n" +
                    "- caseId: 案件 ID\n" +
                    "- creditorName: 债权人名称\n" +
                    "- creditorType: 债权人类型\n" +
                    "- creditorStatus: 债权人状态（CONFIRMED-确认债权人、KNOWN-已知债权人）\n" +
                    "- contactPhone: 联系电话\n" +
                    "- contactEmail: 联系邮箱\n" +
                    "- address: 地址\n" +
                    "- idNumber: 身份证号\n" +
                    "- legalRepresentative: 法定代表人\n" +
                    "- registeredCapital: 注册资本\n" +
                    "- caseNumber: 案件案号\n" +
                    "- caseName: 案件名称\n" +
                    "- createTime: 创建时间\n" +
                    "- updateTime: 更新时间\n" +
                    "- claimType: 债权类型\n" +
                    "- accountName: 账户名称\n" +
                    "- creditorBankAccount: 银行账号\n" +
                    "- bankName: 开户银行\n" +
                    "- declaredPrincipal: 申报本金\n" +
                    "- declaredInterest: 申报利息\n" +
                    "- declaredPenalty: 申报罚金\n" +
                    "- declaredOtherLosses: 申报其他损失\n" +
                    "- declaredTotalAmount: 申报总金额\n" +
                    "- remarks: 备注\n" +
                    "- confirmedPrincipal: 确认本金\n" +
                    "- confirmedInterest: 确认利息\n" +
                    "- confirmedPenalty: 确认违约金\n" +
                    "- confirmedOtherLosses: 确认其他损失\n" +
                    "- confirmedTotalAmount: 确认总金额\n" +
                    "- reductionAmount: 核减金额（申报总金额 - 确认总金额）\n\n" +
                    "**数据来源说明：**\n" +
                    "- 申报数据来自 tb_claim_registration 表\n" +
                    "- 确认数据优先从 tb_claim_confirmation 表获取最新确认记录\n" +
                    "- 如无确认记录，则从 tb_claim_review 表获取最新审查记录\n" +
                    "- 债权人状态从 tb_creditor_info 表获取\n\n" +
                    "**状态码说明：**\n" +
                    "- PENDING: 待登记\n" +
                    "- REVIEW_COMPLETED: 审查完成\n" +
                    "- CONFIRMING: 确认中\n" +
                    "- CONFIRMED: 已确认\n" +
                    "- REGISTERED: 已登记\n" +
                    "- REJECTED: 已驳回",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "查询条件（caseId 必填，其他参数可选）",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditorClaimQueryRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "查询案件下所有债权（第一页）",
                                            value = "{\n  \"caseId\": 123,\n  \"pageNum\": 1,\n  \"pageSize\": 10\n}",
                                            description = "查询案件 ID 为 123 的所有债权申报信息，第一页，每页 10 条"
                                    ),
                                    @ExampleObject(
                                            name = "按债权人名称查询",
                                            value = "{\n  \"caseId\": 123,\n  \"creditorName\": \"某某公司\",\n  \"pageNum\": 1,\n  \"pageSize\": 10\n}",
                                            description = "查询案件 ID 为 123 下债权人名称包含'某某公司'的记录"
                                    ),
                                    @ExampleObject(
                                            name = "按债权人类型查询",
                                            value = "{\n  \"caseId\": 123,\n  \"creditorType\": \"企业\",\n  \"pageNum\": 1,\n  \"pageSize\": 10\n}",
                                            description = "查询案件 ID 为 123 下债权人类型为'企业'的记录"
                                    ),
                                    @ExampleObject(
                                            name = "按债权类型查询",
                                            value = "{\n  \"caseId\": 123,\n  \"claimType\": \"借款债权\",\n  \"pageNum\": 1,\n  \"pageSize\": 10\n}",
                                            description = "查询案件 ID 为 123 下债权类型为'借款债权'的记录"
                                    ),
                                    @ExampleObject(
                                            name = "多条件组合查询",
                                            value = "{\n  \"caseId\": 123,\n  \"creditorName\": \"某某公司\",\n  \"creditorType\": \"企业\",\n  \"claimType\": \"借款债权\",\n  \"pageNum\": 1,\n  \"pageSize\": 10\n}",
                                            description = "查询案件 ID 为 123 下同时满足三个条件的记录（交集）"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "查询成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                    name = "成功响应示例",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"success\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"total\": 25,\n" +
                                            "    \"list\": [\n" +
                                            "      {\n" +
                                            "        \"creditorId\": 1,\n" +
                                            "        \"caseId\": 123,\n" +
                                            "        \"creditorName\": \"某某有限公司\",\n" +
                                            "        \"creditorType\": \"企业\",\n" +
                                            "        \"creditorStatus\": \"CONFIRMED\",\n" +
                                            "        \"contactPhone\": \"010-12345678\",\n" +
                                            "        \"contactEmail\": \"example@company.com\",\n" +
                                            "        \"address\": \"北京市朝阳区xxx路xxx号\",\n" +
                                            "        \"idNumber\": \"91110000XXXXXXXXXX\",\n" +
                                            "        \"legalRepresentative\": \"李四\",\n" +
                                            "        \"registeredCapital\": 5000000.00,\n" +
                                            "        \"caseNumber\": \"(2023)京01破申1号\",\n" +
                                            "        \"caseName\": \"某某有限公司破产清算案\",\n" +
                                            "        \"createTime\": \"2024-01-15T10:30:00\",\n" +
                                            "        \"updateTime\": \"2024-03-20T14:20:00\",\n" +
                                            "        \"claimType\": \"借款债权\",\n" +
                                            "        \"accountName\": \"某某有限公司\",\n" +
                                            "        \"creditorBankAccount\": \"1234567890123456789\",\n" +
                                            "        \"bankName\": \"中国工商银行\",\n" +
                                            "        \"declaredPrincipal\": 800000.00,\n" +
                                            "        \"declaredInterest\": 150000.00,\n" +
                                            "        \"declaredPenalty\": 30000.00,\n" +
                                            "        \"declaredOtherLosses\": 20000.00,\n" +
                                            "        \"declaredTotalAmount\": 1000000.00,\n" +
                                            "        \"remarks\": \"无\",\n" +
                                            "        \"confirmedPrincipal\": 700000.00,\n" +
                                            "        \"confirmedInterest\": 80000.00,\n" +
                                            "        \"confirmedPenalty\": 0.00,\n" +
                                            "        \"confirmedOtherLosses\": 20000.00,\n" +
                                            "        \"confirmedTotalAmount\": 800000.00,\n" +
                                            "        \"reductionAmount\": 200000.00\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"creditorId\": 2,\n" +
                                            "        \"caseId\": 123,\n" +
                                            "        \"creditorName\": \"张三\",\n" +
                                            "        \"creditorType\": \"个人\",\n" +
                                            "        \"creditorStatus\": \"KNOWN\",\n" +
                                            "        \"contactPhone\": \"13800138000\",\n" +
                                            "        \"contactEmail\": \"zhangsan@example.com\",\n" +
                                            "        \"address\": \"北京市海淀区xxx路xxx号\",\n" +
                                            "        \"idNumber\": \"110101199001011234\",\n" +
                                            "        \"legalRepresentative\": null,\n" +
                                            "        \"registeredCapital\": null,\n" +
                                            "        \"caseNumber\": \"(2023)京01破申1号\",\n" +
                                            "        \"caseName\": \"某某有限公司破产清算案\",\n" +
                                            "        \"createTime\": \"2024-02-10T09:00:00\",\n" +
                                            "        \"updateTime\": \"2024-04-05T16:30:00\",\n" +
                                            "        \"claimType\": \"担保债权\",\n" +
                                            "        \"accountName\": \"张三\",\n" +
                                            "        \"creditorBankAccount\": \"9876543210987654321\",\n" +
                                            "        \"bankName\": \"中国建设银行\",\n" +
                                            "        \"declaredPrincipal\": 400000.00,\n" +
                                            "        \"declaredInterest\": 80000.00,\n" +
                                            "        \"declaredPenalty\": 10000.00,\n" +
                                            "        \"declaredOtherLosses\": 10000.00,\n" +
                                            "        \"declaredTotalAmount\": 500000.00,\n" +
                                            "        \"remarks\": \"有担保物\",\n" +
                                            "        \"confirmedPrincipal\": 400000.00,\n" +
                                            "        \"confirmedInterest\": 80000.00,\n" +
                                            "        \"confirmedPenalty\": 0.00,\n" +
                                            "        \"confirmedOtherLosses\": 10000.00,\n" +
                                            "        \"confirmedTotalAmount\": 490000.00,\n" +
                                            "        \"reductionAmount\": 10000.00\n" +
                                            "      }\n" +
                                            "    ],\n" +
                                            "    \"pageNum\": 1,\n" +
                                            "    \"pageSize\": 10\n" +
                                            "  }\n" +
                                            "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "请求参数错误",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "错误响应示例 - 缺少 caseId",
                                            value = "{\n" +
                                                    "  \"code\": 400,\n" +
                                                    "  \"message\": \"案件 ID 不能为空\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "服务器内部错误",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "错误响应示例",
                                            value = "{\n" +
                                                    "  \"code\": 500,\n" +
                                                    "  \"message\": \"查询失败：数据库连接异常\",\n" +
                                                    "  \"data\": null\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/query")
    public Result<PageResult<CreditorClaimQueryResponse>> queryClaims(
            @Parameter(description = "查询条件（caseId 必填，其他参数可选）")
            @Valid @RequestBody CreditorClaimQueryRequest request) {
        PageResult<CreditorClaimQueryResponse> results = creditorClaimQueryService.queryClaims(request);
        return Result.success(results);
    }
}
