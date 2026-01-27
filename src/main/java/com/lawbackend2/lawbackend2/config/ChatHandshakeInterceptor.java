package com.lawbackend2.lawbackend2.config;

import com.lawbackend2.lawbackend2.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String requestURI = servletRequest.getServletRequest().getRequestURI();
            
            log.info("WebSocket握手请求 - URI: {}", requestURI);

            String token = extractToken(servletRequest);

            if (StringUtils.hasText(token)) {
                try {
                    if (jwtTokenUtil.validateToken(token)) {
                        Long userId = jwtTokenUtil.getUserIdFromToken(token);
                        String username = jwtTokenUtil.getUsernameFromToken(token);

                        attributes.put("userId", userId);
                        attributes.put("username", username);
                        
                        log.info("WebSocket握手成功 - 用户ID: {}, 用户名: {}", userId, username);
                        return true;
                    } else {
                        log.warn("WebSocket握手失败 - Token验证失败");
                        // 开发环境允许继续，生产环境应返回false
                        return true;
                    }
                } catch (Exception e) {
                    log.error("Token验证异常", e);
                    // 开发环境允许继续，生产环境应返回false
                    return true;
                }
            } else {
                log.warn("WebSocket握手 - 未提供Token，允许连接（开发环境）");
                // 开发环境允许匿名访问，生产环境应返回false
                return true;
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手后发生异常", exception);
        }
    }

    private String extractToken(ServletServerHttpRequest request) {
        String bearerToken = request.getServletRequest().getHeader(tokenHeader);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix + " ")) {
            return bearerToken.substring(tokenPrefix.length() + 1);
        }

        String tokenParam = request.getServletRequest().getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            log.debug("从query参数中提取Token");
            if (tokenParam.startsWith(tokenPrefix + " ")) {
                return tokenParam.substring(tokenPrefix.length() + 1);
            }
            return tokenParam;
        }

        return null;
    }
}
