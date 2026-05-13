package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.SensitiveDataViewRequest;
import com.lawbackend2.lawbackend2.dto.response.SensitiveDataViewResponse;
import com.lawbackend2.lawbackend2.service.SensitiveDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "敏感数据管理", description = "敏感数据完整查看（二次验证）")
@RestController
@RequestMapping("/system/sensitive-data")
@RequiredArgsConstructor
public class SensitiveDataController {

    private final SensitiveDataService sensitiveDataService;

    @PostMapping("/view")
    @Operation(summary = "查看完整敏感数据", description = "需输入当前登录用户密码二次验证后返回明文")
    @AuditLog(
            module = "敏感数据",
            moduleName = "SensitiveData",
            operationType = "VIEW",
            operationName = "查看完整敏感数据",
            description = "用户二次验证密码后查看敏感数据明文",
            recordData = false
    )
    public Result<SensitiveDataViewResponse> viewSensitiveData(
            @Valid @RequestBody SensitiveDataViewRequest request
    ) {
        SensitiveDataViewResponse response = sensitiveDataService.getPlainTextData(request);
        return Result.success(response);
    }
}