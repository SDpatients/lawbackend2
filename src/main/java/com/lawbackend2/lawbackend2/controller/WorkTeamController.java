package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.AssignWorkTeamPermissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberPermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamUpdateRequest;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import com.lawbackend2.lawbackend2.service.WorkTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "工作团队管理")
@RestController
@RequestMapping("/work-team")
@Validated
public class WorkTeamController {

    private final WorkTeamService workTeamService;

    public WorkTeamController(WorkTeamService workTeamService) {
        this.workTeamService = workTeamService;
    }

    @Operation(summary = "创建工作团队")
    @PostMapping
    public Result<Map<String, Object>> createWorkTeam(@Valid @RequestBody WorkTeamCreateRequest request) {
        Long teamId = workTeamService.createWorkTeam(request);

        Map<String, Object> data = new HashMap<>();
        data.put("teamId", teamId);

        return Result.success(data);
    }

    @Operation(summary = "工作团队列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<WorkTeam>> getWorkTeamList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "团队名称(模糊查询)") @RequestParam(required = false) String teamName,
            @Parameter(description = "团队负责人ID") @RequestParam(required = false) Long teamLeaderId) {

        PageResult<WorkTeam> result = workTeamService.getWorkTeamList(pageNum, pageSize, caseId, status, teamName, teamLeaderId);
        return Result.success(result);
    }

    @Operation(summary = "获取工作团队详情")
    @GetMapping("/{teamId}")
    public Result<WorkTeam> getWorkTeamDetail(@Parameter(description = "团队ID") @PathVariable Long teamId) {
        WorkTeam workTeam = workTeamService.getWorkTeamDetail(teamId);
        return Result.success(workTeam);
    }

    @Operation(summary = "更新工作团队信息")
    @PutMapping("/{teamId}")
    public Result<Void> updateWorkTeam(
            @Parameter(description = "团队ID") @PathVariable Long teamId,
            @Valid @RequestBody WorkTeamUpdateRequest request) {

        workTeamService.updateWorkTeam(teamId, request);
        return Result.success();
    }

    @Operation(summary = "删除工作团队")
    @DeleteMapping("/{teamId}")
    public Result<Void> deleteWorkTeam(@Parameter(description = "团队ID") @PathVariable Long teamId) {
        workTeamService.deleteWorkTeam(teamId);
        return Result.success();
    }

    @Operation(summary = "添加团队成员")
    @PostMapping("/{teamId}/member")
    public Result<Map<String, Object>> addWorkTeamMember(
            @Parameter(description = "团队ID") @PathVariable Long teamId,
            @Valid @RequestBody WorkTeamMemberCreateRequest request) {

        Long memberId = workTeamService.addWorkTeamMember(teamId, request);

        Map<String, Object> data = new HashMap<>();
        data.put("memberId", memberId);

        return Result.success(data);
    }

    @Operation(summary = "团队成员列表")
    @GetMapping("/{teamId}/members")
    public Result<List<WorkTeamMember>> getWorkTeamMembers(@Parameter(description = "团队ID") @PathVariable Long teamId) {
        List<WorkTeamMember> members = workTeamService.getWorkTeamMembers(teamId);
        return Result.success(members);
    }

    @Operation(summary = "获取团队成员权限")
    @GetMapping("/work-team-member/{memberId}/permissions")
    public Result<List<WorkTeamPermission>> getWorkTeamMemberPermissions(@Parameter(description = "成员ID") @PathVariable Long memberId) {
        List<WorkTeamPermission> permissions = workTeamService.getWorkTeamMemberPermissions(memberId);
        return Result.success(permissions);
    }

    @Operation(summary = "更新团队成员权限")
    @PutMapping("/work-team-member/{memberId}/permission")
    public Result<Void> updateWorkTeamMemberPermission(
            @Parameter(description = "成员ID") @PathVariable Long memberId,
            @Valid @RequestBody WorkTeamMemberPermissionRequest request) {

        workTeamService.updateWorkTeamMemberPermission(memberId, request);
        return Result.success();
    }

    @Operation(summary = "分配团队成员权限")
    @PostMapping("/work-team-member/{memberId}/permissions")
    public Result<Void> assignWorkTeamMemberPermissions(
            @Parameter(description = "成员ID") @PathVariable Long memberId,
            @Valid @RequestBody AssignWorkTeamPermissionsRequest request) {

        workTeamService.assignPermissionsToMember(memberId, request);
        return Result.success();
    }
}
