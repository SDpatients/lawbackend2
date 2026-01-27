package com.lawbackend2.lawbackend2.exception;

public class CaseTaskNotFoundException extends BusinessException {
    public CaseTaskNotFoundException(Long taskId) {
        super("案件任务不存在: " + taskId);
    }
}
