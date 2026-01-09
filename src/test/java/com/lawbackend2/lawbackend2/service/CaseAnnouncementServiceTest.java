package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseAnnouncementCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementPublishRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CaseAnnouncementRepository;
import com.lawbackend2.lawbackend2.service.impl.CaseAnnouncementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseAnnouncementServiceTest {

    @Mock
    private CaseAnnouncementRepository caseAnnouncementRepository;

    @InjectMocks
    private CaseAnnouncementServiceImpl caseAnnouncementService;

    private CaseAnnouncementCreateRequest createRequest;
    private CaseAnnouncementUpdateRequest updateRequest;
    private CaseAnnouncementPublishRequest publishRequest;
    private CaseAnnouncement mockAnnouncement;

    @BeforeEach
    void setUp() {
        createRequest = new CaseAnnouncementCreateRequest();
        createRequest.setCaseId(1L);
        createRequest.setTitle("测试公告");
        createRequest.setContent("这是测试公告内容");
        createRequest.setAnnouncementType("ANNOUNCEMENT");
        createRequest.setAttachments("附件1.pdf,附件2.pdf");

        updateRequest = new CaseAnnouncementUpdateRequest();
        updateRequest.setTitle("更新后的标题");
        updateRequest.setContent("更新后的内容");

        publishRequest = new CaseAnnouncementPublishRequest();
        publishRequest.setTopExpireTime(LocalDateTime.now().plusDays(7));

        mockAnnouncement = new CaseAnnouncement();
        mockAnnouncement.setId(1L);
        mockAnnouncement.setCaseId(1L);
        mockAnnouncement.setTitle("测试公告");
        mockAnnouncement.setContent("这是测试公告内容");
        mockAnnouncement.setAnnouncementType("ANNOUNCEMENT");
        mockAnnouncement.setStatus("DRAFT");
        mockAnnouncement.setViewCount(0);
        mockAnnouncement.setIsTop(false);
    }

    @Test
    void testCreateAnnouncement_Success() {
        when(caseAnnouncementRepository.save(any(CaseAnnouncement.class))).thenReturn(mockAnnouncement);

        CaseAnnouncement result = caseAnnouncementService.createAnnouncement(createRequest, 1L);

        assertNotNull(result);
        assertEquals("测试公告", result.getTitle());
        assertEquals("DRAFT", result.getStatus());
        verify(caseAnnouncementRepository, times(1)).save(any(CaseAnnouncement.class));
    }

    @Test
    void testGetAnnouncementById_Success() {
        when(caseAnnouncementRepository.findById(1L)).thenReturn(Optional.of(mockAnnouncement));

        CaseAnnouncement result = caseAnnouncementService.getAnnouncementById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试公告", result.getTitle());
        verify(caseAnnouncementRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAnnouncementById_NotFound() {
        when(caseAnnouncementRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseAnnouncementService.getAnnouncementById(999L);
        });

        assertEquals("案件公告不存在", exception.getMessage());
    }

    @Test
    void testGetAnnouncementList_WithCaseId() {
        List<CaseAnnouncement> announcements = Arrays.asList(mockAnnouncement);
        Page<CaseAnnouncement> page = new PageImpl<>(announcements);
        when(caseAnnouncementRepository.findByCaseId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        List<CaseAnnouncement> result = caseAnnouncementService.getAnnouncementList(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试公告", result.get(0).getTitle());
        verify(caseAnnouncementRepository, times(1)).findByCaseId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetAnnouncementList_WithStatus() {
        List<CaseAnnouncement> announcements = Arrays.asList(mockAnnouncement);
        Page<CaseAnnouncement> page = new PageImpl<>(announcements);
        when(caseAnnouncementRepository.findByStatus(eq("PUBLISHED"), any(PageRequest.class)))
                .thenReturn(page);

        List<CaseAnnouncement> result = caseAnnouncementService.getAnnouncementList(1, 10, null, "PUBLISHED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试公告", result.get(0).getTitle());
        verify(caseAnnouncementRepository, times(1)).findByStatus(eq("PUBLISHED"), any(PageRequest.class));
    }

    @Test
    void testUpdateAnnouncement_Success() {
        when(caseAnnouncementRepository.findById(1L)).thenReturn(Optional.of(mockAnnouncement));
        when(caseAnnouncementRepository.save(any(CaseAnnouncement.class))).thenReturn(mockAnnouncement);

        CaseAnnouncement result = caseAnnouncementService.updateAnnouncement(1L, updateRequest);

        assertNotNull(result);
        verify(caseAnnouncementRepository, times(1)).save(any(CaseAnnouncement.class));
    }

    @Test
    void testUpdateAnnouncement_NotFound() {
        when(caseAnnouncementRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseAnnouncementService.updateAnnouncement(999L, updateRequest);
        });

        assertEquals("案件公告不存在", exception.getMessage());
        verify(caseAnnouncementRepository, never()).save(any(CaseAnnouncement.class));
    }

    @Test
    void testPublishAnnouncement_Success() {
        mockAnnouncement.setStatus("DRAFT");
        when(caseAnnouncementRepository.findById(1L)).thenReturn(Optional.of(mockAnnouncement));
        when(caseAnnouncementRepository.save(any(CaseAnnouncement.class))).thenReturn(mockAnnouncement);

        caseAnnouncementService.publishAnnouncement(1L, publishRequest, 1L);

        assertEquals("PUBLISHED", mockAnnouncement.getStatus());
        assertEquals(1L, mockAnnouncement.getPublisherId());
        assertNotNull(mockAnnouncement.getPublishTime());
        assertTrue(mockAnnouncement.getIsTop());
        assertNotNull(mockAnnouncement.getTopExpireTime());
        verify(caseAnnouncementRepository, times(1)).save(any(CaseAnnouncement.class));
    }

    @Test
    void testPublishAnnouncement_NotDraftStatus() {
        mockAnnouncement.setStatus("PUBLISHED");
        when(caseAnnouncementRepository.findById(1L)).thenReturn(Optional.of(mockAnnouncement));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseAnnouncementService.publishAnnouncement(1L, publishRequest, 1L);
        });

        assertEquals("只有草稿状态的公告才能发布", exception.getMessage());
        verify(caseAnnouncementRepository, never()).save(any(CaseAnnouncement.class));
    }

    @Test
    void testTopAnnouncement_Success() {
        when(caseAnnouncementRepository.findById(1L)).thenReturn(Optional.of(mockAnnouncement));
        when(caseAnnouncementRepository.save(any(CaseAnnouncement.class))).thenReturn(mockAnnouncement);

        caseAnnouncementService.topAnnouncement(1L, publishRequest, 1L);

        assertTrue(mockAnnouncement.getIsTop());
        assertNotNull(mockAnnouncement.getTopExpireTime());
        verify(caseAnnouncementRepository, times(1)).save(any(CaseAnnouncement.class));
    }

    @Test
    void testTopAnnouncement_NotFound() {
        when(caseAnnouncementRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseAnnouncementService.topAnnouncement(999L, publishRequest, 1L);
        });

        assertEquals("案件公告不存在", exception.getMessage());
        verify(caseAnnouncementRepository, never()).save(any(CaseAnnouncement.class));
    }
}
