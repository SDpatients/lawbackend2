package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.UserWithRolesResponse;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        List<Long> existingRoleIds = userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        for (Long roleId : roleIds) {
            if (!existingRoleIds.contains(roleId)) {
                UserRole userRole = UserRole.builder()
                        .userId(userId)
                        .roleId(roleId)
                        .build();
                userRoleRepository.save(userRole);
            }
        }

        log.info("为用户分配角色 - 用户ID: {}, 新增角色数量: {}", userId, roleIds.size());
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        log.info("查询用户角色 - 用户ID: {}, 角色数量: {}", userId, roleIds.size());
        return roleIds;
    }

    @Override
    @Transactional
    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<UserRole> toRemove = userRoles.stream()
                .filter(ur -> roleIds.contains(ur.getRoleId()))
                .collect(Collectors.toList());

        userRoleRepository.deleteAll(toRemove);

        log.info("移除用户角色 - 用户ID: {}, 移除角色数量: {}", userId, toRemove.size());
    }

    @Override
    @Transactional
    public void clearUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        userRoleRepository.deleteByUserId(userId);

        log.info("清空用户角色 - 用户ID: {}", userId);
    }

    @Override
    public List<UserWithRolesResponse> getUserRoleList() {
        List<User> users = userRepository.findAllActive();
        List<Long> userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        List<UserRole> userRoles = userRoleRepository.findByUserIds(userIds);
        Map<Long, List<Long>> userRoleMap = userRoles.stream()
                .collect(Collectors.groupingBy(
                        UserRole::getUserId,
                        Collectors.mapping(UserRole::getRoleId, Collectors.toList())
                ));

        List<Role> allRoles = roleRepository.findAllActive();
        Map<Long, Role> roleMap = allRoles.stream()
                .collect(Collectors.toMap(Role::getId, r -> r));

        List<UserWithRolesResponse> responseList = new ArrayList<>();
        for (User user : users) {
            List<Long> roleIds = userRoleMap.getOrDefault(user.getId(), new ArrayList<>());

            List<UserWithRolesResponse.RoleInfo> roleInfos = roleIds.stream()
                    .map(roleId -> {
                        Role role = roleMap.get(roleId);
                        if (role != null) {
                            return UserWithRolesResponse.RoleInfo.builder()
                                    .id(role.getId())
                                    .roleCode(role.getRoleCode())
                                    .roleName(role.getRoleName())
                                    .roleDesc(role.getRoleDesc())
                                    .isSystem(role.getIsSystem())
                                    .status(role.getStatus())
                                    .sortOrder(role.getSortOrder())
                                    .build();
                        }
                        return null;
                    })
                    .filter(roleInfo -> roleInfo != null)
                    .collect(Collectors.toList());

            UserWithRolesResponse response = UserWithRolesResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .mobile(user.getMobile())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .isValid(user.getIsValid())
                    .status(user.getStatus())
                    .loginType(user.getLoginType())
                    .lastLoginTime(user.getLastLoginTime())
                    .lastLoginIp(user.getLastLoginIp())
                    .loginCount(user.getLoginCount())
                    .createTime(user.getCreateTime())
                    .updateTime(user.getUpdateTime())
                    .roles(roleInfos)
                    .build();

            responseList.add(response);
        }

        log.info("获取用户角色列表 - 用户数量: {}", responseList.size());
        return responseList;
    }
}
