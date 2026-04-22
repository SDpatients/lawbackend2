package com.lawbackend2.lawbackend2.license.aspect;

import com.lawbackend2.lawbackend2.license.LicenseInfo;
import com.lawbackend2.lawbackend2.license.LicenseService;
import com.lawbackend2.lawbackend2.license.annotation.RequireLicense;
import com.lawbackend2.lawbackend2.license.exception.LicenseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LicenseAspect {

    private final LicenseService licenseService;

    @Around("@annotation(requireLicense)")
    public Object around(ProceedingJoinPoint point, RequireLicense requireLicense) throws Throwable {
        String[] modules = requireLicense.modules();
        String methodName = getMethodName(point);

        if (!licenseService.isValid()) {
            String errorMsg = licenseService.getValidationErrorMessage();
            log.warn("许可证验证失败 [方法: {}]: {}", methodName, errorMsg);
            
            if (licenseService.isExpired()) {
                LicenseInfo license = licenseService.getCurrentLicense();
                String expireDate = license != null ? license.getExpireDate().toString() : "未知";
                throw LicenseException.licenseExpired(expireDate);
            }
            
            throw LicenseException.licenseNotFound();
        }

        for (String module : modules) {
            if (!licenseService.isModuleEnabled(module)) {
                log.warn("模块授权检查失败 [方法: {}, 模块: {}]", methodName, module);
                throw LicenseException.moduleNotLicensed(module);
            }
        }

        long remainingDays = licenseService.getRemainingDays();
        if (remainingDays <= 7 && remainingDays > 0) {
            log.warn("许可证即将过期，剩余 {} 天 [方法: {}]", remainingDays, methodName);
        }

        log.debug("许可证验证通过 [方法: {}, 模块: {}]", methodName, Arrays.toString(modules));
        return point.proceed();
    }

    private String getMethodName(ProceedingJoinPoint point) {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            return method.getDeclaringClass().getSimpleName() + "." + method.getName();
        } catch (Exception e) {
            return point.getSignature().toShortString();
        }
    }
}
