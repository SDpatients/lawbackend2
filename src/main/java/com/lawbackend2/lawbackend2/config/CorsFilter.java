package com.lawbackend2.lawbackend2.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        String allowedOrigins = corsConfig.getAllowedOrigins();
        if ("*".equals(allowedOrigins)) {
            response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        } else {
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins);
        }
        response.setHeader("Access-Control-Allow-Methods", corsConfig.getAllowedMethods());
        response.setHeader("Access-Control-Allow-Headers", corsConfig.getAllowedHeaders());
        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsConfig.isAllowCredentials()));
        response.setHeader("Access-Control-Max-Age", String.valueOf(corsConfig.getMaxAge()));

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}