package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CaseProgressCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseProgressUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseProgress;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CaseProgressRepository;
import com.lawbackend2.lawbackend2.service.CaseProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CaseProgressServiceImpl implements CaseProgressService {

    @Autowired
    private CaseProgressRepository caseProgressRepository;

    @Autowired
    private BankruptCaseRepository caseRepository;

    @Override
    public CaseProgress createProgress(CaseProgressCreateRequest request, Long userId) {
        log.info("开始创建案件进度，案件ID：{}，进度阶段：{}", request.getCaseId(), request.getProgressStage());

        try {
            Optional<BankruptCase> caseOpt = caseRepository.findById(request.getCaseId());
            if (!caseOpt.isPresent()) {
                throw new BusinessException("案件不存在");
            }

            BankruptCase bankruptCase = caseOpt.get();

            Optional<CaseProgress> existingProgress = caseProgressRepository.findByCaseIdAndProgressStage(
                    request.getCaseId(), request.getProgressStage());
            if (existingProgress.isPresent()) {
                throw new BusinessException("该案件已存在相同阶段的进度记录");
            }

            CaseProgress progress = new CaseProgress();
            progress.setCaseId(request.getCaseId());
            progress.setCaseName(request.getCaseName() != null ? request.getCaseName() : bankruptCase.getCaseName());
            progress.setCaseNumber(request.getCaseNumber() != null ? request.getCaseNumber() : bankruptCase.getCaseNumber());
            progress.setProgressStage(request.getProgressStage());
            progress.setStageName(request.getStageName());
            progress.setStageDescription(request.getStageDescription());
            progress.setStartDate(request.getStartDate());
            progress.setExpectedEndDate(request.getExpectedEndDate());
            progress.setProgressStatus(request.getProgressStatus() != null ? request.getProgressStatus() : "IN_PROGRESS");
            progress.setCompletionPercentage(request.getCompletionPercentage() != null ? request.getCompletionPercentage() : 0);
            progress.setKeyTasks(request.getKeyTasks());
            progress.setCompletedTasks(request.getCompletedTasks());
            progress.setPendingTasks(request.getPendingTasks());
            progress.setIssues(request.getIssues());
            progress.setSolutions(request.getSolutions());
            progress.setAttachments(request.getAttachments());
            progress.setResponsiblePerson(request.getResponsiblePerson());
            progress.setResponsiblePersonId(request.getResponsiblePersonId());
            progress.setRemarks(request.getRemarks());
            progress.setIsCompleted(false);

            CaseProgress savedProgress = caseProgressRepository.save(progress);

            log.info("案件进度创建成功，进度ID：{}", savedProgress.getId());
            return savedProgress;

        } catch (BusinessException e) {
            log.error("创建案件进度失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("创建案件进度失败");
        }
    }

    @Override
    public CaseProgress getProgressById(Long progressId) {
        log.info("查询案件进度，进度ID：{}", progressId);

        try {
            Optional<CaseProgress> progressOpt = caseProgressRepository.findById(progressId);
            if (!progressOpt.isPresent()) {
                throw new BusinessException("案件进度不存在");
            }

            CaseProgress progress = progressOpt.get();
            log.info("查询案件进度成功，进度ID：{}", progressId);
            return progress;

        } catch (BusinessException e) {
            log.error("查询案件进度失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("查询案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询案件进度失败");
        }
    }

    @Override
    public List<CaseProgress> getProgressList(Integer page, Integer size, Long caseId, String progressStage, String progressStatus, Boolean isCompleted) {
        log.info("查询案件进度列表，page：{}，size：{}，caseId：{}，progressStage：{}，progressStatus：{}，isCompleted：{}",
                page, size, caseId, progressStage, progressStatus, isCompleted);

        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<CaseProgress> progressPage;

            if (caseId != null && progressStage != null) {
                progressPage = caseProgressRepository.findByCaseIdAndProgressStage(caseId, progressStage, pageable);
            } else if (caseId != null && isCompleted != null) {
                progressPage = caseProgressRepository.findByCaseIdAndIsCompleted(caseId, isCompleted, pageable);
            } else if (caseId != null) {
                progressPage = caseProgressRepository.findByCaseIdOrderByStartDateDesc(caseId, pageable);
            } else if (progressStage != null) {
                progressPage = caseProgressRepository.findByProgressStage(progressStage, pageable);
            } else if (progressStatus != null) {
                progressPage = caseProgressRepository.findByProgressStatus(progressStatus, pageable);
            } else {
                progressPage = caseProgressRepository.findAll(pageable);
            }

            log.info("查询案件进度列表成功，总记录数：{}", progressPage.getTotalElements());
            return progressPage.getContent();

        } catch (Exception e) {
            log.error("查询案件进度列表失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询案件进度列表失败");
        }
    }

    @Override
    public CaseProgress updateProgress(Long progressId, CaseProgressUpdateRequest request, Long userId) {
        log.info("更新案件进度，进度ID：{}", progressId);

        try {
            Optional<CaseProgress> progressOpt = caseProgressRepository.findById(progressId);
            if (!progressOpt.isPresent()) {
                throw new BusinessException("案件进度不存在");
            }

            CaseProgress progress = progressOpt.get();

            if (request.getStageName() != null) {
                progress.setStageName(request.getStageName());
            }
            if (request.getStageDescription() != null) {
                progress.setStageDescription(request.getStageDescription());
            }
            if (request.getStartDate() != null) {
                progress.setStartDate(request.getStartDate());
            }
            if (request.getEndDate() != null) {
                progress.setEndDate(request.getEndDate());
            }
            if (request.getExpectedEndDate() != null) {
                progress.setExpectedEndDate(request.getExpectedEndDate());
            }
            if (request.getProgressStatus() != null) {
                progress.setProgressStatus(request.getProgressStatus());
            }
            if (request.getCompletionPercentage() != null) {
                progress.setCompletionPercentage(request.getCompletionPercentage());
            }
            if (request.getKeyTasks() != null) {
                progress.setKeyTasks(request.getKeyTasks());
            }
            if (request.getCompletedTasks() != null) {
                progress.setCompletedTasks(request.getCompletedTasks());
            }
            if (request.getPendingTasks() != null) {
                progress.setPendingTasks(request.getPendingTasks());
            }
            if (request.getIssues() != null) {
                progress.setIssues(request.getIssues());
            }
            if (request.getSolutions() != null) {
                progress.setSolutions(request.getSolutions());
            }
            if (request.getAttachments() != null) {
                progress.setAttachments(request.getAttachments());
            }
            if (request.getResponsiblePerson() != null) {
                progress.setResponsiblePerson(request.getResponsiblePerson());
            }
            if (request.getResponsiblePersonId() != null) {
                progress.setResponsiblePersonId(request.getResponsiblePersonId());
            }
            if (request.getRemarks() != null) {
                progress.setRemarks(request.getRemarks());
            }
            if (request.getIsCompleted() != null && request.getIsCompleted()) {
                progress.setIsCompleted(true);
                progress.setCompletedAt(LocalDateTime.now());
                progress.setCompletedBy(userId);
            }

            CaseProgress updatedProgress = caseProgressRepository.save(progress);

            log.info("案件进度更新成功，进度ID：{}", progressId);
            return updatedProgress;

        } catch (BusinessException e) {
            log.error("更新案件进度失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("更新案件进度失败");
        }
    }

    @Override
    public void completeProgress(Long progressId, Long userId, String userName) {
        log.info("完成案件进度，进度ID：{}，用户ID：{}，用户名：{}", progressId, userId, userName);

        try {
            Optional<CaseProgress> progressOpt = caseProgressRepository.findById(progressId);
            if (!progressOpt.isPresent()) {
                throw new BusinessException("案件进度不存在");
            }

            CaseProgress progress = progressOpt.get();

            if (progress.getIsCompleted()) {
                throw new BusinessException("该进度已完成");
            }

            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setCompletedBy(userId);
            progress.setCompletedByName(userName);
            progress.setCompletionPercentage(100);
            progress.setProgressStatus("COMPLETED");
            progress.setEndDate(java.time.LocalDate.now());

            caseProgressRepository.save(progress);

            log.info("案件进度完成成功，进度ID：{}", progressId);

        } catch (BusinessException e) {
            log.error("完成案件进度失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("完成案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("完成案件进度失败");
        }
    }

    @Override
    public void deleteProgress(Long progressId) {
        log.info("删除案件进度，进度ID：{}", progressId);

        try {
            if (!caseProgressRepository.existsById(progressId)) {
                throw new BusinessException("案件进度不存在");
            }

            caseProgressRepository.deleteById(progressId);
            log.info("删除案件进度成功，进度ID：{}", progressId);

        } catch (BusinessException e) {
            log.error("删除案件进度失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("删除案件进度失败");
        }
    }

    @Override
    public List<CaseProgress> getProgressByCaseId(Long caseId) {
        log.info("查询案件进度，案件ID：{}", caseId);

        try {
            List<CaseProgress> progressList = caseProgressRepository.findByCaseIdOrderByStartDate(caseId);
            log.info("查询案件进度成功，案件ID：{}，进度数量：{}", caseId, progressList.size());
            return progressList;

        } catch (Exception e) {
            log.error("查询案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询案件进度失败");
        }
    }

    @Override
    public List<CaseProgress> getInProgressProgressByCaseId(Long caseId) {
        log.info("查询进行中的案件进度，案件ID：{}", caseId);

        try {
            List<CaseProgress> progressList = caseProgressRepository.findInProgressByCaseId(caseId);
            log.info("查询进行中的案件进度成功，案件ID：{}，进度数量：{}", caseId, progressList.size());
            return progressList;

        } catch (Exception e) {
            log.error("查询进行中的案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询进行中的案件进度失败");
        }
    }

    @Override
    public List<CaseProgress> getCompletedProgressByCaseId(Long caseId) {
        log.info("查询已完成的案件进度，案件ID：{}", caseId);

        try {
            List<CaseProgress> progressList = caseProgressRepository.findCompletedByCaseId(caseId);
            log.info("查询已完成的案件进度成功，案件ID：{}，进度数量：{}", caseId, progressList.size());
            return progressList;

        } catch (Exception e) {
            log.error("查询已完成的案件进度失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询已完成的案件进度失败");
        }
    }

    @Override
    public Double getOverallProgressPercentage(Long caseId) {
        log.info("查询案件整体进度百分比，案件ID：{}", caseId);

        try {
            Double avgPercentage = caseProgressRepository.getAverageCompletionPercentage(caseId);
            log.info("查询案件整体进度百分比成功，案件ID：{}，进度百分比：{}", caseId, avgPercentage);
            return avgPercentage != null ? avgPercentage : 0.0;

        } catch (Exception e) {
            log.error("查询案件整体进度百分比失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("查询案件整体进度百分比失败");
        }
    }
}
