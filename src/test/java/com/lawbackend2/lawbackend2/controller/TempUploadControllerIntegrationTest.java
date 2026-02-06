package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.TempUploadTokenCreateRequest;
import com.lawbackend2.lawbackend2.dto.TempUploadTransferRequest;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.TempUploadToken;
import com.lawbackend2.lawbackend2.service.TempUploadTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TempUploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TempUploadTokenService tempUploadTokenService;

    private TempUploadToken mockToken;
    private FileRecord mockFileRecord;

    @BeforeEach
    void setUp() {
        // 设置安全上下文
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockToken = new TempUploadToken();
        mockToken.setId(1L);
        mockToken.setToken("TEMP550E8400E29B41D4A716446655440000");
        mockToken.setBizType("DOCUMENT");
        mockToken.setUserId(1L);
        mockToken.setExpireTime(LocalDateTime.now().plusMinutes(30));
        mockToken.setStatus("ACTIVE");
        mockToken.setFileCount(0);
        mockToken.setDescription("测试上传");
        mockToken.setCreateTime(LocalDateTime.now());

        mockFileRecord = new FileRecord();
        mockFileRecord.setId(1L);
        mockFileRecord.setOriginalFileName("test.pdf");
        mockFileRecord.setFileSize(1024L);
        mockFileRecord.setFileExtension("pdf");
        mockFileRecord.setMimeType("application/pdf");
        mockFileRecord.setDescription("测试文件");
        mockFileRecord.setUploadTime(LocalDateTime.now());
        mockFileRecord.setBizId("TEMP550E8400E29B41D4A716446655440000");
    }

    @Test
    void testCreateToken_Success() throws Exception {
        when(tempUploadTokenService.createToken(anyString(), anyLong(), anyString(), anyInt()))
                .thenReturn(mockToken);

        TempUploadTokenCreateRequest request = new TempUploadTokenCreateRequest();
        request.setBizType("DOCUMENT");
        request.setDescription("测试上传");
        request.setExpireMinutes(30);

        mockMvc.perform(post("/temp-upload/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("TEMP550E8400E29B41D4A716446655440000"))
                .andExpect(jsonPath("$.data.bizType").value("DOCUMENT"))
                .andExpect(jsonPath("$.data.qrCodeContent").exists());

        verify(tempUploadTokenService, times(1)).createToken(anyString(), anyLong(), anyString(), anyInt());
    }

    @Test
    void testCreateToken_DefaultExpireMinutes() throws Exception {
        when(tempUploadTokenService.createToken(anyString(), anyLong(), any(), any()))
                .thenReturn(mockToken);

        TempUploadTokenCreateRequest request = new TempUploadTokenCreateRequest();
        request.setBizType("DOCUMENT");

        mockMvc.perform(post("/temp-upload/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tempUploadTokenService, times(1)).createToken(anyString(), anyLong(), any(), any());
    }

    @Test
    void testGetTokenInfo_Success() throws Exception {
        when(tempUploadTokenService.getTokenInfo("TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(mockToken);

        mockMvc.perform(get("/temp-upload/token/TEMP550E8400E29B41D4A716446655440000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("TEMP550E8400E29B41D4A716446655440000"))
                .andExpect(jsonPath("$.data.bizType").value("DOCUMENT"));

        verify(tempUploadTokenService, times(1)).getTokenInfo(anyString());
    }

    @Test
    void testValidateToken_Valid() throws Exception {
        when(tempUploadTokenService.validateToken("TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(mockToken);

        mockMvc.perform(get("/temp-upload/token/TEMP550E8400E29B41D4A716446655440000/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(tempUploadTokenService, times(1)).validateToken(anyString());
    }

    @Test
    void testValidateToken_Invalid() throws Exception {
        when(tempUploadTokenService.validateToken("INVALID_TOKEN"))
                .thenThrow(new RuntimeException("上传令牌无效或已过期"));

        mockMvc.perform(get("/temp-upload/token/INVALID_TOKEN/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void testGetFilesByToken_Success() throws Exception {
        when(tempUploadTokenService.getFilesByToken("TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Arrays.asList(mockFileRecord));

        mockMvc.perform(get("/temp-upload/token/TEMP550E8400E29B41D4A716446655440000/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].originalFileName").value("test.pdf"))
                .andExpect(jsonPath("$.data[0].fileSize").value(1024));

        verify(tempUploadTokenService, times(1)).getFilesByToken(anyString());
    }

    @Test
    void testGetFilesByToken_Empty() throws Exception {
        when(tempUploadTokenService.getFilesByToken("TEMP550E8400E29B41D4A716446655440000"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/temp-upload/token/TEMP550E8400E29B41D4A716446655440000/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testMobileUpload_Success() throws Exception {
        when(tempUploadTokenService.uploadFileByToken(anyString(), any(), any()))
                .thenReturn(mockFileRecord);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        mockMvc.perform(multipart("/temp-upload/mobile/upload")
                        .file(file)
                        .param("token", "TEMP550E8400E29B41D4A716446655440000")
                        .param("description", "测试文件"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.originalFileName").value("test.pdf"))
                .andExpect(jsonPath("$.data.description").value("测试文件"));

        verify(tempUploadTokenService, times(1)).uploadFileByToken(anyString(), any(), any());
    }

    @Test
    void testMobileUploadBatch_Success() throws Exception {
        FileRecord fileRecord2 = new FileRecord();
        fileRecord2.setId(2L);
        fileRecord2.setOriginalFileName("test2.pdf");
        fileRecord2.setFileSize(2048L);

        when(tempUploadTokenService.uploadFilesByToken(anyString(), any(), any()))
                .thenReturn(Arrays.asList(mockFileRecord, fileRecord2));

        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.pdf",
                "application/pdf",
                "test content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.pdf",
                "application/pdf",
                "test content 2".getBytes()
        );

        mockMvc.perform(multipart("/temp-upload/mobile/upload-batch")
                        .file(file1)
                        .file(file2)
                        .param("token", "TEMP550E8400E29B41D4A716446655440000")
                        .param("descriptions", "文件1")
                        .param("descriptions", "文件2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(tempUploadTokenService, times(1)).uploadFilesByToken(anyString(), any(), any());
    }

    @Test
    void testTransferFiles_Success() throws Exception {
        when(tempUploadTokenService.transferFilesToBiz(anyString(), anyString(), anyString()))
                .thenReturn(Arrays.asList(mockFileRecord));

        TempUploadTransferRequest request = new TempUploadTransferRequest();
        request.setToken("TEMP550E8400E29B41D4A716446655440000");
        request.setBizType("DOCUMENT");
        request.setBizId("10086");

        mockMvc.perform(post("/temp-upload/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].originalFileName").value("test.pdf"));

        verify(tempUploadTokenService, times(1)).transferFilesToBiz(anyString(), anyString(), anyString());
    }

    @Test
    void testCancelToken_Success() throws Exception {
        doNothing().when(tempUploadTokenService).cancelToken(anyString());

        mockMvc.perform(delete("/temp-upload/token/TEMP550E8400E29B41D4A716446655440000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tempUploadTokenService, times(1)).cancelToken(anyString());
    }
}
