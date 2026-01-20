package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.CaseProcessStageCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseProcessStageUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseProcessStage;
import com.lawbackend2.lawbackend2.service.CaseProcessStageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Tag(name = "案件流程阶段管理")
@RestController
@RequestMapping("/api/case-process-stage")
public class CaseProcessStageController {

    private final CaseProcessStageService caseProcessStageService;

    public CaseProcessStageController(CaseProcessStageService caseProcessStageService) {
        this.caseProcessStageService = caseProcessStageService;
    }

    @Operation(summary = "新增阶段数据")
    @PostMapping
    public Result<Boolean> saveStage(@Valid @RequestBody CaseProcessStageCreateRequest request) {
        CaseProcessStage stage = new CaseProcessStage();
        BeanUtils.copyProperties(request, stage);
        boolean result = caseProcessStageService.saveStage(stage);
        return Result.success(result);
    }

    @Operation(summary = "新增阶段数据(带文件)")
    @PostMapping("/with-files")
    public Result<Boolean> saveStageWithFiles(
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId,
            @Parameter(description = "阶段编号") @RequestParam("stageNum") Integer stageNum,
            @Parameter(description = "阶段名称") @RequestParam("stageName") String stageName,
            @Parameter(description = "模块编码") @RequestParam("moduleCode") String moduleCode,
            @Parameter(description = "模块名称") @RequestParam("moduleName") String moduleName,
            @Parameter(description = "标题") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "内容") @RequestParam(value = "content", required = false) String content,
            @Parameter(description = "处理日期") @RequestParam(value = "processDate", required = false) String processDate,
            @Parameter(description = "文件列表") @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "模块特有字段数据") @RequestParam("fieldData") String fieldData,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) String status) {
        
        CaseProcessStage stage = new CaseProcessStage();
        stage.setCaseId(caseId);
        stage.setStageNum(stageNum);
        stage.setStageName(stageName);
        stage.setModuleCode(moduleCode);
        stage.setModuleName(moduleName);
        stage.setTitle(title);
        stage.setContent(content);
        stage.setFieldData(fieldData);
        stage.setStatus(status);
        
        boolean result = caseProcessStageService.saveStageWithFiles(stage, files);
        return Result.success(result);
    }

    @Operation(summary = "更新阶段数据")
    @PutMapping("/{id}")
    public Result<Boolean> updateStage(
            @Parameter(description = "阶段数据ID") @PathVariable Long id,
            @Valid @RequestBody CaseProcessStageUpdateRequest request) {
        CaseProcessStage existingStage = caseProcessStageService.getStageById(id);
        BeanUtils.copyProperties(request, existingStage, "id");
        boolean result = caseProcessStageService.updateStage(existingStage);
        return Result.success(result);
    }

    @Operation(summary = "更新阶段数据(带文件)")
    @PutMapping("/{id}/with-files")
    public Result<Boolean> updateStageWithFiles(
            @Parameter(description = "阶段数据ID") @PathVariable Long id,
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId,
            @Parameter(description = "阶段编号") @RequestParam(value = "stageNum", required = false) Integer stageNum,
            @Parameter(description = "阶段名称") @RequestParam(value = "stageName", required = false) String stageName,
            @Parameter(description = "模块编码") @RequestParam(value = "moduleCode", required = false) String moduleCode,
            @Parameter(description = "模块名称") @RequestParam(value = "moduleName", required = false) String moduleName,
            @Parameter(description = "标题") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "内容") @RequestParam(value = "content", required = false) String content,
            @Parameter(description = "处理日期") @RequestParam(value = "processDate", required = false) String processDate,
            @Parameter(description = "文件列表") @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "模块特有字段数据") @RequestParam(value = "fieldData", required = false) String fieldData,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) String status) {
        
        CaseProcessStage existingStage = caseProcessStageService.getStageById(id);
        if (stageNum != null) existingStage.setStageNum(stageNum);
        if (stageName != null) existingStage.setStageName(stageName);
        if (moduleCode != null) existingStage.setModuleCode(moduleCode);
        if (moduleName != null) existingStage.setModuleName(moduleName);
        if (title != null) existingStage.setTitle(title);
        if (content != null) existingStage.setContent(content);
        if (fieldData != null) existingStage.setFieldData(fieldData);
        if (status != null) existingStage.setStatus(status);
        
        boolean result = caseProcessStageService.updateStageWithFiles(existingStage, files);
        return Result.success(result);
    }

    @Operation(summary = "删除阶段数据")
    @DeleteMapping("/{id}")
    public Result<Boolean> removeStage(@Parameter(description = "阶段数据ID") @PathVariable Long id) {
        boolean result = caseProcessStageService.removeStage(id);
        return Result.success(result);
    }

    @Operation(summary = "查询单个阶段数据")
    @GetMapping("/{id}")
    public Result<CaseProcessStage> getStageById(@Parameter(description = "阶段数据ID") @PathVariable Long id) {
        CaseProcessStage stage = caseProcessStageService.getStageById(id);
        return Result.success(stage);
    }

    @Operation(summary = "查询案件的所有阶段数据")
    @GetMapping("/case/{caseId}")
    public Result<List<CaseProcessStage>> getStagesByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        List<CaseProcessStage> stages = caseProcessStageService.getStagesByCaseId(caseId);
        return Result.success(stages);
    }

    @Operation(summary = "查询案件的特定阶段数据")
    @GetMapping("/case/{caseId}/stage/{stageNum}")
    public Result<List<CaseProcessStage>> getStagesByCaseIdAndStageNum(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "阶段编号") @PathVariable Integer stageNum) {
        List<CaseProcessStage> stages = caseProcessStageService.getStagesByCaseIdAndStageNum(caseId, stageNum);
        return Result.success(stages);
    }

    @Operation(summary = "查询案件的特定模块数据")
    @GetMapping("/case/{caseId}/module/{moduleCode}")
    public Result<List<CaseProcessStage>> getStagesByCaseIdAndModuleCode(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "模块编码") @PathVariable String moduleCode) {
        List<CaseProcessStage> stages = caseProcessStageService.getStagesByCaseIdAndModuleCode(caseId, moduleCode);
        return Result.success(stages);
    }
}
