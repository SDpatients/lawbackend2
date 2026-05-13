package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.UserAgreementRecord;

import java.util.List;
import java.util.Map;

public interface UserAgreementRecordService {

    UserAgreementRecord recordAgreement(Long userId, String userAccount, String agreementType,
                                        String agreementVersion, String agreementContent,
                                        String ipAddress);

    List<UserAgreementRecord> getUserAgreementHistory(Long userId);

    UserAgreementRecord getLatestAgreement(Long userId, String agreementType);

    boolean hasAgreed(Long userId, String agreementType);

    UserAgreementRecord getLatestByTypeAndVersion(Long userId, String agreementType, String agreementVersion);

    Map<String, Boolean> checkAllAgreements(Long userId);
}