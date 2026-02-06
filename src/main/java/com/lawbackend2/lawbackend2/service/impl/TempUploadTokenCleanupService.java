package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.service.TempUploadTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TempUploadTokenCleanupService {

    @Autowired
    private TempUploadTokenService tempUploadTokenService;

    @Scheduled(cron = "0 0 */1 * * ?")
    public void cleanupExpiredTokens() {
        log.info("开始执行临时上传Token清理任务");
        try {
            tempUploadTokenService.cleanupExpiredTokens();
            log.info("临时上传Token清理任务执行完成");
        } catch (Exception e) {
            log.error("临时上传Token清理任务执行失败", e);
        }
    }
}
