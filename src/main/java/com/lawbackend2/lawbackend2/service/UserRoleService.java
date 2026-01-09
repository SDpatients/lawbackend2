package com.lawbackend2.lawbackend2.service;

import java.util.List;

public interface UserRoleService {

    void assignRolesToUser(Long userId, List<Long> roleIds);

    List<Long> getUserRoles(Long userId);

    void removeRolesFromUser(Long userId, List<Long> roleIds);

    void clearUserRoles(Long userId);
}
