package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CaseTaskUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskStatistics;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CaseTaskService {

    List<CaseTask> createTasksForCase(Long caseId);

    Page<CaseTaskResponse> getTasksByCaseId(Long caseId, String status, String taskCode, Pageable pageable);

    CaseTask getTaskById(Long taskId);

    CaseTaskResponse updateTask(Long taskId, CaseTaskUpdateRequest request);

    void batchUpdateTaskStatus(List<Long> taskIds, String status);

    void deleteTask(Long taskId);

    CaseTaskStatistics getStatistics(Long caseId);
}
