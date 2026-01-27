package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CaseAnnouncementCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementPublishRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CaseAnnouncementRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.CaseAnnouncementService;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CaseAnnouncementServiceImpl implements CaseAnnouncementService {

    private final CaseAnnouncementRepository caseAnnouncementRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final NotificationService notificationService;

    public CaseAnnouncementServiceImpl(CaseAnnouncementRepository caseAnnouncementRepository, UserRepository userRepository, FileService fileService, NotificationService notificationService) {
        this.caseAnnouncementRepository = caseAnnouncementRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseAnnouncement createAnnouncement(CaseAnnouncementCreateRequest request, Long userId) {
        log.info("创建案件公告, 案件ID: {}, 创建人ID: {}", request.getCaseId(), userId);

        CaseAnnouncement announcement = new CaseAnnouncement();
        BeanUtils.copyProperties(request, announcement);
        announcement.setStatus("DRAFT");
        announcement.setCreateUserId(userId);
        announcement.setUpdateUserId(userId);

        CaseAnnouncement saved = caseAnnouncementRepository.save(announcement);
        log.info("案件公告创建成功, ID: {}", saved.getId());
        return saved;
    }

    @Override
    public CaseAnnouncement getAnnouncementById(Long announcementId) {
        log.debug("查询案件公告, ID: {}", announcementId);
        return caseAnnouncementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("案件公告不存在"));
    }

    @Override
    public List<CaseAnnouncement> getAnnouncementList(Integer pageNum, Integer pageSize, Long caseId, String status) {
        log.debug("查询案件公告列表, pageNum: {}, pageSize: {}, caseId: {}, status: {}", 
                  pageNum, pageSize, caseId, status);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "isTop", "createTime"));

        Page<CaseAnnouncement> page;
        if (caseId != null && status != null && !status.isEmpty()) {
            page = caseAnnouncementRepository.findByCaseIdAndStatus(caseId, status, pageable);
        } else if (caseId != null) {
            page = caseAnnouncementRepository.findByCaseId(caseId, pageable);
        } else if (status != null && !status.isEmpty()) {
            page = caseAnnouncementRepository.findByStatus(status, pageable);
        } else {
            // 当没有案件ID时，只查询已发布的公告
            page = caseAnnouncementRepository.findByStatus("PUBLISHED", pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getAnnouncementCount(Long caseId, String status) {
        Pageable pageable = Pageable.unpaged();

        if (caseId != null && status != null && !status.isEmpty()) {
            return caseAnnouncementRepository.findByCaseIdAndStatus(caseId, status, pageable).getTotalElements();
        } else if (caseId != null) {
            return caseAnnouncementRepository.findByCaseId(caseId, pageable).getTotalElements();
        } else if (status != null && !status.isEmpty()) {
            return caseAnnouncementRepository.findByStatus(status, pageable).getTotalElements();
        } else {
            // 当没有案件ID时，只统计已发布的公告
            return caseAnnouncementRepository.findByStatus("PUBLISHED", pageable).getTotalElements();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseAnnouncement updateAnnouncement(Long announcementId, CaseAnnouncementUpdateRequest request) {
        log.info("更新案件公告, ID: {}", announcementId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        if (request.getTitle() != null) {
            announcement.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            announcement.setContent(request.getContent());
        }
        if (request.getAnnouncementType() != null) {
            announcement.setAnnouncementType(request.getAnnouncementType());
        }
        if (request.getAttachments() != null) {
            announcement.setAttachments(request.getAttachments());
        }

        CaseAnnouncement updated = caseAnnouncementRepository.save(announcement);
        log.info("案件公告更新成功, ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishAnnouncement(Long announcementId, CaseAnnouncementPublishRequest request, Long userId) {
        log.info("发布公告, ID: {}, 发布人ID: {}", announcementId, userId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        if (!"DRAFT".equals(announcement.getStatus())) {
            throw new BusinessException("只有草稿状态的公告才能发布");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        announcement.setStatus("PUBLISHED");
        announcement.setPublisherId(userId);
        announcement.setPublisherName(user.getRealName());
        announcement.setPublishTime(LocalDateTime.now());

        if (request.getTopExpireTime() != null) {
            announcement.setIsTop(true);
            announcement.setTopExpireTime(request.getTopExpireTime());
        }

        caseAnnouncementRepository.save(announcement);
        log.info("公告发布成功, ID: {}, 发布人: {}", announcementId, user.getRealName());

        String content = String.format("%s 发布了公告：%s", user.getRealName(), announcement.getTitle());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "公告发布通知",
                content,
                "CASE_ANNOUNCEMENT",
                announcement.getId(),
                "CaseAnnouncement",
                userId,
                user.getRealName()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void topAnnouncement(Long announcementId, CaseAnnouncementPublishRequest request, Long userId) {
        log.info("置顶公告, ID: {}, 操作人ID: {}", announcementId, userId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        announcement.setIsTop(true);
        announcement.setTopExpireTime(request.getTopExpireTime());

        caseAnnouncementRepository.save(announcement);
        log.info("公告置顶成功, ID: {}", announcementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unTopAnnouncement(Long announcementId, Long userId) {
        log.info("取消置顶公告, ID: {}, 操作人ID: {}", announcementId, userId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        announcement.setIsTop(false);
        announcement.setTopExpireTime(null);

        caseAnnouncementRepository.save(announcement);
        log.info("公告取消置顶成功, ID: {}", announcementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long announcementId) {
        log.info("删除案件公告, ID: {}", announcementId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        if ("PUBLISHED".equals(announcement.getStatus())) {
            throw new BusinessException("已发布的公告不能删除");
        }

        caseAnnouncementRepository.delete(announcement);
        log.info("案件公告删除成功, ID: {}", announcementId);
    }

    @Override
    public List<FileRecord> getAnnouncementAttachments(Long announcementId) {
        log.debug("查询公告附件列表, 公告ID: {}", announcementId);

        getAnnouncementById(announcementId);

        return fileService.getFileList(1, 100, "announcement", String.valueOf(announcementId), null).getList();
    }
}
