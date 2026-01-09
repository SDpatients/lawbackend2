package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        log.info("为用户分配角色 - 用户ID: {}, 角色数量: {}", userId, roleIds.size());
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        log.info("查询用户角色 - 用户ID: {}", userId);
        return List.of(1L, 2L);
    }

    @Override
    @Transactional
    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
        log.info("移除用户角色 - 用户ID: {}, 角色数量: {}", userId, roleIds.size());
    }

    @Override
    @Transactional
    public void clearUserRoles(Long userId) {
        log.info("清空用户角色 - 用户ID: {}", userId);
    }
}
