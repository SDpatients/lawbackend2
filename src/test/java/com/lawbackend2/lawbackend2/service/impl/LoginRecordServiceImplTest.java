package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.LoginRecord;
import com.lawbackend2.lawbackend2.repository.LoginRecordRepository;
import com.lawbackend2.lawbackend2.service.LoginRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginRecordServiceImplTest {

    @Mock
    private LoginRecordRepository loginRecordRepository;

    @InjectMocks
    private LoginRecordServiceImpl loginRecordService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_ACCOUNT = "testuser";
    private static final String TEST_USER_NAME = "测试用户";
    private static final String TEST_LOGIN_IP = "127.0.0.1";
    private static final Long TEST_LOGIN_RECORD_ID = 1L;

    @BeforeEach
    void setUp() {
        LoginRecord mockRecord = LoginRecord.builder()
                .id(TEST_LOGIN_RECORD_ID)
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .loginType("PASSWORD")
                .loginIp(TEST_LOGIN_IP)
                .loginStatus("SUCCESS")
                .loginTime(LocalDateTime.now())
                .build();

        when(loginRecordRepository.save(any(LoginRecord.class))).thenReturn(mockRecord);
        when(loginRecordRepository.findByUserId(eq(TEST_USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRecord), PageRequest.of(0, 10), 1));
        when(loginRecordRepository.findByUserAccount(eq(TEST_USER_ACCOUNT), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRecord), PageRequest.of(0, 10), 1));
        when(loginRecordRepository.findByLoginStatus(eq("SUCCESS"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRecord), PageRequest.of(0, 10), 1));
        when(loginRecordRepository.searchLoginRecords(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRecord), PageRequest.of(0, 10), 1));
        when(loginRecordRepository.findRecentFailedLogins(any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(loginRecordRepository.countSuccessfulLoginsSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(5L);
    }

    @Test
    void testRecordLogin_Success() {
        LoginRecord record = loginRecordService.recordLogin(
                TEST_USER_ID, TEST_USER_ACCOUNT, TEST_USER_NAME, "PASSWORD",
                TEST_LOGIN_IP, "本地", "PC", "Chrome", "Windows",
                "SUCCESS", null, "LOW", "YES"
        );

        assertNotNull(record);
        assertEquals(TEST_USER_ID, record.getUserId());
        assertEquals(TEST_USER_ACCOUNT, record.getUserAccount());
        assertEquals("SUCCESS", record.getLoginStatus());
        assertEquals(TEST_LOGIN_IP, record.getLoginIp());
        verify(loginRecordRepository, times(1)).save(any(LoginRecord.class));
    }

    @Test
    void testRecordLogin_Failed() {
        LoginRecord failedRecord = LoginRecord.builder()
                .id(TEST_LOGIN_RECORD_ID)
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .loginType("PASSWORD")
                .loginIp(TEST_LOGIN_IP)
                .loginStatus("FAILED")
                .errorMsg("密码错误")
                .riskLevel("HIGH")
                .loginTime(LocalDateTime.now())
                .build();
        
        when(loginRecordRepository.save(any(LoginRecord.class))).thenReturn(failedRecord);
        
        LoginRecord record = loginRecordService.recordLogin(
                TEST_USER_ID, TEST_USER_ACCOUNT, TEST_USER_NAME, "PASSWORD",
                TEST_LOGIN_IP, null, null, null, null,
                "FAILED", "密码错误", "HIGH", "NO"
        );

        assertNotNull(record);
        assertEquals("FAILED", record.getLoginStatus());
        assertEquals("密码错误", record.getErrorMsg());
        assertEquals("HIGH", record.getRiskLevel());
        verify(loginRecordRepository, times(1)).save(any(LoginRecord.class));
    }

    @Test
    void testGetUserLoginHistory_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoginRecord> page = loginRecordService.getUserLoginHistory(TEST_USER_ID, pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals(TEST_USER_ACCOUNT, page.getContent().get(0).getUserAccount());
        verify(loginRecordRepository, times(1)).findByUserId(TEST_USER_ID, pageable);
    }

    @Test
    void testGetLoginHistoryByAccount_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoginRecord> page = loginRecordService.getLoginHistoryByAccount(TEST_USER_ACCOUNT, pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals(TEST_USER_ACCOUNT, page.getContent().get(0).getUserAccount());
        verify(loginRecordRepository, times(1)).findByUserAccount(TEST_USER_ACCOUNT, pageable);
    }

    @Test
    void testGetLoginHistoryByStatus_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoginRecord> page = loginRecordService.getLoginHistoryByStatus("SUCCESS", pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals("SUCCESS", page.getContent().get(0).getLoginStatus());
        verify(loginRecordRepository, times(1)).findByLoginStatus("SUCCESS", pageable);
    }

    @Test
    void testSearchLoginRecords_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LoginRecord> page = loginRecordService.searchLoginRecords(
                TEST_USER_ID, TEST_USER_ACCOUNT, "SUCCESS", pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(loginRecordRepository, times(1)).searchLoginRecords(TEST_USER_ID, TEST_USER_ACCOUNT, "SUCCESS", pageable);
    }

    @Test
    void testGetRecentFailedLogins_Success() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        List<LoginRecord> failedLogins = loginRecordService.getRecentFailedLogins(startTime);

        assertNotNull(failedLogins);
        verify(loginRecordRepository, times(1)).findRecentFailedLogins(startTime);
    }

    @Test
    void testCountSuccessfulLoginsSince_Success() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        long count = loginRecordService.countSuccessfulLoginsSince(TEST_USER_ID, startTime);

        assertEquals(5L, count);
        verify(loginRecordRepository, times(1)).countSuccessfulLoginsSince(TEST_USER_ID, startTime);
    }
}
