package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.SmsCode;
import com.lawbackend2.lawbackend2.repository.SmsCodeRepository;
import com.lawbackend2.lawbackend2.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SmsServiceImplTest {

    @Mock
    private SmsCodeRepository smsCodeRepository;

    @InjectMocks
    private SmsServiceImpl smsService;

    private static final String TEST_MOBILE = "13800138000";
    private static final String TEST_SMS_TYPE = "1";

    @BeforeEach
    void setUp() {
        when(smsCodeRepository.countRecentCodes(anyString(), any(LocalDateTime.class)))
                .thenReturn(0L);
    }

    @Test
    void testSendSmsCode_Success() {
        when(smsCodeRepository.countRecentCodes(anyString(), any(LocalDateTime.class))).thenReturn(0L);
        assertDoesNotThrow(() -> {
            smsService.sendSmsCode(TEST_MOBILE, TEST_SMS_TYPE);
        });

        verify(smsCodeRepository, times(1)).save(any(SmsCode.class));
    }

    @Test
    void testSendSmsCode_TooFrequent() {
        when(smsCodeRepository.countRecentCodes(anyString(), any(LocalDateTime.class)))
                .thenReturn(5L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            smsService.sendSmsCode(TEST_MOBILE, TEST_SMS_TYPE);
        });

        assertTrue(exception.getMessage().contains("发送过于频繁"));
        verify(smsCodeRepository, never()).save(any(SmsCode.class));
    }

    @Test
    void testGenerateSmsCode() {
        String code = smsService.generateSmsCode();

        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    void testVerifySmsCode_Success() {
        SmsCode smsCode = SmsCode.builder()
                .id(1L)
                .mobile(TEST_MOBILE)
                .code("123456")
                .smsType(TEST_SMS_TYPE)
                .expireTime(LocalDateTime.now().plusMinutes(5))
                .usedStatus('0')
                .build();

        when(smsCodeRepository.findLatestValidCode(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(smsCode));
        when(smsCodeRepository.markAsUsed(anyLong(), any(LocalDateTime.class)))
                .thenReturn(1);

        boolean result = smsService.verifySmsCode(TEST_MOBILE, "123456", TEST_SMS_TYPE);

        assertTrue(result);
        verify(smsCodeRepository, times(1)).markAsUsed(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void testVerifySmsCode_CodeNotFound() {
        when(smsCodeRepository.findLatestValidCode(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        boolean result = smsService.verifySmsCode(TEST_MOBILE, "123456", TEST_SMS_TYPE);

        assertFalse(result);
        verify(smsCodeRepository, never()).markAsUsed(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void testVerifySmsCode_WrongCode() {
        SmsCode smsCode = SmsCode.builder()
                .id(1L)
                .mobile(TEST_MOBILE)
                .code("123456")
                .smsType(TEST_SMS_TYPE)
                .expireTime(LocalDateTime.now().plusMinutes(5))
                .usedStatus('0')
                .build();

        when(smsCodeRepository.findLatestValidCode(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(smsCode));

        boolean result = smsService.verifySmsCode(TEST_MOBILE, "654321", TEST_SMS_TYPE);

        assertFalse(result);
        verify(smsCodeRepository, never()).markAsUsed(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void testVerifySmsCode_AlreadyUsed() {
        SmsCode smsCode = SmsCode.builder()
                .id(1L)
                .mobile(TEST_MOBILE)
                .code("123456")
                .smsType(TEST_SMS_TYPE)
                .expireTime(LocalDateTime.now().plusMinutes(5))
                .usedStatus('1')
                .build();

        when(smsCodeRepository.findLatestValidCode(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(smsCode));

        boolean result = smsService.verifySmsCode(TEST_MOBILE, "123456", TEST_SMS_TYPE);

        assertFalse(result);
        verify(smsCodeRepository, never()).markAsUsed(anyLong(), any(LocalDateTime.class));
    }
}
