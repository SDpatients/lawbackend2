package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class DataPermissionHelper {

    private static final Logger log = LoggerFactory.getLogger(DataPermissionHelper.class);
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private RoleRepository roleRepository;

    public <T> Specification<T> applyDataPermission(Long userId, Function<Specification<T>, Specification<T>> specBuilder) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!isAdministrator(userId)) {
                predicates.add(cb.equal(root.get("createUserId"), userId));
                log.debug("应用数据权限过滤 - 用户ID: {}, 添加条件: createUserId = {}", userId, userId);
            } else {
                log.debug("管理员用户 - 用户ID: {}, 跳过数据权限过滤", userId);
            }

            Specification<T> baseSpec = (r, q, c) -> cb.and(predicates.toArray(new Predicate[0]));
            Specification<T> finalSpec = specBuilder.apply(baseSpec);

            return finalSpec.toPredicate(root, query, cb);
        };
    }

    public boolean isAdministrator(Long userId) {
        // 获取用户角色
        List<Long> roleIds = userRoleService.getUserRoles(userId);
        // 检查是否包含管理员角色（假设管理员角色代码为ADMIN）
        return roleIds.stream()
                .anyMatch(roleId -> {
                    Role role = roleRepository.findById(roleId).orElse(null);
                    return role != null && "ADMIN".equals(role.getRoleCode());
                });
    }
}
