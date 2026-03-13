package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibShareCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.dto.response.LibShareResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentShare;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentShareRepository;
import com.lawbackend2.lawbackend2.service.impl.LibDocumentShareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibDocumentShareServiceTest {

    @Mock
    private LibDocumentShareRepository shareRepository;

    @Mock
    private LibDocumentRepository documentRepository;

    @Mock
    private LibDocumentService documentService;

    @Mock
    private LibDocumentOperationLogService operationLogService;

    @InjectMocks
    private LibDocumentShareServiceImpl shareService;

    private LibDocument mockDocument;
    private LibDocumentShare mockShare;

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

        mockShare = LibDocumentShare.builder()
                .documentId(1L)
                .shareCode("ABC123DEF456")
                .sharePassword("123456")
                .permissionType("READ")
                .expireTime(LocalDateTime.now().plusDays(7))
                .maxAccessCount(100)
                .accessCount(0)
                .isEnabled(true)
                .build();
        mockShare.setId(1L);
        mockShare.setStatus("ACTIVE");
        mockShare.setIsDeleted(false);
    }

    @Test
    void testCreateShare_Success() {
        LibShareCreateRequest request = LibShareCreateRequest.builder()
                .documentId(1L)
                .sharePassword("123456")
                .permissionType("READ")
                .maxAccessCount(100)
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(shareRepository.save(any(LibDocumentShare.class))).thenAnswer(invocation -> {
            LibDocumentShare share = invocation.getArgument(0);
            share.setId(1L);
            return share;
        });

        LibShareResponse response = shareService.createShare(request, 1L);

        assertNotNull(response);
        assertNotNull(response.getShareCode());
        verify(shareRepository, times(1)).save(any(LibDocumentShare.class));
    }

    @Test
    void testCreateShare_DocumentNotFound() {
        LibShareCreateRequest request = LibShareCreateRequest.builder()
                .documentId(999L)
                .build();

        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> shareService.createShare(request, 1L));
    }

    @Test
    void testGetShareByCode_Success() {
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        LibShareResponse response = shareService.getShareByCode("ABC123DEF456");

        assertNotNull(response);
        assertEquals("ABC123DEF456", response.getShareCode());
    }

    @Test
    void testGetShareByCode_NotFound() {
        when(shareRepository.findByShareCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> shareService.getShareByCode("INVALID"));
    }

    @Test
    void testGetShareByCode_Disabled() {
        mockShare.setIsEnabled(false);
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        assertThrows(BusinessException.class, () -> shareService.getShareByCode("ABC123DEF456"));
    }

    @Test
    void testGetShareByCode_Expired() {
        mockShare.setExpireTime(LocalDateTime.now().minusDays(1));
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        assertThrows(BusinessException.class, () -> shareService.getShareByCode("ABC123DEF456"));
    }

    @Test
    void testAccessSharedDocument_Success() {
        mockShare.setSharePassword(null);
        LibDocumentResponse docResponse = LibDocumentResponse.builder()
                .id(1L)
                .documentName("测试文档")
                .build();

        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));
        when(documentService.getDocumentById(1L, 1L)).thenReturn(docResponse);
        doNothing().when(shareRepository).incrementAccessCount(1L);

        LibDocumentResponse response = shareService.accessSharedDocument("ABC123DEF456", null, 1L);

        assertNotNull(response);
        assertEquals("测试文档", response.getDocumentName());
    }

    @Test
    void testAccessSharedDocument_WithPassword() {
        mockShare.setSharePassword("123456");
        LibDocumentResponse docResponse = LibDocumentResponse.builder()
                .id(1L)
                .documentName("测试文档")
                .build();

        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));
        when(documentService.getDocumentById(1L, 1L)).thenReturn(docResponse);
        doNothing().when(shareRepository).incrementAccessCount(1L);

        LibDocumentResponse response = shareService.accessSharedDocument("ABC123DEF456", "123456", 1L);

        assertNotNull(response);
    }

    @Test
    void testAccessSharedDocument_WrongPassword() {
        mockShare.setSharePassword("123456");
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        assertThrows(BusinessException.class, () -> 
            shareService.accessSharedDocument("ABC123DEF456", "wrong", 1L));
    }

    @Test
    void testAccessSharedDocument_MaxAccessCount() {
        mockShare.setMaxAccessCount(2);
        mockShare.setAccessCount(2);
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        assertThrows(BusinessException.class, () -> 
            shareService.accessSharedDocument("ABC123DEF456", null, 1L));
    }

    @Test
    void testDeleteShare_Success() {
        when(shareRepository.findById(1L)).thenReturn(Optional.of(mockShare));
        when(shareRepository.save(any(LibDocumentShare.class))).thenReturn(mockShare);

        shareService.deleteShare(1L, 1L);

        verify(shareRepository, times(1)).save(any(LibDocumentShare.class));
    }

    @Test
    void testDisableShare_Success() {
        when(shareRepository.findById(1L)).thenReturn(Optional.of(mockShare));
        when(shareRepository.save(any(LibDocumentShare.class))).thenReturn(mockShare);

        shareService.disableShare(1L, 1L);

        verify(shareRepository, times(1)).save(any(LibDocumentShare.class));
    }

    @Test
    void testEnableShare_Success() {
        mockShare.setIsEnabled(false);
        when(shareRepository.findById(1L)).thenReturn(Optional.of(mockShare));
        when(shareRepository.save(any(LibDocumentShare.class))).thenReturn(mockShare);

        shareService.enableShare(1L, 1L);

        verify(shareRepository, times(1)).save(any(LibDocumentShare.class));
    }

    @Test
    void testIsShareValid_Success() {
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        boolean valid = shareService.isShareValid("ABC123DEF456");

        assertTrue(valid);
    }

    @Test
    void testIsShareValid_NotFound() {
        when(shareRepository.findByShareCode("INVALID")).thenReturn(Optional.empty());

        boolean valid = shareService.isShareValid("INVALID");

        assertFalse(valid);
    }

    @Test
    void testIsShareValid_Disabled() {
        mockShare.setIsEnabled(false);
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        boolean valid = shareService.isShareValid("ABC123DEF456");

        assertFalse(valid);
    }

    @Test
    void testIsShareValid_Expired() {
        mockShare.setExpireTime(LocalDateTime.now().minusDays(1));
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        boolean valid = shareService.isShareValid("ABC123DEF456");

        assertFalse(valid);
    }

    @Test
    void testGenerateShareCode() {
        String code = shareService.generateShareCode();

        assertNotNull(code);
        assertEquals(12, code.length());
        assertTrue(code.matches("[A-Z0-9]+"));
    }

    @Test
    void testCheckSharePassword_Correct() {
        mockShare.setSharePassword("123456");
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        boolean result = shareService.checkSharePassword("ABC123DEF456", "123456");

        assertTrue(result);
    }

    @Test
    void testCheckSharePassword_Wrong() {
        mockShare.setSharePassword("123456");
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        boolean result = shareService.checkSharePassword("ABC123DEF456", "wrong");

        assertFalse(result);
    }

    @Test
    void testCheckSharePassword_NoPassword() {
        mockShare.setSharePassword(null);
        when(shareRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(mockShare));

        boolean result = shareService.checkSharePassword("ABC123DEF456", null);

        assertTrue(result);
    }
}
