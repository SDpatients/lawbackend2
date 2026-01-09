package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.SmsCode;
import com.lawbackend2.lawbackend2.repository.SmsCodeRepository;
import com.lawbackend2.lawbackend2.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsCodeRepository smsCodeRepository;

    @Value("${sms.expire-minutes:5}")
    private int expireMinutes;

    @Value("${sms.max-attempts:5}")
    private int maxAttempts;

    private static final String SMS_TYPE_LOGIN = "1";
    private static final String SMS_TYPE_REGISTER = "2";
    private static final String SMS_TYPE_RESET_PASSWORD = "3";

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public void sendSmsCode(String mobile, String smsType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusMinutes(1);

        long recentCount = smsCodeRepository.countRecentCodes(mobile, startTime);
        if (recentCount >= maxAttempts) {
            throw new RuntimeException("发送过于频繁，请稍后再试");
        }

        String code = generateSmsCode();
        LocalDateTime expireTime = now.plusMinutes(expireMinutes);

        SmsCode smsCode = SmsCode.builder()
                .mobile(mobile)
                .code(code)
                .smsType(smsType)
                .expireTime(expireTime)
                .usedStatus('0')
                .build();

        smsCodeRepository.save(smsCode);

        String smsTypeDesc = getSmsTypeDesc(smsType);
        log.info("发送{}验证码成功 - 手机号: {}, 验证码: {}, 过期时间: {}", smsTypeDesc, mobile, code, expireTime);

        sendRealSms(mobile, code, smsTypeDesc);
    }

    @Override
    @Transactional
    public boolean verifySmsCode(String mobile, String code, String smsType) {
        LocalDateTime now = LocalDateTime.now();

        Optional<SmsCode> latestCodeOpt = smsCodeRepository.findLatestValidCode(mobile, smsType, now);

        if (!latestCodeOpt.isPresent()) {
            log.warn("验证码验证失败 - 手机号: {}, 原因: 验证码不存在或已过期", mobile);
            return false;
        }

        SmsCode smsCode = latestCodeOpt.get();

        if (!smsCode.getCode().equals(code)) {
            log.warn("验证码验证失败 - 手机号: {}, 原因: 验证码错误", mobile);
            return false;
        }

        if (smsCode.getUsedStatus() == '1') {
            log.warn("验证码验证失败 - 手机号: {}, 原因: 验证码已使用", mobile);
            return false;
        }

        smsCodeRepository.markAsUsed(smsCode.getId(), now);
        log.info("验证码验证成功 - 手机号: {}", mobile);

        return true;
    }

    @Override
    public String generateSmsCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    private void sendRealSms(String mobile, String code, String smsTypeDesc) {
        log.info("模拟发送短信 - 手机号: {}, 验证码: {}, 类型: {}", mobile, code, smsTypeDesc);
    }

    private String getSmsTypeDesc(String smsType) {
        switch (smsType) {
            case SMS_TYPE_LOGIN:
                return "登录";
            case SMS_TYPE_REGISTER:
                return "注册";
            case SMS_TYPE_RESET_PASSWORD:
                return "找回密码";
            default:
                return "未知";
        }
    }
}
