package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.AnnouncementViewRecordCreateRequest;
import com.lawbackend2.lawbackend2.entity.AnnouncementViewRecord;

import java.util.List;

public interface AnnouncementViewRecordService {
    AnnouncementViewRecord createViewRecord(AnnouncementViewRecordCreateRequest request);

    AnnouncementViewRecord getViewRecordById(Long recordId);

    List<AnnouncementViewRecord> getViewRecordList(Integer page, Integer size, Long announcementId, Long caseId, Long viewerId);

    Long getViewCountByAnnouncementId(Long announcementId);

    Long getViewCountByCaseId(Long caseId);

    Long getViewCountByViewerId(Long viewerId);

    void deleteViewRecord(Long recordId);
}
