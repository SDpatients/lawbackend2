package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ArchiveCategoryResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveRecordResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveUpdateRequest;
import com.lawbackend2.lawbackend2.dto.ArchiveUploadRequest;
import com.lawbackend2.lawbackend2.entity.ArchiveCategory;
import com.lawbackend2.lawbackend2.entity.ArchiveRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ArchiveCategoryRepository;
import com.lawbackend2.lawbackend2.repository.ArchiveRecordRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ArchiveServiceTest {

    @Mock
    private ArchiveCategoryRepository archiveCategoryRepository;

    @Mock
    private ArchiveRecordRepository archiveRecordRepository;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private ArchiveService archiveService;

    private ArchiveCategory testCategory;
    private ArchiveRecord testArchiveRecord;

    @BeforeEach
    void setUp() {
        testCategory = new ArchiveCategory();
        testCategory.setId(1L);
        testCategory.setCategoryCode("0-1-1");
        testCategory.setCategoryName("1.1 预重整/庭外重组启动文件");
        testCategory.setParentId(null);
        testCategory.setLevel(1);
        testCategory.setSortOrder(0);
        testCategory.setIsRequired(true);
        testCategory.setStatus("ACTIVE");

        testArchiveRecord = new ArchiveRecord();
        testArchiveRecord.setId(1L);
        testArchiveRecord.setCaseId(1L);
        testArchiveRecord.setCategoryCode("0-1-1");
        testArchiveRecord.setFileId(1L);
        testArchiveRecord.setArchiveNo("AH-20260116-0001");
        testArchiveRecord.setFileTitle("测试文件");
        testArchiveRecord.setFileDescription("测试文件描述");
        testArchiveRecord.setUploadUserId(1L);
        testArchiveRecord.setUploadTime(LocalDateTime.now());
        testArchiveRecord.setStatus("ACTIVE");
        testArchiveRecord.setIsConfidential(false);
        testArchiveRecord.setAccessLevel("INTERNAL");
        testArchiveRecord.setVersion(1);
    }

    @AfterEach
    void tearDown() {
        reset(archiveCategoryRepository, archiveRecordRepository, fileRecordRepository);
    }

    @Test
    void testGetCategoryTree_Success() {
        when(archiveCategoryRepository.findByStatusOrderBySortOrderAsc("ACTIVE"))
                .thenReturn(java.util.List.of(testCategory));

        List<ArchiveCategoryResponse> result = archiveService.getCategoryTree("ACTIVE");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("0-1-1", result.get(0).getCategoryCode());

        verify(archiveCategoryRepository, times(1)).findByStatusOrderBySortOrderAsc("ACTIVE");
    }

    @Test
    void testGetCategoryTree_DefaultStatus() {
        when(archiveCategoryRepository.findByStatusOrderBySortOrderAsc("ACTIVE"))
                .thenReturn(java.util.List.of(testCategory));

        List<ArchiveCategoryResponse> result = archiveService.getCategoryTree(null);

        assertNotNull(result);
        verify(archiveCategoryRepository, times(1)).findByStatusOrderBySortOrderAsc("ACTIVE");
    }

    @Test
    void testUploadArchiveFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        ArchiveUploadRequest request = new ArchiveUploadRequest();
        request.setCategoryCode("0-1-1");
        request.setFileTitle("测试文件");
        request.setFileDescription("测试描述");
        request.setIsConfidential(false);
        request.setAccessLevel("INTERNAL");

        ArchiveRecordResponse response = archiveService.uploadArchiveFile(1L, file, request, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getCaseId());
        assertEquals("0-1-1", response.getCategoryCode());
        assertEquals("测试文件", response.getFileTitle());

        verify(fileRecordRepository, times(1)).save(any());
        verify(archiveRecordRepository, times(1)).save(any());
    }

    @Test
    void testUploadArchiveFile_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "",
                "application/pdf",
                new byte[0]
        );

        ArchiveUploadRequest request = new ArchiveUploadRequest();
        request.setCategoryCode("0-1-1");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            archiveService.uploadArchiveFile(1L, file, request, 1L);
        });

        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    void testUploadArchiveFile_InvalidFileFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "test content".getBytes()
        );

        ArchiveUploadRequest request = new ArchiveUploadRequest();
        request.setCategoryCode("0-1-1");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            archiveService.uploadArchiveFile(1L, file, request, 1L);
        });

        assertEquals("不支持的文件格式", exception.getMessage());
    }

    @Test
    void testUploadArchiveFile_FileTooLarge() {
        byte[] largeContent = new byte[50 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                largeContent
        );

        ArchiveUploadRequest request = new ArchiveUploadRequest();
        request.setCategoryCode("0-1-1");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            archiveService.uploadArchiveFile(1L, file, request, 1L);
        });

        assertTrue(exception.getMessage().contains("文件大小不能超过50MB"));
    }

    @Test
    void testGetArchiveFiles_Success() {
        when(archiveRecordRepository.findByCaseIdAndCategoryCodeAndStatus(anyLong(), anyString(), anyString(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(testArchiveRecord)));

        com.lawbackend2.lawbackend2.common.PageResult<ArchiveRecordResponse> result = 
                archiveService.getArchiveFiles(1L, "0-1-1", 1, 10, "ACTIVE", null);

        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());

        verify(archiveRecordRepository, times(1))
                .findByCaseIdAndCategoryCodeAndStatus(1L, "0-1-1", "ACTIVE", any());
    }

    @Test
    void testGetArchiveFiles_WithKeyword() {
        when(archiveRecordRepository.findByCaseIdAndStatusAndKeyword(anyLong(), anyString(), anyString(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(testArchiveRecord)));

        com.lawbackend2.lawbackend2.common.PageResult<ArchiveRecordResponse> result = 
                archiveService.getArchiveFiles(1L, null, 1, 10, "ACTIVE", "测试");

        assertNotNull(result);
        verify(archiveRecordRepository, times(1))
                .findByCaseIdAndStatusAndKeyword(1L, "ACTIVE", "测试", any());
    }

    @Test
    void testGetArchiveRecord_Success() {
        when(archiveRecordRepository.findById(1L))
                .thenReturn(java.util.Optional.of(testArchiveRecord));

        ArchiveRecordResponse response = archiveService.getArchiveRecord(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("测试文件", response.getFileTitle());

        verify(archiveRecordRepository, times(1)).findById(1L);
    }

    @Test
    void testGetArchiveRecord_NotFound() {
        when(archiveRecordRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            archiveService.getArchiveRecord(999L);
        });

        assertEquals("归档记录不存在", exception.getMessage());
    }

    @Test
    void testUpdateArchiveRecord_Success() {
        when(archiveRecordRepository.findById(1L))
                .thenReturn(java.util.Optional.of(testArchiveRecord));

        ArchiveUpdateRequest request = new ArchiveUpdateRequest();
        request.setFileTitle("更新后的标题");
        request.setFileDescription("更新后的描述");
        request.setIsConfidential(true);
        request.setAccessLevel("CONFIDENTIAL");

        ArchiveRecordResponse response = archiveService.updateArchiveRecord(1L, request, 1L);

        assertNotNull(response);
        assertEquals("更新后的标题", response.getFileTitle());
        assertEquals(true, response.getIsConfidential());
        assertEquals("CONFIDENTIAL", response.getAccessLevel());

        verify(archiveRecordRepository, times(1)).save(any());
    }

    @Test
    void testUpdateArchiveRecord_NotFound() {
        when(archiveRecordRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ArchiveUpdateRequest request = new ArchiveUpdateRequest();
        request.setFileTitle("更新后的标题");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            archiveService.updateArchiveRecord(999L, request, 1L);
        });

        assertEquals("归档记录不存在", exception.getMessage());
    }

    @Test
    void testDeleteArchiveRecord_Success() {
        when(archiveRecordRepository.findById(1L))
                .thenReturn(java.util.Optional.of(testArchiveRecord));

        archiveService.deleteArchiveRecord(1L, 1L);

        verify(archiveRecordRepository, times(1)).save(any());
        verify(fileRecordRepository, times(1)).save(any());
    }

    @Test
    void testDeleteArchiveRecord_NotFound() {
        when(archiveRecordRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            archiveService.deleteArchiveRecord(999L, 1L);
        });

        assertEquals("归档记录不存在", exception.getMessage());
    }

    @Test
    void testDeleteArchiveRecords_Success() {
        when(archiveRecordRepository.findById(1L))
                .thenReturn(java.util.Optional.of(testArchiveRecord));

        archiveService.deleteArchiveRecords(java.util.List.of(1L, 2L), 1L);

        verify(archiveRecordRepository, times(2)).findById(anyLong());
        verify(archiveRecordRepository, times(2)).save(any());
    }

    @Test
    void testGetArchiveCount_Success() {
        when(archiveRecordRepository.countByCaseIdAndStatus(1L, "ACTIVE"))
                .thenReturn(10L);

        Long count = archiveService.getArchiveCount(1L, null, "ACTIVE");

        assertEquals(10L, count);
        verify(archiveRecordRepository, times(1))
                .countByCaseIdAndStatus(1L, "ACTIVE");
    }

    @Test
    void testGetArchiveCount_WithCategoryCode() {
        when(archiveRecordRepository.countByCaseIdAndCategoryCodeAndStatus(1L, "0-1-1", "ACTIVE"))
                .thenReturn(5L);

        Long count = archiveService.getArchiveCount(1L, "0-1-1", "ACTIVE");

        assertEquals(5L, count);
        verify(archiveRecordRepository, times(1))
                .countByCaseIdAndCategoryCodeAndStatus(1L, "0-1-1", "ACTIVE");
    }
}
