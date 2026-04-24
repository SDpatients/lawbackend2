package com.lawbackend2.lawbackend2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "ACCESS");
        return generateToken(claims, username, accessTokenExpiration);
    }

    public String generateTokenWithPermissions(Long userId, String username, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "ACCESS");
        claims.put("permissions", permissions);
        claims.put("permHash", generatePermissionsHash(permissions));
        return generateToken(claims, username, accessTokenExpiration);
    }

    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "REFRESH");
        return generateToken(claims, username, refreshTokenExpiration);
    }

    private String generatePermissionsHash(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "empty";
        }
        String sortedPermissions = permissions.stream()
                .sorted()
                .collect(java.util.stream.Collectors.joining("|"));
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sortedPermissions.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return String.valueOf(sortedPermissions.hashCode());
        }
    }

    public List<String> getPermissionsFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object permObj = claims.get("permissions");
            if (permObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> permissions = (List<String>) permObj;
                return permissions;
            }
        } catch (Exception e) {
            log.warn("从Token中获取权限失败: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public String getPermissionsHashFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object hashObj = claims.get("permHash");
            if (hashObj != null) {
                return hashObj.toString();
            }
        } catch (Exception e) {
            log.warn("从Token中获取权限哈希失败: {}", e.getMessage());
        }
        return null;
    }

    public Boolean isPermissionsValid(String token, List<String> currentPermissions) {
        String tokenHash = getPermissionsHashFromToken(token);
        if (tokenHash == null) {
            return false;
        }
        String currentHash = generatePermissionsHash(currentPermissions);
        return tokenHash.equals(currentHash);
    }

    public String generateToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public String getTokenType(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    public Boolean isAccessToken(String token) {
        try {
            return "ACCESS".equals(getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isRefreshToken(String token) {
        try {
            return "REFRESH".equals(getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }
}
