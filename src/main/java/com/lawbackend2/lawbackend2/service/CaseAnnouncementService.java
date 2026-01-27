package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseAnnouncementCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementPublishRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.entity.FileRecord;

import java.util.List;

public interface CaseAnnouncementService {

    CaseAnnouncement createAnnouncement(CaseAnnouncementCreateRequest request, Long userId);

    CaseAnnouncement getAnnouncementById(Long announcementId);

    List<CaseAnnouncement> getAnnouncementList(Integer pageNum, Integer pageSize, Long caseId, String status);

    Long getAnnouncementCount(Long caseId, String status);

    CaseAnnouncement updateAnnouncement(Long announcementId, CaseAnnouncementUpdateRequest request);

    void publishAnnouncement(Long announcementId, CaseAnnouncementPublishRequest request, Long userId);

    void topAnnouncement(Long announcementId, CaseAnnouncementPublishRequest request, Long userId);

    void unTopAnnouncement(Long announcementId, Long userId);

    void deleteAnnouncement(Long announcementId);

    List<FileRecord> getAnnouncementAttachments(Long announcementId);
}
