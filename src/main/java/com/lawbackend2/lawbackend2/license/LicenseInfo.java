package com.lawbackend2.lawbackend2.license;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class LicenseInfo {

    private String licenseId;

    private String customerName;

    private String machineCode;

    @Builder.Default
    private List<String> modules = new ArrayList<>();

    private Integer maxUsers;

    private LocalDateTime expireDate;

    private LocalDateTime createTime;

    private String signature;

    public boolean isExpired() {
        if (expireDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expireDate);
    }

    public boolean hasModule(String module) {
        if (modules == null || module == null) {
            return false;
        }
        return modules.contains(module);
    }

    public long getRemainingDays() {
        if (expireDate == null) {
            return Long.MAX_VALUE;
        }
        if (isExpired()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), expireDate);
    }

    public void validate() throws LicenseValidationException {
        if (licenseId == null || licenseId.isEmpty()) {
            throw new LicenseValidationException("许可证ID不能为空");
        }
        if (customerName == null || customerName.isEmpty()) {
            throw new LicenseValidationException("客户名称不能为空");
        }
        if (machineCode == null || machineCode.isEmpty()) {
            throw new LicenseValidationException("机器码不能为空");
        }
        if (signature == null || signature.isEmpty()) {
            throw new LicenseValidationException("许可证签名不能为空");
        }
        if (isExpired()) {
            throw new LicenseValidationException("许可证已于 " + expireDate + " 过期");
        }
    }

    public static class LicenseValidationException extends Exception {
        public LicenseValidationException(String message) {
            super(message);
        }
    }
}
