package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibDocumentCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentQueryRequest;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentFolder;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentFavoriteRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentFolderRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.service.impl.LibDocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibDocumentServiceTest {

    @Mock
    private LibDocumentRepository documentRepository;

    @Mock
    private LibDocumentFolderRepository folderRepository;

    @Mock
    private LibDocumentFavoriteRepository favoriteRepository;

    @Mock
    private LibDocumentOperationLogService operationLogService;

    @Mock
    private LibDocumentVersionService versionService;

    @InjectMocks
    private LibDocumentServiceImpl documentService;

    private LibDocument mockDocument;
    private LibDocumentFolder mockFolder;

    @BeforeEach
    void setUp() {
        mockFolder = LibDocumentFolder.builder()
                .folderName("测试文件夹")
                .folderPath("/测试文件夹")
                .build();
        mockFolder.setId(1L);
        mockFolder.setStatus("ACTIVE");
        mockFolder.setIsDeleted(false);

        mockDocument = LibDocument.builder()
                .documentName("测试文档")
                .documentCode("DOC20260309100000ABCD1234")
                .folderId(1L)
                .documentType("WORD")
                .fileName("测试文档.docx")
                .filePath("/uploads/测试文档.docx")
                .fileSize(10240L)
                .fileExtension("docx")
                .mimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .currentVersion(1)
                .description("测试描述")
                .isPublic(false)
                .isLocked(false)
                .downloadCount(0)
                .viewCount(0)
                .build();
        mockDocument.setId(1L);
        mockDocument.setStatus("ACTIVE");
        mockDocument.setIsDeleted(false);
    }

    @Test
    void testCreateDocument_Success() {
        LibDocumentCreateRequest request = LibDocumentCreateRequest.builder()
                .documentName("新文档")
                .documentType("WORD")
                .fileName("新文档.docx")
                .filePath("/uploads/新文档.docx")
                .fileSize(20480L)
                .build();

        when(documentRepository.save(any(LibDocument.class))).thenAnswer(invocation -> {
            LibDocument doc = invocation.getArgument(0);
            doc.setId(2L);
            return doc;
        });

        LibDocumentResponse response = documentService.createDocument(request, 1L);

        assertNotNull(response);
        assertEquals("新文档", response.getDocumentName());
        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testCreateDocument_WithFolder() {
        LibDocumentCreateRequest request = LibDocumentCreateRequest.builder()
                .documentName("新文档")
                .folderId(1L)
                .documentType("WORD")
                .fileName("新文档.docx")
                .filePath("/uploads/新文档.docx")
                .build();

        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(documentRepository.save(any(LibDocument.class))).thenAnswer(invocation -> {
            LibDocument doc = invocation.getArgument(0);
            doc.setId(2L);
            return doc;
        });

        LibDocumentResponse response = documentService.createDocument(request, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getFolderId());
    }

    @Test
    void testCreateDocument_DuplicateCode() {
        LibDocumentCreateRequest request = LibDocumentCreateRequest.builder()
                .documentName("新文档")
                .documentCode("DOC20260309100000ABCD1234")
                .documentType("WORD")
                .fileName("新文档.docx")
                .filePath("/uploads/新文档.docx")
                .build();

        when(documentRepository.existsByDocumentCode("DOC20260309100000ABCD1234")).thenReturn(true);

        assertThrows(BusinessException.class, () -> documentService.createDocument(request, 1L));
    }

    @Test
    void testGetDocumentById_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);

        LibDocumentResponse response = documentService.getDocumentById(1L, 1L);

        assertNotNull(response);
        assertEquals("测试文档", response.getDocumentName());
    }

    @Test
    void testGetDocumentById_NotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> documentService.getDocumentById(999L, 1L));
    }

    @Test
    void testGetDocumentByCode_Success() {
        when(documentRepository.findByDocumentCode("DOC20260309100000ABCD1234")).thenReturn(Optional.of(mockDocument));
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);

        LibDocumentResponse response = documentService.getDocumentByCode("DOC20260309100000ABCD1234", 1L);

        assertNotNull(response);
        assertEquals("DOC20260309100000ABCD1234", response.getDocumentCode());
    }

    @Test
    void testUpdateDocument_Success() {
        LibDocumentUpdateRequest request = LibDocumentUpdateRequest.builder()
                .documentName("更新后的文档")
                .description("更新后的描述")
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);

        LibDocumentResponse response = documentService.updateDocument(1L, request, 1L);

        assertNotNull(response);
        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testUpdateDocument_Locked() {
        mockDocument.setIsLocked(true);
        mockDocument.setLockedBy(2L);

        LibDocumentUpdateRequest request = LibDocumentUpdateRequest.builder()
                .documentName("更新后的文档")
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        assertThrows(BusinessException.class, () -> documentService.updateDocument(1L, request, 1L));
    }

    @Test
    void testDeleteDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);

        documentService.deleteDocument(1L, 1L);

        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testLockDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);

        documentService.lockDocument(1L, 1L);

        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testLockDocument_AlreadyLocked() {
        mockDocument.setIsLocked(true);
        mockDocument.setLockedBy(1L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        assertThrows(BusinessException.class, () -> documentService.lockDocument(1L, 1L));
    }

    @Test
    void testUnlockDocument_Success() {
        mockDocument.setIsLocked(true);
        mockDocument.setLockedBy(1L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);

        documentService.unlockDocument(1L, 1L);

        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testUnlockDocument_NotLocker() {
        mockDocument.setIsLocked(true);
        mockDocument.setLockedBy(2L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        assertThrows(BusinessException.class, () -> documentService.unlockDocument(1L, 1L));
    }

    @Test
    void testMoveDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(folderRepository.findById(2L)).thenReturn(Optional.of(mockFolder));
        when(documentRepository.save(any(LibDocument.class))).thenReturn(mockDocument);

        documentService.moveDocument(1L, 2L, 1L);

        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testCopyDocument_Success() {
        LibDocumentFolder targetFolder = LibDocumentFolder.builder()
                .folderName("目标文件夹")
                .folderPath("/目标文件夹")
                .build();
        targetFolder.setId(2L);
        targetFolder.setStatus("ACTIVE");
        targetFolder.setIsDeleted(false);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(folderRepository.findById(2L)).thenReturn(Optional.of(targetFolder));
        when(documentRepository.save(any(LibDocument.class))).thenAnswer(invocation -> {
            LibDocument doc = invocation.getArgument(0);
            doc.setId(2L);
            return doc;
        });

        documentService.copyDocument(1L, 2L, 1L);

        verify(documentRepository, times(1)).save(any(LibDocument.class));
    }

    @Test
    void testGetMyDocuments() {
        List<LibDocument> documents = new ArrayList<>();
        documents.add(mockDocument);
        Page<LibDocument> page = new PageImpl<>(documents);

        when(documentRepository.findByCreateUserId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);

        LibDocumentListResponse response = documentService.getMyDocuments(1L, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getDocuments().size());
    }

    @Test
    void testSearchDocuments() {
        List<LibDocument> documents = new ArrayList<>();
        documents.add(mockDocument);
        Page<LibDocument> page = new PageImpl<>(documents);

        when(documentRepository.searchByKeyword(eq("测试"), any(Pageable.class))).thenReturn(page);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);

        LibDocumentListResponse response = documentService.searchDocuments("测试", 1, 10, 1L);

        assertNotNull(response);
        assertEquals(1, response.getDocuments().size());
    }

    @Test
    void testGenerateDocumentCode() {
        String code = documentService.generateDocumentCode();

        assertNotNull(code);
        assertTrue(code.startsWith("DOC"));
        assertTrue(code.length() > 10);
    }
}
