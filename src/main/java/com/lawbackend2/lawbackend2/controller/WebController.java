package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import com.lawbackend2.lawbackend2.service.WorkTeamService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/web")
@Tag(name = "公共Web接口", description = "面向前端页面的公共接口")
public class WebController {

    @Autowired
    private WorkTeamMemberRepository workTeamMemberRepository;

    @Autowired
    private BankruptCaseRepository bankruptCaseRepository;

    @Autowired
    private WorkTeamService workTeamService;

    @GetMapping("/currentUser")
    @Operation(summary = "获取当前用户的破产案件列表", description = "根据JWT Token获取当前用户参与的破产案件列表，分页返回")
    public Result<PageResult<BankruptCase>> getCurrentUserCases(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10") @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取当前用户的破产案件列表请求，pageNum: {}, pageSize: {}", pageNum, pageSize);
        Long userId = SecurityUtil.getCurrentUserId();
        
        List<Long> caseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
        
        if (caseIds.isEmpty()) {
            return Result.success(new PageResult<>(List.of(), 0L, pageNum, pageSize));
        }
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BankruptCase> casePage = bankruptCaseRepository.findByIdIn(caseIds, pageable);
        
        PageResult<BankruptCase> pageResult = new PageResult<>(
                casePage.getContent(),
                casePage.getTotalElements(),
                pageNum,
                pageSize
        );
        
        return Result.success(pageResult);
    }

    @GetMapping("/currentUser/workTeams")
    @Operation(summary = "获取当前用户参与的工作团队列表", description = "根据JWT Token获取当前用户参与的工作团队列表")
    public Result<List<WorkTeam>> getCurrentUserWorkTeams() {
        log.info("获取当前用户的工作团队列表请求");
        Long userId = SecurityUtil.getCurrentUserId();
        
        List<WorkTeam> workTeams = workTeamService.getWorkTeamsByUserId(userId);
        
        return Result.success(workTeams);
    }
}