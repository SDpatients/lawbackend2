package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private User adminUser;
    private User normalUser;
    private Role adminRole;
    private Role normalRole;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder()
                .id(1L)
                .roleCode("ADMIN")
                .roleName("管理员")
                .build();

        normalRole = Role.builder()
                .id(2L)
                .roleCode("USER")
                .roleName("普通用户")
                .build();

        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .build();

        normalUser = User.builder()
                .id(2L)
                .username("user")
                .build();
    }

    @Test
    void testIsAdministrator_WithAdminRole_ShouldReturnTrue() {
        UserRole userRole = UserRole.builder()
                .userId(1L)
                .roleId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRoleRepository.findRoleIdsByUserId(1L)).thenReturn(Arrays.asList(1L));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        boolean result = roleService.isAdministrator(1L);

        assertTrue(result);
        verify(userRoleRepository).findRoleIdsByUserId(1L);
        verify(roleRepository).findById(1L);
    }

    @Test
    void testIsAdministrator_WithNormalRole_ShouldReturnFalse() {
        UserRole userRole = UserRole.builder()
                .userId(2L)
                .roleId(2L)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        when(userRoleRepository.findRoleIdsByUserId(2L)).thenReturn(Arrays.asList(2L));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(normalRole));

        boolean result = roleService.isAdministrator(2L);

        assertFalse(result);
        verify(userRoleRepository).findRoleIdsByUserId(2L);
        verify(roleRepository).findById(2L);
    }

    @Test
    void testIsAdministrator_WithAdminRoleName_ShouldReturnTrue() {
        Role adminRoleByName = Role.builder()
                .id(3L)
                .roleCode("SUPER_USER")
                .roleName("管理员")
                .build();

        UserRole userRole = UserRole.builder()
                .userId(3L)
                .roleId(3L)
                .build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(userRoleRepository.findRoleIdsByUserId(3L)).thenReturn(Arrays.asList(3L));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(adminRoleByName));

        boolean result = roleService.isAdministrator(3L);

        assertTrue(result);
        verify(userRoleRepository).findRoleIdsByUserId(3L);
        verify(roleRepository).findById(3L);
    }

    @Test
    void testGetUserRoleIds_ShouldReturnCorrectIds() {
        when(userRoleRepository.findRoleIdsByUserId(1L)).thenReturn(Arrays.asList(1L, 2L));

        List<Long> roleIds = roleService.getUserRoleIds(1L);

        assertNotNull(roleIds);
        assertEquals(2, roleIds.size());
        assertTrue(roleIds.contains(1L));
        assertTrue(roleIds.contains(2L));
        verify(userRoleRepository).findRoleIdsByUserId(1L);
    }

    @Test
    void testGetUserRoleIds_WithNoRoles_ShouldReturnEmptyList() {
        when(userRoleRepository.findRoleIdsByUserId(2L)).thenReturn(Arrays.asList());

        List<Long> roleIds = roleService.getUserRoleIds(2L);

        assertNotNull(roleIds);
        assertTrue(roleIds.isEmpty());
        verify(userRoleRepository).findRoleIdsByUserId(2L);
    }
}
