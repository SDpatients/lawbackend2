package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.repository.CaseAnnouncementRepository;
import com.lawbackend2.lawbackend2.service.AnnouncementTopCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AnnouncementTopCleanupServiceImpl implements AnnouncementTopCleanupService {

    @Autowired
    private CaseAnnouncementRepository caseAnnouncementRepository;

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cleanupExpiredTopAnnouncements() {
        log.info("开始检查并取消过期的置顶公告...");

        LocalDateTime now = LocalDateTime.now();
        List<CaseAnnouncement> expiredTopAnnouncements = caseAnnouncementRepository.findByIsTopTrueAndTopExpireTimeBefore(now);

        if (!expiredTopAnnouncements.isEmpty()) {
            int count = expiredTopAnnouncements.size();
            for (CaseAnnouncement announcement : expiredTopAnnouncements) {
                announcement.setIsTop(false);
                announcement.setTopExpireTime(null);
                caseAnnouncementRepository.save(announcement);
                log.info("公告置顶已过期，已自动取消置顶，公告ID: {}, 标题: {}", announcement.getId(), announcement.getTitle());
            }
            log.info("过期置顶公告清理完成，共取消 {} 个公告的置顶", count);
        } else {
            log.info("没有发现过期的置顶公告");
        }
    }
}