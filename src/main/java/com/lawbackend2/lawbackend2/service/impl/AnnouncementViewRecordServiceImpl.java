package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.AnnouncementViewRecordCreateRequest;
import com.lawbackend2.lawbackend2.entity.AnnouncementViewRecord;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.AnnouncementViewRecordRepository;
import com.lawbackend2.lawbackend2.repository.CaseAnnouncementRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.AnnouncementViewRecordService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AnnouncementViewRecordServiceImpl implements AnnouncementViewRecordService {

    @Autowired
    private AnnouncementViewRecordRepository viewRecordRepository;

    @Autowired
    private CaseAnnouncementRepository announcementRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AnnouncementViewRecord createViewRecord(AnnouncementViewRecordCreateRequest request) {
        log.info("开始创建公告查看记录，公告ID：{}", request.getAnnouncementId());

        try {
            Optional<CaseAnnouncement> announcementOpt = announcementRepository.findById(request.getAnnouncementId());
            if (!announcementOpt.isPresent()) {
                throw new BusinessException("公告不存在");
            }

            CaseAnnouncement announcement = announcementOpt.get();

            Long viewerId = request.getViewerId();
            String viewerName = request.getViewerName();
            String viewerType = request.getViewerType();

            if (viewerId == null) {
                viewerId = SecurityUtil.getCurrentUserId();
                Optional<User> userOpt = userRepository.findById(viewerId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    viewerName = user.getRealName();
                    viewerType = "USER";
                }
            }

            AnnouncementViewRecord record = new AnnouncementViewRecord();
            record.setAnnouncementId(request.getAnnouncementId());
            record.setAnnouncementTitle(announcement.getTitle());
            record.setCaseId(announcement.getCaseId());
            record.setViewerId(viewerId);
            record.setViewerName(viewerName);
            record.setViewerType(viewerType);
            record.setViewTime(java.time.LocalDateTime.now());
            record.setIpAddress(request.getIpAddress());
            record.setUserAgent(request.getUserAgent());
            record.setViewDuration(request.getViewDuration() != null ? request.getViewDuration() : 0);
            record.setDeviceType(request.getDeviceType());
            record.setBrowserType(request.getBrowserType());
            record.setOsType(request.getOsType());
            record.setLocation(request.getLocation());

            AnnouncementViewRecord savedRecord = viewRecordRepository.save(record);

            announcement.setViewCount(announcement.getViewCount() + 1);
            announcementRepository.save(announcement);

            log.info("公告查看记录创建成功，记录ID：{}", savedRecord.getId());
            return savedRecord;

        } catch (BusinessException e) {
            log.error("创建公告查看记录失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建公告查看记录失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("创建公告查看记录失败");
        }
    }

    @Override
    public AnnouncementViewRecord getViewRecordById(Long recordId) {
        log.info("查询公告查看记录，记录ID：{}", recordId);

        try {
            Optional<AnnouncementViewRecord> recordOpt = viewRecordRepository.findById(recordId);
            if (!recordOpt.isPresent()) {
                throw new BusinessException("公告查看记录不存在");
            }

            AnnouncementViewRecord record = recordOpt.get();
            log.info("查询公告查看记录成功，记录ID：{}", recordId);
            return record;

        } catch (BusinessException e) {
            log.error("查询公告查看记录失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("查询公告查看记录失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询公告查看记录失败");
        }
    }

    @Override
    public List<AnnouncementViewRecord> getViewRecordList(Integer page, Integer size, Long announcementId, Long caseId, Long viewerId) {
        log.info("查询公告查看记录列表，page：{}，size：{}，announcementId：{}，caseId：{}，viewerId：{}",
                page, size, announcementId, caseId, viewerId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AnnouncementViewRecord> recordPage;

            if (announcementId != null && caseId != null) {
                recordPage = viewRecordRepository.findByAnnouncementIdAndCaseId(announcementId, caseId, pageable);
            } else if (announcementId != null) {
                recordPage = viewRecordRepository.findByAnnouncementId(announcementId, pageable);
            } else if (caseId != null) {
                recordPage = viewRecordRepository.findByCaseId(caseId, pageable);
            } else if (viewerId != null) {
                recordPage = viewRecordRepository.findByViewerId(viewerId, pageable);
            } else {
                recordPage = viewRecordRepository.findAll(pageable);
            }

            log.info("查询公告查看记录列表成功，总记录数：{}", recordPage.getTotalElements());
            return recordPage.getContent();

        } catch (Exception e) {
            log.error("查询公告查看记录列表失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询公告查看记录列表失败");
        }
    }

    @Override
    public Long getViewCountByAnnouncementId(Long announcementId) {
        log.info("查询公告查看次数，公告ID：{}", announcementId);

        try {
            Long count = viewRecordRepository.countByAnnouncementId(announcementId);
            log.info("查询公告查看次数成功，公告ID：{}，查看次数：{}", announcementId, count);
            return count;

        } catch (Exception e) {
            log.error("查询公告查看次数失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询公告查看次数失败");
        }
    }

    @Override
    public Long getViewCountByCaseId(Long caseId) {
        log.info("查询案件公告查看次数，案件ID：{}", caseId);

        try {
            Long count = viewRecordRepository.countByCaseId(caseId);
            log.info("查询案件公告查看次数成功，案件ID：{}，查看次数：{}", caseId, count);
            return count;

        } catch (Exception e) {
            log.error("查询案件公告查看次数失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询案件公告查看次数失败");
        }
    }

    @Override
    public Long getViewCountByViewerId(Long viewerId) {
        log.info("查询用户查看次数，用户ID：{}", viewerId);

        try {
            Long count = viewRecordRepository.countByViewerId(viewerId);
            log.info("查询用户查看次数成功，用户ID：{}，查看次数：{}", viewerId, count);
            return count;

        } catch (Exception e) {
            log.error("查询用户查看次数失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询用户查看次数失败");
        }
    }

    @Override
    public void deleteViewRecord(Long recordId) {
        log.info("删除公告查看记录，记录ID：{}", recordId);

        try {
            if (!viewRecordRepository.existsById(recordId)) {
                throw new BusinessException("公告查看记录不存在");
            }

            viewRecordRepository.deleteById(recordId);
            log.info("删除公告查看记录成功，记录ID：{}", recordId);

        } catch (BusinessException e) {
            log.error("删除公告查看记录失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除公告查看记录失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("删除公告查看记录失败");
        }
    }
}
