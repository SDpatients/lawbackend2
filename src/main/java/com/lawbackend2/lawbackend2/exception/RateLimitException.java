package com.lawbackend2.lawbackend2.exception;

public class RateLimitException extends RuntimeException {
    
    private int code;
    
    public RateLimitException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
