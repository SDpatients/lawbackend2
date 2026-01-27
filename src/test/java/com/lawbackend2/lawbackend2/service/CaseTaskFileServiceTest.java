package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.CaseTaskNotFoundException;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.impl.CaseTaskFileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseTaskFileServiceTest {

    @Mock
    private CaseTaskRepository caseTaskRepository;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CaseTaskFileServiceImpl caseTaskFileService;

    private CaseTask mockTask;
    private User mockUser;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockTask = new CaseTask();
        mockTask.setId(1L);
        mockTask.setCaseId(1L);
        mockTask.setTaskCode("TASK_001");
        mockTask.setTaskName("测试任务");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRealName("张三");

        mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    void testUploadFile_Success() throws IOException {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(fileRecordRepository.save(any(FileRecord.class))).thenAnswer(invocation -> {
            FileRecord file = invocation.getArgument(0);
            file.setId(1L);
            return file;
        });

        FileRecord result = caseTaskFileService.uploadFile(1L, mockFile, "文件描述", 1L);

        assertNotNull(result);
        assertEquals("test.pdf", result.getOriginalFileName());
        assertEquals("CASE_TASK", result.getBizType());
        assertEquals("1", result.getBizId());
        assertEquals(1L, result.getUploadUserId());
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    void testUploadFile_TaskNotFound() {
        when(caseTaskRepository.findById(999L)).thenReturn(Optional.empty());

        com.lawbackend2.lawbackend2.exception.BusinessException exception = assertThrows(
                com.lawbackend2.lawbackend2.exception.BusinessException.class, () -> {
                    caseTaskFileService.uploadFile(999L, mockFile, "描述", 1L);
                });

        assertTrue(exception.getMessage().contains("案件任务不存在"));
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @Test
    void testUploadFile_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]
        );

        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        com.lawbackend2.lawbackend2.exception.BusinessException exception = assertThrows(
                com.lawbackend2.lawbackend2.exception.BusinessException.class, () -> {
                    caseTaskFileService.uploadFile(1L, emptyFile, "描述", 1L);
                });

        assertEquals("文件不能为空", exception.getMessage());
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @Test
    void testUploadFile_FileTooLarge() {
        byte[] largeContent = new byte[51 * 1024 * 1024];
        MultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                largeContent
        );

        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        com.lawbackend2.lawbackend2.exception.BusinessException exception = assertThrows(
                com.lawbackend2.lawbackend2.exception.BusinessException.class, () -> {
                    caseTaskFileService.uploadFile(1L, largeFile, "描述", 1L);
                });

        assertEquals("文件大小不能超过50MB", exception.getMessage());
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @Test
    void testGetFilesByTaskId_Success() {
        FileRecord file1 = createMockFileRecord(1L, "file1.pdf", 1L);
        FileRecord file2 = createMockFileRecord(2L, "file2.pdf", 2L);

        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(fileRecordRepository.findByConditions(eq("CASE_TASK"), eq("1"), eq("ACTIVE"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(file1, file2)));
        when(userRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(mockUser));

        List<FileRecordInfo> result = caseTaskFileService.getFilesByTaskId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1.pdf", result.get(0).getOriginalFileName());
        assertEquals("张三", result.get(0).getUploadUserName());
        verify(caseTaskRepository, times(1)).findById(1L);
        verify(fileRecordRepository, times(1)).findByConditions(eq("CASE_TASK"), eq("1"), eq("ACTIVE"), any(Pageable.class));
    }

    @Test
    void testGetFilesByTaskId_TaskNotFound() {
        when(caseTaskRepository.findById(999L)).thenReturn(Optional.empty());

        CaseTaskNotFoundException exception = assertThrows(CaseTaskNotFoundException.class, () -> {
            caseTaskFileService.getFilesByTaskId(999L);
        });

        assertTrue(exception.getMessage().contains("案件任务不存在"));
        verify(fileRecordRepository, never()).findByConditions(any(), any(), any(), any());
    }

    @Test
    void testGetFilesByTaskId_EmptyFiles() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(fileRecordRepository.findByConditions(eq("CASE_TASK"), eq("1"), eq("ACTIVE"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList()));

        List<FileRecordInfo> result = caseTaskFileService.getFilesByTaskId(1L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testDeleteFile_Success() {
        FileRecord file = createMockFileRecord(1L, "test.pdf", 1L);

        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(fileRecordRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(file);

        caseTaskFileService.deleteFile(1L, 1L, 1L);

        assertTrue(file.getIsDeleted());
        assertNotNull(file.getDeleteTime());
        assertEquals(1L, file.getDeleteUserId());
        verify(caseTaskRepository, times(1)).findById(1L);
        verify(fileRecordRepository, times(1)).findById(1L);
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    void testDeleteFile_TaskNotFound() {
        when(caseTaskRepository.findById(999L)).thenReturn(Optional.empty());

        CaseTaskNotFoundException exception = assertThrows(CaseTaskNotFoundException.class, () -> {
            caseTaskFileService.deleteFile(999L, 1L, 1L);
        });

        assertTrue(exception.getMessage().contains("案件任务不存在"));
        verify(fileRecordRepository, never()).findById(any());
        verify(fileRecordRepository, never()).save(any());
    }

    @Test
    void testDeleteFile_FileNotFound() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(fileRecordRepository.findById(999L)).thenReturn(Optional.empty());

        com.lawbackend2.lawbackend2.exception.BusinessException exception = assertThrows(
                com.lawbackend2.lawbackend2.exception.BusinessException.class, () -> {
                    caseTaskFileService.deleteFile(1L, 999L, 1L);
                });

        assertEquals("文件不存在", exception.getMessage());
        verify(fileRecordRepository, never()).save(any());
    }

    @Test
    void testDeleteFile_FileNotBelongToTask() {
        FileRecord file = createMockFileRecord(1L, "test.pdf", 1L);
        file.setBizType("OTHER_TYPE");
        file.setBizId("999");

        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(fileRecordRepository.findById(1L)).thenReturn(Optional.of(file));

        com.lawbackend2.lawbackend2.exception.BusinessException exception = assertThrows(
                com.lawbackend2.lawbackend2.exception.BusinessException.class, () -> {
                    caseTaskFileService.deleteFile(1L, 1L, 1L);
                });

        assertEquals("文件不属于该任务", exception.getMessage());
        verify(fileRecordRepository, never()).save(any());
    }

    private FileRecord createMockFileRecord(Long id, String fileName, Long userId) {
        FileRecord file = new FileRecord();
        file.setId(id);
        file.setOriginalFileName(fileName);
        file.setFilePath("/test/path/" + fileName);
        file.setFileSize(1024L);
        file.setBizType("CASE_TASK");
        file.setBizId("1");
        file.setUploadTime(LocalDateTime.now());
        file.setUploadUserId(userId);
        file.setFileStatus(1);
        file.setStatus("ACTIVE");
        return file;
    }
}
