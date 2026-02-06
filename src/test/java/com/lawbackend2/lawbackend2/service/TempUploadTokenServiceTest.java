package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.TempUploadToken;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.TempUploadTokenRepository;
import com.lawbackend2.lawbackend2.service.impl.TempUploadTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TempUploadTokenServiceTest {

    @Mock
    private TempUploadTokenRepository tempUploadTokenRepository;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private TempUploadTokenServiceImpl tempUploadTokenService;

    private TempUploadToken mockToken;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tempUploadTokenService, "defaultExpireMinutes", 30);

        mockToken = new TempUploadToken();
        mockToken.setId(1L);
        mockToken.setToken("TEMP550E8400E29B41D4A716446655440000");
        mockToken.setBizType("DOCUMENT");
        mockToken.setUserId(1L);
        mockToken.setExpireTime(LocalDateTime.now().plusMinutes(30));
        mockToken.setStatus("ACTIVE");
        mockToken.setFileCount(0);
        mockToken.setDescription("测试上传");

        mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    void testCreateToken_Success() {
        when(tempUploadTokenRepository.save(any(TempUploadToken.class))).thenAnswer(invocation -> {
            TempUploadToken token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });

        TempUploadToken result = tempUploadTokenService.createToken("DOCUMENT", 1L, "测试上传");

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertTrue(result.getToken().startsWith("TEMP"));
        assertEquals("DOCUMENT", result.getBizType());
        assertEquals(1L, result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(0, result.getFileCount());
        assertNotNull(result.getExpireTime());
        verify(tempUploadTokenRepository, times(1)).save(any(TempUploadToken.class));
    }

    @Test
    void testCreateToken_WithCustomExpireMinutes() {
        when(tempUploadTokenRepository.save(any(TempUploadToken.class))).thenAnswer(invocation -> {
            TempUploadToken token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });

        TempUploadToken result = tempUploadTokenService.createToken("DOCUMENT", 1L, "测试上传", 60);

        assertNotNull(result);
        assertNotNull(result.getExpireTime());
        assertTrue(result.getExpireTime().isAfter(LocalDateTime.now().plusMinutes(59)));
        verify(tempUploadTokenRepository, times(1)).save(any(TempUploadToken.class));
    }

    @Test
    void testValidateToken_Success() {
        when(tempUploadTokenRepository.findValidToken(eq("TEMP550E8400E29B41D4A716446655440000"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockToken));

        TempUploadToken result = tempUploadTokenService.validateToken("TEMP550E8400E29B41D4A716446655440000");

        assertNotNull(result);
        assertEquals("TEMP550E8400E29B41D4A716446655440000", result.getToken());
        verify(tempUploadTokenRepository, times(1)).findValidToken(anyString(), any(LocalDateTime.class));
    }

    @Test
    void testValidateToken_InvalidToken() {
        when(tempUploadTokenRepository.findValidToken(eq("INVALID_TOKEN"), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tempUploadTokenService.validateToken("INVALID_TOKEN");
        });

        assertEquals("上传令牌无效或已过期", exception.getMessage());
    }

    @Test
    void testUploadFileByToken_Success() throws IOException {
        FileRecord mockFileRecord = createMockFileRecord(1L, "test.pdf");

        when(tempUploadTokenRepository.findValidToken(eq("TEMP550E8400E29B41D4A716446655440000"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockToken));
        when(fileService.uploadFile(any(MultipartFile.class), eq("TEMP_UPLOAD"), eq("TEMP550E8400E29B41D4A716446655440000")))
                .thenReturn(mockFileRecord);
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(mockFileRecord);
        when(tempUploadTokenRepository.incrementFileCount(eq(1L), any(LocalDateTime.class))).thenReturn(1);

        FileRecord result = tempUploadTokenService.uploadFileByToken("TEMP550E8400E29B41D4A716446655440000", mockFile, "文件描述");

        assertNotNull(result);
        assertEquals("test.pdf", result.getOriginalFileName());
        assertEquals("文件描述", result.getDescription());
        verify(fileService, times(1)).uploadFile(any(MultipartFile.class), eq("TEMP_UPLOAD"), anyString());
        verify(tempUploadTokenRepository, times(1)).incrementFileCount(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void testUploadFileByToken_InvalidToken() {
        when(tempUploadTokenRepository.findValidToken(eq("INVALID_TOKEN"), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tempUploadTokenService.uploadFileByToken("INVALID_TOKEN", mockFile, null);
        });

        assertEquals("上传令牌无效或已过期", exception.getMessage());
        verify(fileService, never()).uploadFile(any(), any(), any());
    }

    @Test
    void testGetFilesByToken_Success() {
        FileRecord file1 = createMockFileRecord(1L, "file1.pdf");
        FileRecord file2 = createMockFileRecord(2L, "file2.pdf");

        when(tempUploadTokenRepository.findValidToken(eq("TEMP550E8400E29B41D4A716446655440000"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockToken));
        when(fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", "TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Arrays.asList(file1, file2));

        List<FileRecord> result = tempUploadTokenService.getFilesByToken("TEMP550E8400E29B41D4A716446655440000");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1.pdf", result.get(0).getOriginalFileName());
        assertEquals("file2.pdf", result.get(1).getOriginalFileName());
    }

    @Test
    void testTransferFilesToBiz_Success() {
        FileRecord file1 = createMockFileRecord(1L, "file1.pdf");
        FileRecord file2 = createMockFileRecord(2L, "file2.pdf");

        when(tempUploadTokenRepository.findValidToken(eq("TEMP550E8400E29B41D4A716446655440000"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockToken));
        when(fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", "TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Arrays.asList(file1, file2));
        when(fileRecordRepository.save(any(FileRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tempUploadTokenRepository.save(any(TempUploadToken.class))).thenReturn(mockToken);

        List<FileRecord> result = tempUploadTokenService.transferFilesToBiz(
                "TEMP550E8400E29B41D4A716446655440000", "DOCUMENT", "10086");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("DOCUMENT", result.get(0).getBizType());
        assertEquals("10086", result.get(0).getBizId());
        assertEquals("DOCUMENT", result.get(1).getBizType());
        assertEquals("10086", result.get(1).getBizId());
        verify(fileRecordRepository, times(2)).save(any(FileRecord.class));
        verify(tempUploadTokenRepository, times(1)).save(any(TempUploadToken.class));
    }

    @Test
    void testTransferFilesToBiz_NoFiles() {
        when(tempUploadTokenRepository.findValidToken(eq("TEMP550E8400E29B41D4A716446655440000"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockToken));
        when(fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", "TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Collections.emptyList());

        List<FileRecord> result = tempUploadTokenService.transferFilesToBiz(
                "TEMP550E8400E29B41D4A716446655440000", "DOCUMENT", "10086");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @Test
    void testCancelToken_Success() {
        FileRecord file1 = createMockFileRecord(1L, "file1.pdf");

        when(tempUploadTokenRepository.findByToken("TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Optional.of(mockToken));
        when(fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", "TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Arrays.asList(file1));
        when(tempUploadTokenRepository.save(any(TempUploadToken.class))).thenReturn(mockToken);
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(file1);

        tempUploadTokenService.cancelToken("TEMP550E8400E29B41D4A716446655440000");

        assertEquals("CANCELLED", mockToken.getStatus());
        assertEquals("CANCELLED", file1.getStatus());
        verify(tempUploadTokenRepository, times(1)).save(any(TempUploadToken.class));
        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    void testCancelToken_TokenNotFound() {
        when(tempUploadTokenRepository.findByToken("NON_EXISTENT_TOKEN"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            tempUploadTokenService.cancelToken("NON_EXISTENT_TOKEN");
        });

        verify(tempUploadTokenRepository, never()).save(any(TempUploadToken.class));
    }

    @Test
    void testCleanupExpiredTokens() {
        when(tempUploadTokenRepository.markExpiredTokens(any(LocalDateTime.class))).thenReturn(5);

        tempUploadTokenService.cleanupExpiredTokens();

        verify(tempUploadTokenRepository, times(1)).markExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void testGetTokenInfo_Success() {
        when(tempUploadTokenRepository.findByToken("TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Optional.of(mockToken));

        TempUploadToken result = tempUploadTokenService.getTokenInfo("TEMP550E8400E29B41D4A716446655440000");

        assertNotNull(result);
        assertEquals("TEMP550E8400E29B41D4A716446655440000", result.getToken());
    }

    @Test
    void testGetTokenInfo_NotFound() {
        when(tempUploadTokenRepository.findByToken("NON_EXISTENT_TOKEN"))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tempUploadTokenService.getTokenInfo("NON_EXISTENT_TOKEN");
        });

        assertEquals("Token不存在", exception.getMessage());
    }

    private FileRecord createMockFileRecord(Long id, String fileName) {
        FileRecord file = new FileRecord();
        file.setId(id);
        file.setOriginalFileName(fileName);
        file.setFilePath("/test/path/" + fileName);
        file.setFileSize(1024L);
        file.setBizType("TEMP_UPLOAD");
        file.setBizId("TEMP550E8400E29B41D4A716446655440000");
        file.setUploadTime(LocalDateTime.now());
        file.setUploadUserId(1L);
        file.setFileStatus(1);
        file.setStatus("ACTIVE");
        return file;
    }
}
