package com.lawbackend2.lawbackend2.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";
        
        System.out.println("=== 测试旧密码 hash ===");
        System.out.println("Stored password length: " + encodedPassword.length());
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Does '123456' match stored password? " + matches);
        System.out.println();
        
        System.out.println("=== 生成新密码 hash ===");
        String newHash = encoder.encode(rawPassword);
        System.out.println("Password: " + rawPassword);
        System.out.println("New hash: " + newHash);
        System.out.println("Hash length: " + newHash.length());
        System.out.println();
        
        System.out.println("=== 验证新 hash ===");
        boolean verifyNew = encoder.matches(rawPassword, newHash);
        System.out.println("Does new hash match '123456'? " + verifyNew);
    }
}
