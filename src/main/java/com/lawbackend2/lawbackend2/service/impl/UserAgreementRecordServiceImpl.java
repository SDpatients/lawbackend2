package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.UserAgreementRecord;
import com.lawbackend2.lawbackend2.repository.UserAgreementRecordRepository;
import com.lawbackend2.lawbackend2.service.UserAgreementRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAgreementRecordServiceImpl implements UserAgreementRecordService {

    private final UserAgreementRecordRepository repository;

    @Override
    @Transactional
    public UserAgreementRecord recordAgreement(Long userId, String userAccount, String agreementType,
                                                String agreementVersion, String agreementContent,
                                                String ipAddress) {
        UserAgreementRecord record = UserAgreementRecord.builder()
                .userId(userId)
                .userAccount(userAccount)
                .agreementType(agreementType)
                .agreementVersion(agreementVersion)
                .agreementContent(agreementContent)
                .agreed(true)
                .ipAddress(ipAddress)
                .agreeTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUserId(userId)
                .updateUserId(userId)
                .build();

        UserAgreementRecord saved = repository.save(record);
        log.info("用户 {} 同意了 {} 版本 {}, 记录ID: {}", userAccount, agreementType, agreementVersion, saved.getId());
        return saved;
    }

    @Override
    public List<UserAgreementRecord> getUserAgreementHistory(Long userId) {
        return repository.findByUserIdAndIsDeletedFalseOrderByAgreeTimeDesc(userId);
    }

    @Override
    public UserAgreementRecord getLatestAgreement(Long userId, String agreementType) {
        return repository.findTopByUserIdAndAgreementTypeAndIsDeletedFalseOrderByAgreeTimeDesc(userId, agreementType)
                .orElse(null);
    }

    @Override
    public boolean hasAgreed(Long userId, String agreementType) {
        return repository.existsByUserIdAndAgreementTypeAndIsDeletedFalse(userId, agreementType);
    }

    @Override
    public UserAgreementRecord getLatestByTypeAndVersion(Long userId, String agreementType, String agreementVersion) {
        return repository.findLatestByUserIdAndTypeAndVersion(userId, agreementType, agreementVersion)
                .orElse(null);
    }

    @Override
    public Map<String, Boolean> checkAllAgreements(Long userId) {
        Map<String, Boolean> result = new HashMap<>();
        result.put(UserAgreementRecord.TYPE_PRIVACY_POLICY,
                repository.existsByUserIdAndAgreementTypeAndIsDeletedFalse(userId, UserAgreementRecord.TYPE_PRIVACY_POLICY));
        result.put(UserAgreementRecord.TYPE_USER_AGREEMENT,
                repository.existsByUserIdAndAgreementTypeAndIsDeletedFalse(userId, UserAgreementRecord.TYPE_USER_AGREEMENT));
        return result;
    }
}