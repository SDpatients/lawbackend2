package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.WorkPlan;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WorkPlanServiceImpl implements WorkPlanService {

    private final WorkPlanRepository workPlanRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public WorkPlanServiceImpl(WorkPlanRepository workPlanRepository, WorkTeamMemberRepository workTeamMemberRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.workPlanRepository = workPlanRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Long createWorkPlan(WorkPlanCreateRequest request, Long userId) {
        WorkPlan workPlan = new WorkPlan();
        BeanUtils.copyProperties(request, workPlan);
        workPlan.setExecutionStatus("NOT_STARTED");
        workPlan.setStatus("ACTIVE");
        workPlan.setCreateUserId(userId);
        workPlan.setUpdateUserId(userId);

        WorkPlan saved = workPlanRepository.save(workPlan);
        return saved.getId();
    }

    @Override
    public PageResult<WorkPlan> getWorkPlanList(Integer pageNum, Integer pageSize, Long caseId, String planType, String executionStatus, String status, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<WorkPlan> spec = buildSpecificationWithPermission(caseId, planType, executionStatus, status, userId);
        Page<WorkPlan> page = workPlanRepository.findAll(spec, pageable);

        PageResult<WorkPlan> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
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
                predicates.add(cb.or(
                    cb.equal(root.get("createUserId"), userId),
                    root.get("caseId").in(accessibleCaseIds)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void checkPermission(WorkPlan workPlan, Long userId) {
        if (isAdminOrSuperAdmin(userId)) {
            return;
        }
        
        if (workPlan.getCreateUserId().equals(userId)) {
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
}
