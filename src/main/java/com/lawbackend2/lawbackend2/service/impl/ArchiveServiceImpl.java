package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.ArchiveCategory;
import com.lawbackend2.lawbackend2.entity.ArchiveRecord;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ArchiveCategoryRepository;
import com.lawbackend2.lawbackend2.repository.ArchiveRecordRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.ArchiveService;
import com.lawbackend2.lawbackend2.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveCategoryRepository archiveCategoryRepository;
    private final ArchiveRecordRepository archiveRecordRepository;
    private final FileRecordRepository fileRecordRepository;
    private final FileService fileService;

    @Autowired
    public ArchiveServiceImpl(ArchiveCategoryRepository archiveCategoryRepository,
                            ArchiveRecordRepository archiveRecordRepository,
                            FileRecordRepository fileRecordRepository,
                            FileService fileService) {
        this.archiveCategoryRepository = archiveCategoryRepository;
        this.archiveRecordRepository = archiveRecordRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.fileService = fileService;
    }

    @Override
    public List<ArchiveCategoryResponse> getCategoryTree(String status) {
        if (status == null) {
            status = "ACTIVE";
        }

        List<ArchiveCategory> rootCategories = archiveCategoryRepository.findByStatusOrderBySortOrderAsc(status);

        return buildCategoryTree(rootCategories, status);
    }

    private List<ArchiveCategoryResponse> buildCategoryTree(List<ArchiveCategory> categories, String status) {
        Map<Long, ArchiveCategoryResponse> categoryMap = new ConcurrentHashMap<>();
        List<ArchiveCategoryResponse> rootCategories = new ArrayList<>();

        for (ArchiveCategory category : categories) {
            ArchiveCategoryResponse response = convertToCategoryResponse(category);
            categoryMap.put(category.getId(), response);

            if (category.getParentId() == null) {
                rootCategories.add(response);
            }
        }

        for (ArchiveCategory category : categories) {
            if (category.getParentId() != null) {
                ArchiveCategoryResponse parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    ArchiveCategoryResponse child = categoryMap.get(category.getId());
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(child);
                }
            }
        }

        return rootCategories;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArchiveRecordResponse uploadArchiveFile(Long caseId, MultipartFile file, ArchiveUploadRequest request, Long userId) {
        validateFile(file);

        FileRecord fileRecord = fileService.uploadFile(file, "archive", String.valueOf(caseId));

        ArchiveRecord archiveRecord = new ArchiveRecord();
        archiveRecord.setCaseId(caseId);
        archiveRecord.setCategoryCode(request.getCategoryCode());
        archiveRecord.setFileId(fileRecord.getId());
        archiveRecord.setFileTitle(request.getFileTitle() != null ? request.getFileTitle() : file.getOriginalFilename());
        archiveRecord.setFileDescription(request.getFileDescription());
        archiveRecord.setUploadUserId(userId);
        archiveRecord.setUploadTime(LocalDateTime.now());
        archiveRecord.setIsConfidential(request.getIsConfidential() != null ? request.getIsConfidential() : false);
        archiveRecord.setAccessLevel(request.getAccessLevel() != null ? request.getAccessLevel() : "INTERNAL");
        archiveRecord.setArchiveNo(generateArchiveNo());
        archiveRecord.setVersion(1);
        archiveRecord.setCreateUserId(userId);
        archiveRecord.setUpdateUserId(userId);

        archiveRecord = archiveRecordRepository.save(archiveRecord);

        log.info("用户[{}]上传归档文件成功,案件ID:{},文件ID:{},归档编号:{}", userId, caseId, fileRecord.getId(), archiveRecord.getArchiveNo());

        return convertToArchiveRecordResponse(archiveRecord);
    }

    @Override
    public PageResult<ArchiveRecordResponse> getArchiveFiles(Long caseId, String categoryCode, Integer pageNum, Integer pageSize, String status, String keyword) {
        if (status == null) {
            status = "ACTIVE";
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "uploadTime"));

        Page<ArchiveRecord> page;
        if (categoryCode != null && keyword != null) {
            page = archiveRecordRepository.findByCaseIdAndCategoryCodeAndStatusAndKeyword(caseId, categoryCode, status, keyword, pageable);
        } else if (categoryCode != null) {
            page = archiveRecordRepository.findByCaseIdAndCategoryCodeAndStatus(caseId, categoryCode, status, pageable);
        } else if (keyword != null) {
            page = archiveRecordRepository.findByCaseIdAndStatusAndKeyword(caseId, status, keyword, pageable);
        } else {
            page = archiveRecordRepository.findByCaseIdAndStatus(caseId, status, pageable);
        }

        List<ArchiveRecordResponse> responses = page.getContent().stream()
                .map(this::convertToArchiveRecordResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responses, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public ArchiveRecordResponse getArchiveRecord(Long recordId) {
        ArchiveRecord archiveRecord = archiveRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("归档记录不存在"));

        return convertToArchiveRecordResponse(archiveRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArchiveRecordResponse updateArchiveRecord(Long recordId, ArchiveUpdateRequest request, Long userId) {
        ArchiveRecord archiveRecord = archiveRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("归档记录不存在"));

        if (request.getFileTitle() != null) {
            archiveRecord.setFileTitle(request.getFileTitle());
        }
        if (request.getFileDescription() != null) {
            archiveRecord.setFileDescription(request.getFileDescription());
        }
        if (request.getIsConfidential() != null) {
            archiveRecord.setIsConfidential(request.getIsConfidential());
        }
        if (request.getAccessLevel() != null) {
            archiveRecord.setAccessLevel(request.getAccessLevel());
        }

        archiveRecord.setUpdateUserId(userId);
        archiveRecord.setUpdateTime(LocalDateTime.now());

        archiveRecord = archiveRecordRepository.save(archiveRecord);

        log.info("用户[{}]更新归档记录成功,记录ID:{}", userId, recordId);

        return convertToArchiveRecordResponse(archiveRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArchiveRecord(Long recordId, Long userId) {
        ArchiveRecord archiveRecord = archiveRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("归档记录不存在"));

        archiveRecord.setStatus("DELETED");
        archiveRecord.setIsDeleted(true);
        archiveRecord.setUpdateUserId(userId);
        archiveRecord.setUpdateTime(LocalDateTime.now());
        archiveRecordRepository.save(archiveRecord);

        FileRecord fileRecord = fileRecordRepository.findById(archiveRecord.getFileId()).orElse(null);
        if (fileRecord != null) {
            fileRecord.setIsDeleted(true);
            fileRecord.setDeleteTime(LocalDateTime.now());
            fileRecord.setDeleteUserId(userId);
            fileRecordRepository.save(fileRecord);
        }

        log.info("用户[{}]删除归档记录成功,记录ID:{}", userId, recordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArchiveRecords(List<Long> recordIds, Long userId) {
        for (Long recordId : recordIds) {
            deleteArchiveRecord(recordId, userId);
        }
    }

    @Override
    public Long getArchiveCount(Long caseId, String categoryCode, String status) {
        if (status == null) {
            status = "ACTIVE";
        }

        if (categoryCode != null) {
            return archiveRecordRepository.countByCaseIdAndCategoryCodeAndStatus(caseId, categoryCode, status);
        } else {
            return archiveRecordRepository.countByCaseIdAndStatus(caseId, status);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过50MB");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = List.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "jpg", "jpeg", "png", "gif", "bmp", "txt");
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException("不支持的文件格式");
        }
    }

    private String generateArchiveNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AH-" + dateStr + "-";

        long count = archiveRecordRepository.count() + 1;
        return prefix + String.format("%04d", count);
    }

    private ArchiveCategoryResponse convertToCategoryResponse(ArchiveCategory category) {
        ArchiveCategoryResponse response = new ArchiveCategoryResponse();
        BeanUtils.copyProperties(category, response);
        response.setChildren(new ArrayList<>());
        return response;
    }

    private ArchiveRecordResponse convertToArchiveRecordResponse(ArchiveRecord archiveRecord) {
        ArchiveRecordResponse response = new ArchiveRecordResponse();
        BeanUtils.copyProperties(archiveRecord, response);

        ArchiveCategory category = archiveCategoryRepository.findByCategoryCode(archiveRecord.getCategoryCode());
        if (category != null) {
            response.setCategoryName(category.getCategoryName());
        }

        FileRecord fileRecord = fileRecordRepository.findById(archiveRecord.getFileId()).orElse(null);
        if (fileRecord != null) {
            FileRecordInfo fileInfo = new FileRecordInfo();
            fileInfo.setId(fileRecord.getId());
            fileInfo.setOriginalFileName(fileRecord.getOriginalFileName());
            fileInfo.setFileSize(fileRecord.getFileSize());
            fileInfo.setFileExtension(fileRecord.getFileExtension());
            fileInfo.setMimeType(fileRecord.getMimeType());
            response.setFile(fileInfo);
        }

        return response;
    }
}
