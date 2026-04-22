package com.lawbackend2.lawbackend2.license.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.license.LicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 许可证状态检查拦截器
 * 当系统没有有效许可证时，阻止访问非许可证相关的API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LicenseCheckInterceptor implements HandlerInterceptor {

    private final LicenseService licenseService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 允许在无许可证时访问的路径
    private static final String[] ALLOWED_PATHS = {
            "/system/license/status",
            "/system/license/machine-code",
            "/system/license/upload",
            "/auth/",
            "/swagger-ui",
            "/api-docs",
            "/v3/api-docs",
            "/webjars",
            "/swagger-resources",
            "/error"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果许可证验证被禁用，放行所有请求
        if (!licenseService.isLicenseEnabled()) {
            return true;
        }

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestUri.substring(contextPath.length());

        // 检查是否是允许访问的路径
        for (String allowedPath : ALLOWED_PATHS) {
            if (path.startsWith(allowedPath)) {
                return true;
            }
        }

        // 检查是否有有效许可证
        if (!licenseService.isValid()) {
            log.warn("许可证无效，拒绝访问: {}", path);

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("errorCode", licenseService.getLastValidationErrorCode());
            errorDetails.put("message", licenseService.getValidationErrorMessage());
            errorDetails.put("help", licenseService.getLastValidationDetails().get("help"));
            errorDetails.put("machineCode", licenseService.getMachineCode());

            Result<Map<String, Object>> result = Result.error(403,
                    "系统未激活: " + licenseService.getValidationErrorMessage(),
                    errorDetails);

            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }

        return true;
    }
}
