package com.lawbackend2.lawbackend2.exception;

public class PermissionDeniedException extends RuntimeException {

    private Integer code;

    public PermissionDeniedException(String message) {
        super(message);
        this.code = 403;
    }

    public PermissionDeniedException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
