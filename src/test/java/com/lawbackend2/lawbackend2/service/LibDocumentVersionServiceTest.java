package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibVersionCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibVersionListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibVersionResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentVersion;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentVersionRepository;
import com.lawbackend2.lawbackend2.service.impl.LibDocumentVersionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibDocumentVersionServiceTest {

    @Mock
    private LibDocumentVersionRepository versionRepository;

    @Mock
    private LibDocumentRepository documentRepository;

    @Mock
    private LibDocumentOperationLogService operationLogService;

    @InjectMocks
    private LibDocumentVersionServiceImpl versionService;

    private LibDocument mockDocument;
    private LibDocumentVersion mockVersion;

    @BeforeEach
    void setUp() {
        mockDocument = LibDocument.builder()
                .documentName("测试文档")
                .documentCode("DOC20260309100000ABCD1234")
                .documentType("WORD")
                .fileName("测试文档.docx")
                .filePath("/uploads/测试文档.docx")
                .fileSize(10240L)
                .currentVersion(1)
                .build();
        mockDocument.setId(1L);
        mockDocument.setStatus("ACTIVE");
        mockDocument.setIsDeleted(false);

        mockVersion = LibDocumentVersion.builder()
                .documentId(1L)
                .versionNumber(1)
                .versionName("v1")
                .fileName("测试文档.docx")
                .filePath("/uploads/测试文档.docx")
                .fileSize(10240L)
                .changeSummary("初始版本")
                .changeType("CREATE")
                .isMajor(true)
                .build();
        mockVersion.setId(1L);
        mockVersion.setStatus("ACTIVE");
        mockVersion.setIsDeleted(false);
    }

    @Test
    void testCreateVersion_Success() {
        LibVersionCreateRequest request = LibVersionCreateRequest.builder()
                .documentId(1L)
                .fileName("测试文档v2.docx")
                .filePath("/uploads/测试文档v2.docx")
                .fileSize(20480L)
                .changeSummary("更新内容")
                .isMajor(false)
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(versionRepository.findMaxVersionNumber(1L)).thenReturn(1);
        when(versionRepository.save(any(LibDocumentVersion.class))).thenAnswer(invocation -> {
            LibDocumentVersion version = invocation.getArgument(0);
            version.setId(2L);
            return version;
        });
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);

        LibVersionResponse response = versionService.createVersion(request, 1L);

        assertNotNull(response);
        assertEquals(2, response.getVersionNumber());
        verify(versionRepository, times(1)).save(any(LibDocumentVersion.class));
    }

    @Test
    void testCreateVersion_DocumentNotFound() {
        LibVersionCreateRequest request = LibVersionCreateRequest.builder()
                .documentId(999L)
                .fileName("测试文档.docx")
                .filePath("/uploads/测试文档.docx")
                .build();

        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> versionService.createVersion(request, 1L));
    }

    @Test
    void testGetVersionList() {
        List<LibDocumentVersion> versions = new ArrayList<>();
        versions.add(mockVersion);

        when(versionRepository.findActiveVersionsByDocumentId(1L)).thenReturn(versions);

        LibVersionListResponse response = versionService.getVersionList(1L);

        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getVersions().size());
    }

    @Test
    void testGetVersion_Success() {
        when(versionRepository.findByDocumentIdAndVersionNumber(1L, 1)).thenReturn(Optional.of(mockVersion));

        LibVersionResponse response = versionService.getVersion(1L, 1);

        assertNotNull(response);
        assertEquals(1, response.getVersionNumber());
    }

    @Test
    void testGetVersion_NotFound() {
        when(versionRepository.findByDocumentIdAndVersionNumber(1L, 999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> versionService.getVersion(1L, 999));
    }

    @Test
    void testGetLatestVersion_Success() {
        when(versionRepository.findFirstByDocumentIdOrderByVersionNumberDesc(1L)).thenReturn(Optional.of(mockVersion));

        LibVersionResponse response = versionService.getLatestVersion(1L);

        assertNotNull(response);
        assertEquals(1, response.getVersionNumber());
    }

    @Test
    void testGetLatestVersion_NoVersions() {
        when(versionRepository.findFirstByDocumentIdOrderByVersionNumberDesc(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> versionService.getLatestVersion(1L));
    }

    @Test
    void testDeleteVersion_Success() {
        when(versionRepository.findById(1L)).thenReturn(Optional.of(mockVersion));
        when(versionRepository.save(any(LibDocumentVersion.class))).thenReturn(mockVersion);

        versionService.deleteVersion(1L, 1L);

        verify(versionRepository, times(1)).save(any(LibDocumentVersion.class));
    }

    @Test
    void testDeleteVersion_NotFound() {
        when(versionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> versionService.deleteVersion(999L, 1L));
    }

    @Test
    void testRestoreVersion_Success() {
        LibDocumentVersion version2 = LibDocumentVersion.builder()
                .documentId(1L)
                .versionNumber(2)
                .versionName("v2")
                .fileName("测试文档v2.docx")
                .filePath("/uploads/测试文档v2.docx")
                .fileSize(20480L)
                .build();
        version2.setId(2L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(versionRepository.findByDocumentIdAndVersionNumber(1L, 1)).thenReturn(Optional.of(mockVersion));
        when(versionRepository.findMaxVersionNumber(1L)).thenReturn(2);
        when(versionRepository.save(any(LibDocumentVersion.class))).thenAnswer(invocation -> {
            LibDocumentVersion version = invocation.getArgument(0);
            version.setId(3L);
            return version;
        });
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);

        versionService.restoreVersion(1L, 1, 1L);

        verify(versionRepository, times(1)).save(any(LibDocumentVersion.class));
        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testGetNextVersionNumber_FirstVersion() {
        when(versionRepository.findMaxVersionNumber(1L)).thenReturn(null);

        Integer nextVersion = versionService.getNextVersionNumber(1L);

        assertEquals(1, nextVersion);
    }

    @Test
    void testGetNextVersionNumber_ExistingVersions() {
        when(versionRepository.findMaxVersionNumber(1L)).thenReturn(3);

        Integer nextVersion = versionService.getNextVersionNumber(1L);

        assertEquals(4, nextVersion);
    }

    @Test
    void testGetTotalVersions() {
        when(versionRepository.countByDocumentId(1L)).thenReturn(5L);

        Long count = versionService.getTotalVersions(1L);

        assertEquals(5L, count);
    }
}
