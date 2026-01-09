package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.UserListResponse;
import com.lawbackend2.lawbackend2.dto.response.UserResponse;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceCRUDTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("Test@1234"))
                .realName("测试用户")
                .mobile("13800138001")
                .email("test@example.com")
                .status("ACTIVE")
                .build();
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser_Success() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("NewUser@123")
                .realName("新用户")
                .mobile("13900139000")
                .email("newuser@example.com")
                .phone("010-12345678")
                .status("ACTIVE")
                .build();

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("新用户", response.getRealName());
        assertEquals("13900139000", response.getMobile());
        assertEquals("newuser@example.com", response.getEmail());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void testCreateUser_DuplicateUsername() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("testuser")
                .password("NewUser@123")
                .realName("新用户")
                .mobile("13900139000")
                .email("newuser@example.com")
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(request);
        });

        assertEquals(400, exception.getCode());
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void testCreateUser_DuplicateMobile() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("NewUser@123")
                .realName("新用户")
                .mobile("13800138001")
                .email("newuser@example.com")
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(request);
        });

        assertEquals(400, exception.getCode());
        assertEquals("手机号已注册", exception.getMessage());
    }

    @Test
    void testGetUserById_Success() {
        UserResponse response = userService.getUserById(testUser.getId());

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("测试用户", response.getRealName());
    }

    @Test
    void testGetUserById_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(99999L);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testGetUserList_Success() {
        UserQueryRequest request = UserQueryRequest.builder()
                .page(1)
                .size(10)
                .sortField("createTime")
                .sortOrder("DESC")
                .build();

        UserListResponse response = userService.getUserList(request);

        assertNotNull(response);
        assertNotNull(response.getUsers());
        assertTrue(response.getTotal() >= 1);
        assertEquals(1, response.getPage());
        assertEquals(10, response.getSize());
    }

    @Test
    void testGetUserList_WithKeyword() {
        UserQueryRequest request = UserQueryRequest.builder()
                .page(1)
                .size(10)
                .keyword("test")
                .build();

        UserListResponse response = userService.getUserList(request);

        assertNotNull(response);
        assertNotNull(response.getUsers());
    }

    @Test
    void testGetUserList_WithStatus() {
        UserQueryRequest request = UserQueryRequest.builder()
                .page(1)
                .size(10)
                .status("ACTIVE")
                .build();

        UserListResponse response = userService.getUserList(request);

        assertNotNull(response);
        assertNotNull(response.getUsers());
    }

    @Test
    void testUpdateUser_Success() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .realName("更新后的用户名")
                .phone("010-87654321")
                .status("ACTIVE")
                .build();

        UserResponse response = userService.updateUser(testUser.getId(), request);

        assertNotNull(response);
        assertEquals("更新后的用户名", response.getRealName());
        assertEquals("010-87654321", response.getPhone());
    }

    @Test
    void testUpdateUser_NotFound() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .realName("更新后的用户名")
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(99999L, request);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testUpdateUser_UpdatePassword() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .password("NewPassword@123")
                .build();

        UserResponse response = userService.updateUser(testUser.getId(), request);

        assertNotNull(response);

        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches("NewPassword@123", updatedUser.getPassword()));
    }

    @Test
    void testPatchUser_Success() {
        UserPatchRequest request = UserPatchRequest.builder()
                .realName("部分更新的用户名")
                .build();

        UserResponse response = userService.patchUser(testUser.getId(), request);

        assertNotNull(response);
        assertEquals("部分更新的用户名", response.getRealName());
    }

    @Test
    void testPatchUser_UpdatePassword() {
        UserPatchRequest request = UserPatchRequest.builder()
                .password("NewPassword@123")
                .build();

        UserResponse response = userService.patchUser(testUser.getId(), request);

        assertNotNull(response);

        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches("NewPassword@123", updatedUser.getPassword()));
    }

    @Test
    void testDeleteUserById_Success() {
        userService.deleteUserById(testUser.getId());

        User deletedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(deletedUser);
        assertTrue(deletedUser.getIsDeleted());
        assertEquals("DELETED", deletedUser.getStatus());
    }

    @Test
    void testDeleteUserById_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.deleteUserById(99999L);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testCreateUser_WithInvalidEmail() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("NewUser@123")
                .realName("新用户")
                .mobile("13900139000")
                .email("invalid-email")
                .build();

        assertThrows(Exception.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testCreateUser_WithInvalidMobile() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("NewUser@123")
                .realName("新用户")
                .mobile("123")
                .email("newuser@example.com")
                .build();

        assertThrows(Exception.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testCreateUser_WithWeakPassword() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("weak")
                .realName("新用户")
                .mobile("13900139000")
                .email("newuser@example.com")
                .build();

        assertThrows(Exception.class, () -> {
            userService.createUser(request);
        });
    }
}
