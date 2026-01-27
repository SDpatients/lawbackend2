package com.lawbackend2.lawbackend2.exception;

public class CaseTaskStatusInvalidException extends BusinessException {
    public CaseTaskStatusInvalidException(String status) {
        super("无效的任务状态: " + status);
    }
}
