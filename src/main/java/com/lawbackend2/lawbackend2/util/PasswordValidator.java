package com.lawbackend2.lawbackend2.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[^a-zA-Z0-9]");

    public static void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }

        if (password.length() > 32) {
            throw new IllegalArgumentException("密码长度不能超过32位");
        }

        int complexity = 0;
        if (UPPERCASE.matcher(password).find()) complexity++;
        if (LOWERCASE.matcher(password).find()) complexity++;
        if (DIGIT.matcher(password).find()) complexity++;
        if (SPECIAL_CHAR.matcher(password).find()) complexity++;

        if (complexity < 3) {
            throw new IllegalArgumentException("密码必须包含大写字母、小写字母、数字、特殊符号中的至少3种");
        }
    }
}