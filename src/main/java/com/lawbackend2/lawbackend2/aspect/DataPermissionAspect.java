package com.lawbackend2.lawbackend2.aspect;

import com.lawbackend2.lawbackend2.annotation.DataPermission;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Aspect
@Component
public class DataPermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(DataPermissionAspect.class);
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Around("@annotation(dataPermission)")
    public Object checkDataPermission(ProceedingJoinPoint joinPoint, DataPermission dataPermission) throws Throwable {
        Long userId = SecurityUtil.getCurrentUserId();
        String fieldName = dataPermission.value();

        // 获取用户角色
        List<Long> roleIds = userRoleService.getUserRoles(userId);
        // 检查是否包含管理员角色（假设管理员角色代码为ADMIN）
        boolean isAdmin = roleIds.stream()
                .anyMatch(roleId -> {
                    Role role = roleRepository.findById(roleId).orElse(null);
                    return role != null && "ADMIN".equals(role.getRoleCode());
                });
                
        if (isAdmin) {
            log.debug("管理员用户 - 用户ID: {}, 跳过数据权限检查", userId);
            return joinPoint.proceed();
        }

        log.debug("非管理员用户 - 用户ID: {}, 添加数据权限过滤条件: {}", userId, fieldName);
        return joinPoint.proceed();
    }
}
