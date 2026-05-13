package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.util.PasswordValidator;
import com.lawbackend2.lawbackend2.util.Sm4Encryptor;
import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.RefreshTokenResponse;
import com.lawbackend2.lawbackend2.dto.response.UserInfoResponse;
import com.lawbackend2.lawbackend2.dto.response.UserListResponse;
import com.lawbackend2.lawbackend2.dto.response.UserLoginResponse;
import com.lawbackend2.lawbackend2.dto.response.UserResponse;
import com.lawbackend2.lawbackend2.entity.LoginFail;
import com.lawbackend2.lawbackend2.entity.LoginRecord;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.Token;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LoginFailRepository;
import com.lawbackend2.lawbackend2.repository.LoginRecordRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.TokenRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.PermissionService;
import com.lawbackend2.lawbackend2.service.SmsService;
import com.lawbackend2.lawbackend2.service.SystemConfigService;
import com.lawbackend2.lawbackend2.service.TokenBlacklistService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.service.UserService;
import com.lawbackend2.lawbackend2.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private LoginFailRepository loginFailRepository;

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private Sm4Encryptor sm4Encryptor;

    @Value("${login.max-fail-count:5}")
    private int defaultMaxFailCount;

    @Value("${login.lock-minutes:30}")
    private int defaultLockMinutes;

    private int getMaxFailCount() {
        String val = systemConfigService.getConfigValue("security.login.max_attempts");
        if (val != null) {
            try { return Integer.parseInt(val); } catch (NumberFormatException ignored) {}
        }
        return defaultMaxFailCount;
    }

    private int getLockMinutes() {
        String val = systemConfigService.getConfigValue("security.login.lock_duration");
        if (val != null) {
            try { return Integer.parseInt(val); } catch (NumberFormatException ignored) {}
        }
        return defaultLockMinutes;
    }

    private static final String SMS_TYPE_REGISTER = "2";
    private static final String SMS_TYPE_LOGIN = "1";

    @Override
    @Transactional
    public UserLoginResponse register(UserRegisterRequest request) {
        PasswordValidator.validate(request.getPassword());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }

        String encryptedMobile = sm4Encryptor.encrypt(request.getMobile());
        if (userRepository.existsByMobile(encryptedMobile)) {
            throw new BusinessException(400, "手机号已注册");
        }

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "邮箱已注册");
        }

        boolean smsValid = smsService.verifySmsCode(request.getMobile(), request.getSmsCode(), SMS_TYPE_REGISTER);
        if (!smsValid) {
            throw new BusinessException(400, "短信验证码错误或已过期");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .realName(request.getRealName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .status("ACTIVE")
                .build();

        user = userRepository.save(user);
        log.info("用户注册成功 - 用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

        List<String> permissions = permissionService.getUserPermissions(user.getId());
        String accessToken = jwtTokenUtil.generateTokenWithPermissions(user.getId(), user.getUsername(), permissions);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUsername());

        saveToken(user.getId(), accessToken, "A", null, null);
        saveToken(user.getId(), refreshToken, "R", null, null);

        return generateLoginResponse(user, accessToken, refreshToken, permissions);
    }

    @Override
    @Transactional
    public UserLoginResponse login(String username, String password, String smsCode, String ipAddress, String deviceInfo) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (!userOpt.isPresent()) {
            recordLoginFailure(username, ipAddress, "ACCOUNT");
            throw new BusinessException(401, "用户名或密码错误");
        }

        User user = userOpt.get();

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(401, "账号已被禁用或锁定");
        }

        boolean isLocked = checkAccountLocked(username, ipAddress);
        if (isLocked) {
            throw new BusinessException(401, "账号已被锁定，请" + getLockMinutes() + "分钟后再试");
        }

        boolean passwordValid = passwordEncoder.matches(password, user.getPassword());

        if (!passwordValid) {
            recordLoginFailure(username, ipAddress, "PASSWORD");
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (smsCode != null && smsCode.matches("\\d{4,6}")) {
            boolean smsValid = smsService.verifySmsCode(user.getMobile(), smsCode, SMS_TYPE_LOGIN);
            if (!smsValid) {
                recordLoginFailure(username, ipAddress, "CAPTCHA");
                throw new BusinessException(401, "短信验证码错误或已过期");
            }
        }

        resetLoginFailures(username, ipAddress);

        List<String> permissions = permissionService.getUserPermissions(user.getId());
        String accessToken = jwtTokenUtil.generateTokenWithPermissions(user.getId(), username, permissions);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), username);

        saveToken(user.getId(), accessToken, "A", ipAddress, deviceInfo);
        saveToken(user.getId(), refreshToken, "R", ipAddress, deviceInfo);

        updateLoginInfo(user, ipAddress);

        recordLoginSuccess(user, ipAddress, deviceInfo);

        log.info("用户登录成功 - 用户ID: {}, 用户名: {}, IP: {}, 权限数量: {}", user.getId(), user.getUsername(), ipAddress, permissions.size());

        return generateLoginResponse(user, accessToken, refreshToken, permissions);
    }

    @Override
    @Transactional
    public void logout(String token) {
        Optional<Token> tokenOpt = tokenRepository.findByTokenValue(token);
        if (tokenOpt.isPresent()) {
            Token tokenEntity = tokenOpt.get();
            Long userId = tokenEntity.getUserId();

            tokenEntity.setStatus("INACTIVE");
            tokenEntity.setRevokeTime(LocalDateTime.now());
            tokenRepository.save(tokenEntity);
            tokenBlacklistService.addToBlacklist(token);

            List<Token> activeTokens = tokenRepository.findActiveTokensByUserId(userId, LocalDateTime.now());
            for (Token activeToken : activeTokens) {
                activeToken.setStatus("INACTIVE");
                activeToken.setRevokeTime(LocalDateTime.now());
                tokenRepository.save(activeToken);
                tokenBlacklistService.addToBlacklist(activeToken.getTokenValue());
            }

            log.info("用户登出成功 - 用户ID: {}", userId);
        }
    }

    @Override
    @Transactional
    public UserLoginResponse refreshToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            throw new BusinessException(401, "Token无效或已过期");
        }

        if (!jwtTokenUtil.isRefreshToken(token)) {
            throw new BusinessException(400, "令牌类型错误，需要刷新令牌");
        }

        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(401, "账号已被禁用或锁定");
        }

        Optional<Token> oldTokenOpt = tokenRepository.findByTokenValue(token);
        if (oldTokenOpt.isPresent()) {
            Token oldToken = oldTokenOpt.get();
            oldToken.setStatus("INACTIVE");
            oldToken.setRevokeTime(LocalDateTime.now());
            tokenRepository.save(oldToken);
            tokenBlacklistService.addToBlacklist(token);
        }

        String newAccessToken = jwtTokenUtil.generateTokenWithPermissions(userId, username, 
                permissionService.getUserPermissions(userId));
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userId, username);

        saveToken(userId, newAccessToken, "A", null, null);
        saveToken(userId, newRefreshToken, "R", null, null);

        log.info("Token刷新成功 - 用户ID: {}", userId);

        return generateLoginResponse(user, newAccessToken, newRefreshToken, 
                permissionService.getUserPermissions(userId));
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new BusinessException(401, "刷新令牌无效或已过期");
        }

        if (!jwtTokenUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(400, "令牌类型错误，需要刷新令牌");
        }

        Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(401, "账号已被禁用或锁定");
        }

        Optional<Token> oldRefreshTokenOpt = tokenRepository.findByTokenValue(refreshToken);
        if (oldRefreshTokenOpt.isPresent()) {
            Token oldRefreshToken = oldRefreshTokenOpt.get();
            if (!"ACTIVE".equals(oldRefreshToken.getStatus())) {
                throw new BusinessException(401, "刷新令牌已失效");
            }
        }

        List<String> userPermissions = permissionService.getUserPermissions(userId);
        String newAccessToken = jwtTokenUtil.generateTokenWithPermissions(userId, username, userPermissions);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userId, username);

        saveToken(userId, newAccessToken, "A", null, null);
        saveToken(userId, newRefreshToken, "R", null, null);

        if (oldRefreshTokenOpt.isPresent()) {
            Token oldRefreshToken = oldRefreshTokenOpt.get();
            oldRefreshToken.setStatus("INACTIVE");
            oldRefreshToken.setRevokeTime(LocalDateTime.now());
            tokenRepository.save(oldRefreshToken);
            tokenBlacklistService.addToBlacklist(refreshToken);
        }

        log.info("访问令牌刷新成功 - 用户ID: {}", userId);

        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .permissions(userPermissions)
                .build();
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, String realName, String mobile, String email, String phone) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();

        if (mobile != null && !mobile.equals(user.getMobile())) {
            String encryptedMobile = sm4Encryptor.encrypt(mobile);
            if (userRepository.existsByMobile(encryptedMobile)) {
                throw new BusinessException(400, "手机号已被使用");
            }
        }

        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new BusinessException(400, "邮箱已被使用");
        }

        if (realName != null) {
            user.setRealName(realName);
        }
        if (mobile != null) {
            user.setMobile(mobile);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phone != null) {
            user.setPhone(phone);
        }

        userRepository.save(user);
        log.info("用户信息更新成功 - 用户ID: {}", userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        PasswordValidator.validate(newPassword);

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "原密码错误，请重新输入");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        user.setPwdErrorCount(0);
        user.setPwdErrorTime(null);

        userRepository.save(user);

        tokenRepository.revokeAllTokensByUserId(userId, LocalDateTime.now());

        log.info("用户密码修改成功 - 用户 ID: {}", userId);
    }

    @Override
    @Transactional
    public void forgotPassword(String mobile, String smsCode, String newPassword) {
        PasswordValidator.validate(newPassword);

        boolean smsValid = smsService.verifySmsCode(mobile, smsCode, "3");
        if (!smsValid) {
            throw new BusinessException(400, "短信验证码错误或已过期");
        }

        // 查找用户
        String encryptedMobile = sm4Encryptor.encrypt(mobile);
        Optional<User> userOpt = userRepository.findByMobile(encryptedMobile);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "该手机号未注册");
        }

        User user = userOpt.get();

        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(400, "账号已被禁用或锁定");
        }

        // 加密新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        user.setPwdErrorCount(0);
        user.setPwdErrorTime(null);

        userRepository.save(user);

        // 撤销该用户的所有Token
        tokenRepository.revokeAllTokensByUserId(user.getId(), LocalDateTime.now());

        log.info("用户通过忘记密码重置密码成功 - 用户ID: {}, 手机号: {}", user.getId(), mobile);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        user.setIsDeleted(true);
        user.setStatus("DELETED");
        userRepository.save(user);

        tokenRepository.revokeAllTokensByUserId(userId, LocalDateTime.now());

        log.info("用户删除成功 - 用户ID: {}", userId);
    }

    private UserLoginResponse generateLoginResponse(User user, String accessToken, String refreshToken, List<String> permissions) {
        if (accessToken == null && permissions != null) {
            accessToken = jwtTokenUtil.generateTokenWithPermissions(user.getId(), user.getUsername(), permissions);
        } else if (accessToken == null) {
            List<String> userPerms = permissionService.getUserPermissions(user.getId());
            accessToken = jwtTokenUtil.generateTokenWithPermissions(user.getId(), user.getUsername(), userPerms);
        }

        if (refreshToken == null) {
            refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUsername());
        }

        if (permissions == null) {
            permissions = permissionService.getUserPermissions(user.getId());
        }

        return UserLoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .permissions(permissions)
                .build();
    }

    private void saveToken(Long userId, String token, String tokenType, String ipAddress, String deviceInfo) {
        Long expiration = "A".equals(tokenType) ? jwtTokenUtil.getAccessTokenExpiration() : jwtTokenUtil.getRefreshTokenExpiration();
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(expiration / 1000);

        Token tokenEntity = Token.builder()
                .userId(userId)
                .tokenType(tokenType)
                .tokenValue(token)
                .ipAddress(ipAddress)
                .deviceInfo(deviceInfo)
                .expireTime(expireTime)
                .status("ACTIVE")
                .build();

        tokenRepository.save(tokenEntity);
    }

    private void updateLoginInfo(User user, String ipAddress) {
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user.setLoginCount(user.getLoginCount() + 1);
        user.setPwdErrorCount(0);
        user.setPwdErrorTime(null);
        userRepository.save(user);
    }

    private void recordLoginSuccess(User user, String ipAddress, String deviceInfo) {
        LoginRecord record = LoginRecord.builder()
                .userId(user.getId())
                .userAccount(user.getUsername())
                .userName(user.getRealName())
                .loginType("PASSWORD")
                .loginIp(ipAddress)
                .loginDevice(deviceInfo)
                .loginStatus("SUCCESS")
                .loginTime(LocalDateTime.now())
                .build();

        loginRecordRepository.save(record);
    }

    private void recordLoginFailure(String username, String ipAddress, String failType) {
        Optional<LoginFail> loginFailOpt = loginFailRepository.findByLoginAccountAndFailIp(username, ipAddress);

        LoginFail loginFail;
        if (loginFailOpt.isPresent()) {
            loginFail = loginFailOpt.get();
            loginFail.setFailCount(loginFail.getFailCount() + 1);
            loginFail.setLastFailTime(LocalDateTime.now());
            loginFail.setFailType(failType);
        } else {
            loginFail = LoginFail.builder()
                    .loginAccount(username)
                    .failIp(ipAddress)
                    .failCount(1)
                    .failType(failType)
                    .lastFailTime(LocalDateTime.now())
                    .build();
        }

        loginFailRepository.save(loginFail);

        log.warn("登录失败记录 - 用户名: {}, IP: {}, 失败次数: {}, 失败类型: {}",
                username, ipAddress, loginFail.getFailCount(), failType);
    }

    private boolean checkAccountLocked(String username, String ipAddress) {
        return loginFailRepository.isAccountLocked(username, ipAddress, getMaxFailCount(), LocalDateTime.now());
    }

    private void resetLoginFailures(String username, String ipAddress) {
        Optional<LoginFail> loginFailOpt = loginFailRepository.findByLoginAccountAndFailIp(username, ipAddress);
        if (loginFailOpt.isPresent()) {
            LoginFail loginFail = loginFailOpt.get();
            loginFailRepository.resetFailCount(loginFail.getId(), LocalDateTime.now());
        }
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        PasswordValidator.validate(request.getPassword());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }

        if (request.getMobile() != null) {
            String encryptedMobile = sm4Encryptor.encrypt(request.getMobile());
            if (userRepository.existsByMobile(encryptedMobile)) {
                throw new BusinessException(400, "手机号已注册");
            }
        }

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "邮箱已注册");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .realName(request.getRealName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        user = userRepository.save(user);
        log.info("用户创建成功 - 用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

        return convertToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        return convertToUserResponse(user);
    }

    @Override
    public UserInfoResponse getCurrentUserInfo(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        List<Role> roles = roleRepository.findAllById(roleIds);
        List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        List<String> permissions = permissionService.getUserPermissions(userId);

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(roleNames)
                .permissions(permissions)
                .build();
    }

    @Override
    public UserListResponse getUserList(UserQueryRequest request) {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(request.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSortField()
        );

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        Page<User> userPage;
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            userPage = userRepository.searchByKeyword(request.getKeyword(), pageable);
        } else if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            List<User> users = userRepository.findByStatus(request.getStatus());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), users.size());
            List<User> pageContent = users.subList(start, end);
            userPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, users.size());
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> userResponses = userPage.getContent().stream()
                .filter(user -> !user.getIsDeleted())
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());

        return UserListResponse.builder()
                .total(userPage.getTotalElements())
                .page(request.getPage())
                .size(request.getSize())
                .totalPages(userPage.getTotalPages())
                .users(userResponses)
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException(400, "用户名已存在");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getMobile() != null && !request.getMobile().equals(user.getMobile())) {
            String encryptedMobile = sm4Encryptor.encrypt(request.getMobile());
            if (userRepository.existsByMobile(encryptedMobile)) {
                throw new BusinessException(400, "手机号已被使用");
            }
            user.setMobile(request.getMobile());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(400, "邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            PasswordValidator.validate(request.getPassword());
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
            user.setPwdErrorCount(0);
            user.setPwdErrorTime(null);
        }

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        user = userRepository.save(user);
        log.info("用户更新成功 - 用户ID: {}", id);

        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse patchUser(Long id, UserPatchRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException(400, "用户名已存在");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getMobile() != null && !request.getMobile().equals(user.getMobile())) {
            String encryptedMobile = sm4Encryptor.encrypt(request.getMobile());
            if (userRepository.existsByMobile(encryptedMobile)) {
                throw new BusinessException(400, "手机号已被使用");
            }
            user.setMobile(request.getMobile());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(400, "邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            PasswordValidator.validate(request.getPassword());
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
            user.setPwdErrorCount(0);
            user.setPwdErrorTime(null);
        }

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        user = userRepository.save(user);
        log.info("用户部分更新成功 - 用户ID: {}", id);

        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setIsDeleted(true);
        user.setStatus("DELETED");
        userRepository.save(user);

        tokenRepository.revokeAllTokensByUserId(id, LocalDateTime.now());

        log.info("用户删除成功 - 用户ID: {}", id);
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
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
                .build();
    }

    @Override
    public List<UserResponse> getAdminUsers() {
        Optional<Role> adminRoleOpt = roleRepository.findByRoleCode("ADMIN");
        if (!adminRoleOpt.isPresent()) {
            return Collections.emptyList();
        }
        
        Long adminRoleId = adminRoleOpt.get().getId();
        
        List<Long> userIds = userRoleRepository.findUserIdsByRoleId(adminRoleId);
        
        List<User> users = userRepository.findAllById(userIds);
        
        return users.stream()
                .filter(user -> !user.getIsDeleted())
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        return convertToUserResponse(user);
    }

    @Override
    public UserResponse getUserByMobile(String mobile) {
        String encryptedMobile = sm4Encryptor.encrypt(mobile);
        Optional<User> userOpt = userRepository.findByMobile(encryptedMobile);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, String status) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setStatus(status);
        user = userRepository.save(user);

        if ("LOCKED".equals(status) || "INACTIVE".equals(status)) {
            tokenRepository.revokeAllTokensByUserId(id, LocalDateTime.now());
        }

        log.info("用户状态更新成功 - 用户ID: {}, 新状态: {}", id, status);
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserMobile(Long id, String mobile, String smsCode) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        if (!mobile.equals(user.getMobile())) {
            String encryptedMobile = sm4Encryptor.encrypt(mobile);
            if (userRepository.existsByMobile(encryptedMobile)) {
                throw new BusinessException(400, "手机号已被使用");
            }
        }

        if (smsCode != null && !smsCode.isEmpty()) {
            boolean smsValid = smsService.verifySmsCode(mobile, smsCode, "4");
            if (!smsValid) {
                throw new BusinessException(400, "短信验证码错误或已过期");
            }
        }

        user.setMobile(mobile);
        user = userRepository.save(user);

        log.info("用户手机号更新成功 - 用户ID: {}, 新手机号: {}", id, mobile);
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserEmail(Long id, String email) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new BusinessException(400, "邮箱已被使用");
        }

        user.setEmail(email);
        user = userRepository.save(user);

        log.info("用户邮箱更新成功 - 用户 ID: {}, 新邮箱：{}", id, email);
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserRealName(Long id, String realName) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = userOpt.get();
        if (user.getIsDeleted()) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setRealName(realName);
        user = userRepository.save(user);

        log.info("用户真实姓名更新成功 - 用户 ID: {}, 新姓名：{}", id, realName);
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public void batchUpdateUserStatus(List<Long> userIds, String status) {
        List<User> users = userRepository.findAllById(userIds);
        
        for (User user : users) {
            if (!user.getIsDeleted()) {
                user.setStatus(status);
                if ("LOCKED".equals(status) || "INACTIVE".equals(status)) {
                    tokenRepository.revokeAllTokensByUserId(user.getId(), LocalDateTime.now());
                }
            }
        }
        
        userRepository.saveAll(users);
        log.info("批量更新用户状态成功 - 用户数量: {}, 新状态: {}", userIds.size(), status);
    }
}
