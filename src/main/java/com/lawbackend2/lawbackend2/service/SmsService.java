package com.lawbackend2.lawbackend2.service;

public interface SmsService {

    void sendSmsCode(String mobile, String smsType);

    boolean verifySmsCode(String mobile, String code, String smsType);

    String generateSmsCode();
}
