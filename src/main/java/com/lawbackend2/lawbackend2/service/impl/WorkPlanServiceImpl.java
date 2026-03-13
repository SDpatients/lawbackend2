package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanResponse;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.WorkPlan;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.repository.WorkPlanRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import com.lawbackend2.lawbackend2.service.WorkPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkPlanServiceImpl implements WorkPlanService {

    private final WorkPlanRepository workPlanRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BankruptCaseRepository bankruptCaseRepository;

    public WorkPlanServiceImpl(WorkPlanRepository workPlanRepository, WorkTeamMemberRepository workTeamMemberRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, UserRepository userRepository, BankruptCaseRepository bankruptCaseRepository) {
        this.workPlanRepository = workPlanRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    public Long createWorkPlan(WorkPlanCreateRequest request, Long userId) {
        WorkPlan workPlan = new WorkPlan();
        BeanUtils.copyProperties(request, workPlan);
        
        // 自动生成计划编号
        if (workPlan.getPlanNumber() == null || workPlan.getPlanNumber().isEmpty()) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "WP" + today;
            
            // 查询当天已有的计划数量
            int count = workPlanRepository.countByPlanNumberStartingWith(prefix);
            String sequence = String.format("%04d", count + 1);
            workPlan.setPlanNumber(prefix + sequence);
        }
        
        workPlan.setExecutionStatus("NOT_STARTED");
        workPlan.setStatus("ACTIVE");
        workPlan.setCreateUserId(userId);
        workPlan.setUpdateUserId(userId);

        WorkPlan saved = workPlanRepository.save(workPlan);
        return saved.getId();
    }

    private WorkPlanResponse convertToResponse(WorkPlan workPlan) {
        WorkPlanResponse response = new WorkPlanResponse();
        BeanUtils.copyProperties(workPlan, response);
        
        if (workPlan.getResponsibleUserId() != null) {
            Optional<User> userOpt = userRepository.findById(workPlan.getResponsibleUserId());
            if (userOpt.isPresent()) {
                response.setResponsibleUserName(userOpt.get().getRealName());
            }
        }
        
        if (workPlan.getCaseId() != null) {
            Optional<com.lawbackend2.lawbackend2.entity.BankruptCase> caseOpt = bankruptCaseRepository.findById(workPlan.getCaseId());
            if (caseOpt.isPresent()) {
                response.setCaseNumber(caseOpt.get().getCaseNumber());
                response.setCaseName(caseOpt.get().getCaseName());
            }
        }
        
        return response;
    }

    @Override
    public PageResult<WorkPlanResponse> getWorkPlanList(Integer pageNum, Integer pageSize, Long caseId, String planType, String executionStatus, String status, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<WorkPlan> spec = buildSpecificationWithPermission(caseId, planType, executionStatus, status, userId);
        Page<WorkPlan> page = workPlanRepository.findAll(spec, pageable);

        PageResult<WorkPlanResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent().stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList()));
        return result;
    }

    @Override
    public WorkPlan getWorkPlanDetail(Long planId, Long userId) {
        WorkPlan workPlan = workPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("工作计划不存在"));
        
        checkPermission(workPlan, userId);
        
        return workPlan;
    }
    
    @Override
    public WorkPlanResponse getWorkPlanDetailResponse(Long planId, Long userId) {
        WorkPlan workPlan = getWorkPlanDetail(planId, userId);
        return convertToResponse(workPlan);
    }

    @Override
    public void updateWorkPlan(Long planId, WorkPlanUpdateRequest request, Long userId) {
        WorkPlan workPlan = getWorkPlanDetail(planId, userId);
        workPlan.setPlanContent(request.getPlanContent());
        workPlan.setStartDate(request.getStartDate());
        workPlan.setEndDate(request.getEndDate());
        workPlan.setResponsibleUserId(request.getResponsibleUserId());
        workPlanRepository.save(workPlan);
    }

    @Override
    public void updateWorkPlanStatus(Long planId, WorkPlanStatusRequest request, Long userId) {
        WorkPlan workPlan = getWorkPlanDetail(planId, userId);
        workPlan.setExecutionStatus(request.getExecutionStatus());
        workPlanRepository.save(workPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkPlan(Long planId) {
        if (!workPlanRepository.existsById(planId)) {
            throw new BusinessException("工作计划不存在");
        }
        workPlanRepository.deleteById(planId);
    }

    @Override
    public PageResult<WorkPlanResponse> getWorkPlanListByTimeRange(Integer pageNum, Integer pageSize, String startDate, String endDate, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<WorkPlan> spec = buildTimeRangeSpecification(startDate, endDate, userId);
        Page<WorkPlan> page = workPlanRepository.findAll(spec, pageable);

        PageResult<WorkPlanResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent().stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList()));
        return result;
    }

    private Specification<WorkPlan> buildSpecificationWithPermission(Long caseId, String planType, String executionStatus, String status, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (caseId != null) {
                predicates.add(cb.equal(root.get("caseId"), caseId));
            }

            if (planType != null && !planType.isEmpty()) {
                predicates.add(cb.equal(root.get("planType"), planType));
            }

            if (executionStatus != null && !executionStatus.isEmpty()) {
                predicates.add(cb.equal(root.get("executionStatus"), executionStatus));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (!isAdminOrSuperAdmin(userId)) {
                List<Long> accessibleCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
                predicates.add(root.get("caseId").in(accessibleCaseIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void checkPermission(WorkPlan workPlan, Long userId) {
        if (isAdminOrSuperAdmin(userId)) {
            return;
        }
        
        if (workPlan.getCaseId() != null) {
            List<Long> accessibleCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
            if (accessibleCaseIds.contains(workPlan.getCaseId())) {
                return;
            }
        }
        
        throw new BusinessException("无权访问该工作计划");
    }

    private boolean isAdminOrSuperAdmin(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null && ("ADMIN".equals(role.getRoleCode()) || "SUPER_ADMIN".equals(role.getRoleCode()))) {
                return true;
            }
        }
        return false;
    }

    private Specification<WorkPlan> buildTimeRangeSpecification(String startDateStr, String endDateStr, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            predicates.add(cb.or(
                cb.between(root.get("startDate"), startDate, endDate),
                cb.between(root.get("endDate"), startDate, endDate)
            ));

            if (!isAdminOrSuperAdmin(userId)) {
                List<Long> accessibleCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
                predicates.add(root.get("caseId").in(accessibleCaseIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
