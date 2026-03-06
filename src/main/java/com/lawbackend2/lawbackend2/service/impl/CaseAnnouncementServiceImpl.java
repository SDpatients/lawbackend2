package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CaseAnnouncementCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementPublishRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseAnnouncementCreateWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseAnnouncementWithFilesResponse;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CaseAnnouncementRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CaseAnnouncementServiceImpl implements CaseAnnouncementService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CaseAnnouncementServiceImpl.class);

    private final CaseAnnouncementRepository caseAnnouncementRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final NotificationService notificationService;
    private final FileRecordRepository fileRecordRepository;

    public CaseAnnouncementServiceImpl(CaseAnnouncementRepository caseAnnouncementRepository, 
                                       UserRepository userRepository, 
                                       FileService fileService, 
                                       NotificationService notificationService,
                                       FileRecordRepository fileRecordRepository) {
        this.caseAnnouncementRepository = caseAnnouncementRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.notificationService = notificationService;
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseAnnouncement createAnnouncement(CaseAnnouncementCreateRequest request, Long userId) {
        logger.info("创建案件公告，案件 ID: {}, 创建人 ID: {}", request.getCaseId(), userId);

        CaseAnnouncement announcement = new CaseAnnouncement();
        BeanUtils.copyProperties(request, announcement);
        announcement.setStatus("DRAFT");
        announcement.setCreateUserId(userId);
        announcement.setUpdateUserId(userId);

        CaseAnnouncement saved = caseAnnouncementRepository.save(announcement);
        logger.info("案件公告创建成功，ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseAnnouncementWithFilesResponse createAnnouncementWithFiles(CaseAnnouncementCreateWithFilesRequest request, Long userId) {
        logger.info("创建案件公告（带文件），案件 ID: {}, 创建人 ID: {}, 文件数量：{}", 
                 request.getCaseId(), userId, request.getFiles().size());

        CaseAnnouncement announcement = new CaseAnnouncement();
        announcement.setCaseId(request.getCaseId());
        announcement.setCaseNumber(request.getCaseNumber());
        announcement.setPrincipalOfficer(request.getPrincipalOfficer());
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setAnnouncementType(request.getAnnouncementType());
        announcement.setStatus("DRAFT");
        announcement.setCreateUserId(userId);
        announcement.setUpdateUserId(userId);

        CaseAnnouncement saved = caseAnnouncementRepository.save(announcement);
        logger.info("案件公告创建成功，ID: {}", saved.getId());

        List<FileRecord> uploadedFiles = new ArrayList<>();
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (int i = 0; i < request.getFiles().size(); i++) {
                MultipartFile file = request.getFiles().get(i);
                String description = (request.getFileDescriptions() != null && i < request.getFileDescriptions().size()) 
                                   ? request.getFileDescriptions().get(i) : null;
                
                FileRecord fileRecord = fileService.uploadFile(file, "announcement", String.valueOf(saved.getId()));
                if (description != null && !description.isEmpty()) {
                    fileRecord.setDescription(description);
                    fileRecord = fileRecordRepository.save(fileRecord);
                }
                uploadedFiles.add(fileRecord);
            }
            logger.info("公告附件上传成功，公告 ID: {}, 文件数量：{}", saved.getId(), uploadedFiles.size());
        }

        CaseAnnouncementWithFilesResponse response = new CaseAnnouncementWithFilesResponse();
        response.setAnnouncementId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setAnnouncementType(saved.getAnnouncementType());
        response.setStatus(saved.getStatus());
        response.setFiles(uploadedFiles);

        return response;
    }

    @Override
    public CaseAnnouncement getAnnouncementById(Long announcementId) {
        logger.debug("查询案件公告，ID: {}", announcementId);
        return caseAnnouncementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException("案件公告不存在"));
    }

    @Override
    public List<CaseAnnouncement> getAnnouncementList(Integer pageNum, Integer pageSize, Long caseId, String status) {
        logger.debug("查询案件公告列表，pageNum: {}, pageSize: {}, caseId: {}, status: {}", 
                  pageNum, pageSize, caseId, status);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "isTop", "publishTime"));

        Page<CaseAnnouncement> page;
        if (caseId != null && status != null && !status.isEmpty()) {
            page = caseAnnouncementRepository.findByCaseIdAndStatus(caseId, status, pageable);
        } else if (caseId != null) {
            page = caseAnnouncementRepository.findByCaseId(caseId, pageable);
        } else if (status != null && !status.isEmpty()) {
            page = caseAnnouncementRepository.findByStatus(status, pageable);
        } else {
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
            return caseAnnouncementRepository.findByStatus("PUBLISHED", pageable).getTotalElements();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseAnnouncement updateAnnouncement(Long announcementId, CaseAnnouncementUpdateRequest request) {
        logger.info("更新案件公告，ID: {}", announcementId);

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
        logger.info("案件公告更新成功，ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishAnnouncement(Long announcementId, CaseAnnouncementPublishRequest request, Long userId) {
        logger.info("发布公告，ID: {}, 发布人 ID: {}", announcementId, userId);

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
        logger.info("公告发布成功，ID: {}, 发布人：{}", announcementId, user.getRealName());

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
        logger.info("置顶公告，ID: {}, 操作人 ID: {}", announcementId, userId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        announcement.setIsTop(true);
        announcement.setTopExpireTime(request.getTopExpireTime());

        caseAnnouncementRepository.save(announcement);
        logger.info("公告置顶成功，ID: {}", announcementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unTopAnnouncement(Long announcementId, Long userId) {
        logger.info("取消置顶公告，ID: {}, 操作人 ID: {}", announcementId, userId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        announcement.setIsTop(false);
        announcement.setTopExpireTime(null);

        caseAnnouncementRepository.save(announcement);
        logger.info("公告取消置顶成功，ID: {}", announcementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long announcementId) {
        logger.info("删除案件公告，ID: {}", announcementId);

        CaseAnnouncement announcement = getAnnouncementById(announcementId);

        caseAnnouncementRepository.delete(announcement);
        logger.info("案件公告删除成功，ID: {}", announcementId);
    }

    @Override
    public List<FileRecord> getAnnouncementAttachments(Long announcementId) {
        logger.debug("查询公告附件列表，公告 ID: {}", announcementId);

        getAnnouncementById(announcementId);

        return fileService.getFileList(1, 100, "announcement", String.valueOf(announcementId), null).getList();
    }
}
