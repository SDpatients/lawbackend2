package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.SensitiveDataViewRequest;
import com.lawbackend2.lawbackend2.dto.response.SensitiveDataViewResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.CreditorClaim;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.enums.SensitiveDataType;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.CreditorClaimRepository;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveDataService {

    private final UserRepository userRepository;
    private final CreditorInfoRepository creditorInfoRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CreditorClaimRepository creditorClaimRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public SensitiveDataViewResponse getPlainTextData(SensitiveDataViewRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(401, "未登录"));

        boolean passwordValid = passwordEncoder.matches(
                request.getPassword(),
                currentUser.getPassword()
        );
        if (!passwordValid) {
            throw new BusinessException(400, "密码错误");
        }

        String plainText = fetchData(request.getDataType(), request.getId());
        log.info("用户 {} 查看敏感数据: {}", currentUser.getUsername(), request.getDataType());

        return SensitiveDataViewResponse.builder()
                .plainTextValue(plainText)
                .build();
    }

    private String fetchData(SensitiveDataType dataType, Long id) {
        if (dataType == SensitiveDataType.USER_MOBILE) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "用户不存在"));
            return user.getMobile();
        }
        if (dataType == SensitiveDataType.USER_PHONE) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "用户不存在"));
            return user.getPhone();
        }
        if (dataType == SensitiveDataType.CREDITOR_ID_NUMBER) {
            CreditorInfo creditor = creditorInfoRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "债权人不存在"));
            return creditor.getIdNumber();
        }
        if (dataType == SensitiveDataType.CREDITOR_CONTACT_PHONE) {
            CreditorInfo creditor = creditorInfoRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "债权人不存在"));
            return creditor.getContactPhone();
        }
        if (dataType == SensitiveDataType.BANK_ACCOUNT_NUMBER) {
            BankAccount account = bankAccountRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "银行账户不存在"));
            return account.getAccountNumber();
        }
        if (dataType == SensitiveDataType.AGENT_PHONE) {
            CreditorClaim claim = creditorClaimRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "债权不存在"));
            return claim.getAgentPhone();
        }
        if (dataType == SensitiveDataType.AGENT_ID_CARD) {
            CreditorClaim claim = creditorClaimRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "债权不存在"));
            return claim.getAgentIdCard();
        }
        if (dataType == SensitiveDataType.CREDITOR_BANK_ACCOUNT) {
            CreditorClaim claim = creditorClaimRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "债权不存在"));
            return claim.getCreditorBankAccount();
        }
        throw new BusinessException(400, "不支持的数据类型: " + dataType);
    }
}