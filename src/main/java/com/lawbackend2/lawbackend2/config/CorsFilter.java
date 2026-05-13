package com.lawbackend2.lawbackend2.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private final AppProperties appProperties;

    public CorsFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        AppProperties.CorsConfig corsConfig = appProperties.getCors();

        String origin = request.getHeader("Origin");
        String allowedOrigin = resolveAllowedOrigin(origin, corsConfig.getAllowedOrigins());

        if (allowedOrigin != null) {
            response.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        }

        response.setHeader("Access-Control-Allow-Methods", corsConfig.getAllowedMethods());
        response.setHeader("Access-Control-Allow-Headers", corsConfig.getAllowedHeaders());
        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsConfig.isAllowCredentials()));
        response.setHeader("Access-Control-Max-Age", String.valueOf(corsConfig.getMaxAge()));

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (origin != null && allowedOrigin == null) {
            log.warn("CORS请求被拒绝 - Origin: {} 不在白名单中, 白名单: {}", origin, corsConfig.getAllowedOrigins());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(req, res);
    }

    private String resolveAllowedOrigin(String origin, String allowedOriginsConfig) {
        if (origin == null || origin.isEmpty()) {
            return null;
        }

        if ("*".equals(allowedOriginsConfig.trim())) {
            return origin;
        }

        Set<String> allowedOriginSet = parseOriginList(allowedOriginsConfig);
        if (allowedOriginSet.contains(origin)) {
            return origin;
        }

        return null;
    }

    private Set<String> parseOriginList(String allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(allowedOrigins.trim().split("\\s*,\\s*")));
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}