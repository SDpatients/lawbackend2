package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkLogCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogCreateWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkLogDetailResponse;
import com.lawbackend2.lawbackend2.entity.WorkLog;

import java.time.LocalDate;
import java.util.Map;

public interface WorkLogService {

    Long createWorkLog(WorkLogCreateRequest request);

    Map<String, Object> createWorkLogWithFiles(WorkLogCreateWithFilesRequest request, java.util.List<org.springframework.web.multipart.MultipartFile> files);

    PageResult<WorkLog> getWorkLogList(Integer pageNum, Integer pageSize, Long caseId, String workType, LocalDate startDate, LocalDate endDate, Long createUserId, String status);

    WorkLog getWorkLogDetail(Long logId);

    WorkLogDetailResponse getWorkLogDetailWithFiles(Long logId);

    void updateWorkLog(Long logId, WorkLogUpdateRequest request);

    Map<String, Object> updateWorkLogWithFiles(Long logId, WorkLogUpdateRequest request, java.util.List<org.springframework.web.multipart.MultipartFile> files);

    void updateWorkLogStatus(Long logId, WorkLogStatusRequest request);

    void deleteWorkLog(Long logId);

    void deleteWorkLogWithFiles(Long logId);
}
