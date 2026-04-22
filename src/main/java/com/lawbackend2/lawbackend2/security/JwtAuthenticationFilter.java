package com.lawbackend2.lawbackend2.security;

import com.lawbackend2.lawbackend2.service.PermissionCacheService;
import com.lawbackend2.lawbackend2.service.PermissionService;
import com.lawbackend2.lawbackend2.service.TokenBlacklistService;
import com.lawbackend2.lawbackend2.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // OPTIONS请求是CORS预检请求，直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();

        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 对于文件上传请求，确保从请求头中提取token，不读取请求体
        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                if (jwtTokenUtil.validateToken(token)) {
                    boolean isBlacklisted = false;
                    try {
                        isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);
                    } catch (Exception e) {
                        log.warn("检查Token黑名单时出错（Redis可能未启动），继续处理 - URI: {}, 错误: {}", requestURI, e.getMessage());
                    }
                    
                    if (!isBlacklisted) {
                        Long userId = jwtTokenUtil.getUserIdFromToken(token);
                        String username = jwtTokenUtil.getUsernameFromToken(token);

                        List<String> permissions = permissionService.getUserPermissions(userId);
                        
                        List<SimpleGrantedAuthority> authorities = permissions.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(
                                        userId,
                                        null,
                                        authorities
                                );

                        // 只设置基本信息，不读取请求体
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("用户认证成功 - 用户ID: {}, 用户名: {}, 权限数量: {}, URI: {}", userId, username, authorities.size(), requestURI);
                    } else {
                        log.warn("Token已在黑名单中 - URI: {}", requestURI);
                    }
                } else {
                    log.warn("Token验证失败 - URI: {}", requestURI);
                }
            } catch (Exception e) {
                log.warn("处理Token时发生异常 - URI: {}, 错误: {}", requestURI, e.getMessage());
            }
        } else {
            log.warn("未提供Token - URI: {}", requestURI);
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix + " ")) {
            return bearerToken.substring(tokenPrefix.length() + 1);
        }

        return null;
    }

    private boolean isPublicEndpoint(String requestURI) {
        boolean isAuthEndpoint = requestURI.contains("/auth/");
        boolean needsAuth = requestURI.contains("/auth/current-user") ||
                           requestURI.contains("/auth/change-password") ||
                           requestURI.contains("/auth/statistics") ||
                           requestURI.contains("/auth/recent-failed") ||
                           requestURI.contains("/auth/login-history") ||
                           requestURI.contains("/auth/profile/");
        
        return (isAuthEndpoint && !needsAuth) ||
               requestURI.contains("/user/register") ||
               requestURI.contains("/swagger") ||
               requestURI.contains("/api-docs") ||
               requestURI.contains("/v3/api-docs") ||
               requestURI.contains("/ws") ||
               requestURI.contains("/sockjs") ||
               requestURI.contains("/work-team/list/details");
    }
}
