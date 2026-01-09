package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.LoginRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginRecordService {

    LoginRecord recordLogin(Long userId, String userAccount, String userName, String loginType,
                         String loginIp, String loginLocation, String loginDevice,
                         String loginBrowser, String loginOs, String loginStatus,
                         String errorMsg, String riskLevel, String isKnownDevice);

    Page<LoginRecord> getUserLoginHistory(Long userId, Pageable pageable);

    Page<LoginRecord> getLoginHistoryByAccount(String userAccount, Pageable pageable);

    Page<LoginRecord> getLoginHistoryByStatus(String loginStatus, Pageable pageable);

    Page<LoginRecord> searchLoginRecords(Long userId, String userAccount, String loginStatus, Pageable pageable);

    List<LoginRecord> getRecentFailedLogins(LocalDateTime startTime);

    long countSuccessfulLoginsSince(Long userId, LocalDateTime startTime);
}
