package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.LoginRecord;
import com.lawbackend2.lawbackend2.repository.LoginRecordRepository;
import com.lawbackend2.lawbackend2.service.LoginRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LoginRecordServiceImpl implements LoginRecordService {

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Override
    @Transactional
    public LoginRecord recordLogin(Long userId, String userAccount, String userName, String loginType,
                                  String loginIp, String loginLocation, String loginDevice,
                                  String loginBrowser, String loginOs, String loginStatus,
                                  String errorMsg, String riskLevel, String isKnownDevice) {
        LoginRecord loginRecord = LoginRecord.builder()
                .userId(userId)
                .userAccount(userAccount)
                .userName(userName)
                .loginType(loginType)
                .loginIp(loginIp)
                .loginLocation(loginLocation)
                .loginDevice(loginDevice)
                .loginBrowser(loginBrowser)
                .loginOs(loginOs)
                .loginStatus(loginStatus)
                .errorMsg(errorMsg)
                .riskLevel(riskLevel)
                .isKnownDevice(isKnownDevice)
                .loginTime(LocalDateTime.now())
                .build();

        LoginRecord savedRecord = loginRecordRepository.save(loginRecord);
        log.info("记录登录日志成功 - 用户ID: {}, 账号: {}, 状态: {}, IP: {}", 
                userId, userAccount, loginStatus, loginIp);
        return savedRecord;
    }

    @Override
    public Page<LoginRecord> getUserLoginHistory(Long userId, Pageable pageable) {
        Page<LoginRecord> page = loginRecordRepository.findByUserId(userId, pageable);
        log.debug("查询用户登录历史 - 用户ID: {}, 总数: {}", userId, page.getTotalElements());
        return page;
    }

    @Override
    public Page<LoginRecord> getLoginHistoryByAccount(String userAccount, Pageable pageable) {
        Page<LoginRecord> page = loginRecordRepository.findByUserAccount(userAccount, pageable);
        log.debug("查询账号登录历史 - 账号: {}, 总数: {}", userAccount, page.getTotalElements());
        return page;
    }

    @Override
    public Page<LoginRecord> getLoginHistoryByStatus(String loginStatus, Pageable pageable) {
        Page<LoginRecord> page = loginRecordRepository.findByLoginStatus(loginStatus, pageable);
        log.debug("查询登录历史(按状态) - 状态: {}, 总数: {}", loginStatus, page.getTotalElements());
        return page;
    }

    @Override
    public Page<LoginRecord> searchLoginRecords(Long userId, String userAccount, String loginStatus, Pageable pageable) {
        Page<LoginRecord> page = loginRecordRepository.searchLoginRecords(userId, userAccount, loginStatus, pageable);
        log.debug("搜索登录历史 - 用户ID: {}, 账号: {}, 状态: {}, 总数: {}", 
                userId, userAccount, loginStatus, page.getTotalElements());
        return page;
    }

    @Override
    public List<LoginRecord> getRecentFailedLogins(LocalDateTime startTime) {
        List<LoginRecord> failedLogins = loginRecordRepository.findRecentFailedLogins(startTime);
        log.debug("查询最近失败登录记录 - 开始时间: {}, 数量: {}", startTime, failedLogins.size());
        return failedLogins;
    }

    @Override
    public long countSuccessfulLoginsSince(Long userId, LocalDateTime startTime) {
        long count = loginRecordRepository.countSuccessfulLoginsSince(userId, startTime);
        log.debug("统计成功登录次数 - 用户ID: {}, 开始时间: {}, 数量: {}", userId, startTime, count);
        return count;
    }
}
