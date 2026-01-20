package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.ArchiveCategoryResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveRecordResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveUpdateRequest;
import com.lawbackend2.lawbackend2.dto.ArchiveUploadRequest;
import com.lawbackend2.lawbackend2.entity.ArchiveRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchiveService {

    List<ArchiveCategoryResponse> getCategoryTree(String status);

    ArchiveRecordResponse uploadArchiveFile(Long caseId, MultipartFile file, ArchiveUploadRequest request, Long userId);

    PageResult<ArchiveRecordResponse> getArchiveFiles(Long caseId, String categoryCode, Integer pageNum, Integer pageSize, String status, String keyword);

    ArchiveRecordResponse getArchiveRecord(Long recordId);

    ArchiveRecordResponse updateArchiveRecord(Long recordId, ArchiveUpdateRequest request, Long userId);

    void deleteArchiveRecord(Long recordId, Long userId);

    void deleteArchiveRecords(List<Long> recordIds, Long userId);

    Long getArchiveCount(Long caseId, String categoryCode, String status);
}
