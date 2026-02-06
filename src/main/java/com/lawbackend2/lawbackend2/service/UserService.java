package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.RefreshTokenResponse;
import com.lawbackend2.lawbackend2.dto.response.UserInfoResponse;
import com.lawbackend2.lawbackend2.dto.response.UserListResponse;
import com.lawbackend2.lawbackend2.dto.response.UserLoginResponse;
import com.lawbackend2.lawbackend2.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserLoginResponse register(UserRegisterRequest request);

    UserLoginResponse login(String username, String password, String smsCode, String ipAddress, String deviceInfo);

    void logout(String token);

    UserLoginResponse refreshToken(String token);

    RefreshTokenResponse refreshAccessToken(String refreshToken);

    void updateUserInfo(Long userId, String realName, String mobile, String email, String phone);

    void changePassword(Long userId, String oldPassword, String newPassword);

    void forgotPassword(String mobile, String smsCode, String newPassword);

    void updateUserStatus(Long userId, String status);

    void deleteUser(Long userId);

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    UserInfoResponse getCurrentUserInfo(Long userId);

    UserListResponse getUserList(UserQueryRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse patchUser(Long id, UserPatchRequest request);

    void deleteUserById(Long id);

    List<UserResponse> getAdminUsers();
}
