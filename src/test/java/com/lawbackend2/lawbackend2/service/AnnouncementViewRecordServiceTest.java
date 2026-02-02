package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.AnnouncementViewRecordCreateRequest;
import com.lawbackend2.lawbackend2.entity.AnnouncementViewRecord;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.AnnouncementViewRecordRepository;
import com.lawbackend2.lawbackend2.repository.CaseAnnouncementRepository;
import com.lawbackend2.lawbackend2.service.impl.AnnouncementViewRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementViewRecordServiceTest {

    @Mock
    private AnnouncementViewRecordRepository viewRecordRepository;

    @Mock
    private CaseAnnouncementRepository announcementRepository;

    @InjectMocks
    private AnnouncementViewRecordServiceImpl viewRecordService;

    private AnnouncementViewRecordCreateRequest createRequest;
    private CaseAnnouncement mockAnnouncement;
    private AnnouncementViewRecord mockRecord;

    @BeforeEach
    void setUp() {
        createRequest = new AnnouncementViewRecordCreateRequest();
        createRequest.setAnnouncementId(1L);
        createRequest.setViewerId(1L);
        createRequest.setViewerName("测试用户");
        createRequest.setViewerType("CREDITOR");
        createRequest.setIpAddress("192.168.1.1");
        createRequest.setUserAgent("Mozilla/5.0");
        createRequest.setViewDuration(120);
        createRequest.setDeviceType("PC");
        createRequest.setBrowserType("Chrome");
        createRequest.setOsType("Windows");
        createRequest.setLocation("北京");

        mockAnnouncement = new CaseAnnouncement();
        mockAnnouncement.setId(1L);
        mockAnnouncement.setTitle("测试公告");
        mockAnnouncement.setCaseId(1L);
        mockAnnouncement.setViewCount(0);

        mockRecord = new AnnouncementViewRecord();
        mockRecord.setId(1L);
        mockRecord.setAnnouncementId(1L);
        mockRecord.setAnnouncementTitle("测试公告");
        mockRecord.setCaseId(1L);
        mockRecord.setCaseName("测试案件");
        mockRecord.setViewerId(1L);
        mockRecord.setViewerName("测试用户");
        mockRecord.setViewerType("CREDITOR");
        mockRecord.setViewTime(LocalDateTime.now());
        mockRecord.setIpAddress("192.168.1.1");
        mockRecord.setUserAgent("Mozilla/5.0");
        mockRecord.setViewDuration(120);
        mockRecord.setDeviceType("PC");
        mockRecord.setBrowserType("Chrome");
        mockRecord.setOsType("Windows");
        mockRecord.setLocation("北京");
    }

    @Test
    void testCreateViewRecord_Success() {
        when(announcementRepository.findById(1L)).thenReturn(Optional.of(mockAnnouncement));
        when(viewRecordRepository.save(any(AnnouncementViewRecord.class))).thenReturn(mockRecord);

        AnnouncementViewRecord result = viewRecordService.createViewRecord(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getAnnouncementId());
        assertEquals("测试用户", result.getViewerName());
        verify(viewRecordRepository, times(1)).save(any(AnnouncementViewRecord.class));
        verify(announcementRepository, times(1)).save(any(CaseAnnouncement.class));
    }

    @Test
    void testCreateViewRecord_AnnouncementNotFound() {
        when(announcementRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setAnnouncementId(999L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            viewRecordService.createViewRecord(createRequest);
        });

        assertEquals("公告不存在", exception.getMessage());
        verify(viewRecordRepository, never()).save(any(AnnouncementViewRecord.class));
    }

    @Test
    void testGetViewRecordById_Success() {
        when(viewRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));

        AnnouncementViewRecord result = viewRecordService.getViewRecordById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试用户", result.getViewerName());
        verify(viewRecordRepository, times(1)).findById(1L);
    }

    @Test
    void testGetViewRecordById_NotFound() {
        when(viewRecordRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            viewRecordService.getViewRecordById(999L);
        });

        assertEquals("公告查看记录不存在", exception.getMessage());
    }

    @Test
    void testGetViewRecordList_WithAnnouncementId() {
        when(viewRecordRepository.findByAnnouncementId(eq(1L), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        List<AnnouncementViewRecord> result = viewRecordService.getViewRecordList(1, 10, 1L, null, null);

        assertNotNull(result);
        verify(viewRecordRepository, times(1)).findByAnnouncementId(eq(1L), any());
    }

    @Test
    void testGetViewRecordList_WithCaseId() {
        when(viewRecordRepository.findByCaseId(eq(1L), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        List<AnnouncementViewRecord> result = viewRecordService.getViewRecordList(1, 10, null, 1L, null);

        assertNotNull(result);
        verify(viewRecordRepository, times(1)).findByCaseId(eq(1L), any());
    }

    @Test
    void testGetViewRecordList_WithViewerId() {
        when(viewRecordRepository.findByViewerId(eq(1L), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        List<AnnouncementViewRecord> result = viewRecordService.getViewRecordList(1, 10, null, null, 1L);

        assertNotNull(result);
        verify(viewRecordRepository, times(1)).findByViewerId(eq(1L), any());
    }

    @Test
    void testGetViewCountByAnnouncementId_Success() {
        when(viewRecordRepository.countByAnnouncementId(1L)).thenReturn(100L);

        Long count = viewRecordService.getViewCountByAnnouncementId(1L);

        assertEquals(100L, count);
        verify(viewRecordRepository, times(1)).countByAnnouncementId(1L);
    }

    @Test
    void testGetViewCountByCaseId_Success() {
        when(viewRecordRepository.countByCaseId(1L)).thenReturn(200L);

        Long count = viewRecordService.getViewCountByCaseId(1L);

        assertEquals(200L, count);
        verify(viewRecordRepository, times(1)).countByCaseId(1L);
    }

    @Test
    void testGetViewCountByViewerId_Success() {
        when(viewRecordRepository.countByViewerId(1L)).thenReturn(50L);

        Long count = viewRecordService.getViewCountByViewerId(1L);

        assertEquals(50L, count);
        verify(viewRecordRepository, times(1)).countByViewerId(1L);
    }

    @Test
    void testDeleteViewRecord_Success() {
        when(viewRecordRepository.existsById(1L)).thenReturn(true);

        viewRecordService.deleteViewRecord(1L);

        verify(viewRecordRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteViewRecord_NotFound() {
        when(viewRecordRepository.existsById(999L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            viewRecordService.deleteViewRecord(999L);
        });

        assertEquals("公告查看记录不存在", exception.getMessage());
        verify(viewRecordRepository, never()).deleteById(999L);
    }
}
