package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.annotation.MaskType;

public class MaskUtil {

    public static String mask(String value, MaskType type) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        switch (type) {
            case ID_CARD:
                return maskIdCard(value);
            case PHONE:
                return maskPhone(value);
            case BANK_ACCOUNT:
                return maskBankAccount(value);
            case EMAIL:
                return maskEmail(value);
            case NAME:
                return maskName(value);
            case DEFAULT:
            default:
                return maskDefault(value);
        }
    }

    private static String maskIdCard(String value) {
        if (value.length() <= 8) return value;
        return value.substring(0, 4) + "**********" + value.substring(value.length() - 4);
    }

    private static String maskPhone(String value) {
        if (value.length() < 7) return value;
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    private static String maskBankAccount(String value) {
        if (value.length() <= 8) return value;
        return value.substring(0, 4) + " **** **** " + value.substring(value.length() - 4);
    }

    private static String maskEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 2) return value;
        String prefix = value.substring(0, atIndex);
        String suffix = value.substring(atIndex);
        return prefix.charAt(0) + "***" + prefix.charAt(prefix.length() - 1) + suffix;
    }

    private static String maskName(String value) {
        if (value.length() <= 1) return value;
        if (value.length() == 2) return value.charAt(0) + "*";
        return value.charAt(0) + "**";
    }

    private static String maskDefault(String value) {
        if (value.length() <= 4) return value;
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }
}