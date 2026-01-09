package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CourtCreateRequest;
import com.lawbackend2.lawbackend2.dto.CourtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Court;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CourtRepository;
import com.lawbackend2.lawbackend2.service.impl.CourtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtServiceImpl courtService;

    private CourtCreateRequest createRequest;
    private CourtUpdateRequest updateRequest;
    private Court mockCourt;

    @BeforeEach
    void setUp() {
        createRequest = new CourtCreateRequest();
        createRequest.setFullName("北京市第一中级人民法院");
        createRequest.setShortName("北京一中院");
        createRequest.setCourtLevel("中级人民法院");
        createRequest.setContactPhone("010-12345678");
        createRequest.setUndertakingJudge("张法官");

        updateRequest = new CourtUpdateRequest();
        updateRequest.setFullName("更新后的法院全称");
        updateRequest.setContactPhone("010-87654321");

        mockCourt = new Court();
        mockCourt.setId(1L);
        mockCourt.setFullName("北京市第一中级人民法院");
        mockCourt.setShortName("北京一中院");
        mockCourt.setCourtLevel("中级人民法院");
        mockCourt.setStatus("ACTIVE");
    }

    @Test
    void testCreateCourt_Success() {
        when(courtRepository.findByShortName("北京一中院")).thenReturn(Optional.empty());
        when(courtRepository.save(any(Court.class))).thenReturn(mockCourt);

        Court result = courtService.createCourt(createRequest, 1L);

        assertNotNull(result);
        assertEquals("北京市第一中级人民法院", result.getFullName());
        assertEquals("北京一中院", result.getShortName());
        verify(courtRepository, times(1)).save(any(Court.class));
    }

    @Test
    void testCreateCourt_DuplicateShortName() {
        when(courtRepository.findByShortName("北京一中院")).thenReturn(Optional.of(mockCourt));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            courtService.createCourt(createRequest, 1L);
        });

        assertEquals("法院简称已存在", exception.getMessage());
        verify(courtRepository, never()).save(any(Court.class));
    }

    @Test
    void testGetCourtById_Success() {
        when(courtRepository.findById(1L)).thenReturn(Optional.of(mockCourt));

        Court result = courtService.getCourtById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("北京市第一中级人民法院", result.getFullName());
        verify(courtRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCourtById_NotFound() {
        when(courtRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            courtService.getCourtById(999L);
        });

        assertEquals("法院信息不存在", exception.getMessage());
    }

    @Test
    void testGetCourtList_WithCourtLevel() {
        List<Court> courts = Arrays.asList(mockCourt);
        Page<Court> page = new PageImpl<>(courts);
        when(courtRepository.findByCourtLevel(eq("中级人民法院"), any(PageRequest.class)))
                .thenReturn(page);

        List<Court> result = courtService.getCourtList(1, 10, "中级人民法院", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("北京市第一中级人民法院", result.get(0).getFullName());
        verify(courtRepository, times(1)).findByCourtLevel(eq("中级人民法院"), any(PageRequest.class));
    }

    @Test
    void testGetCourtList_WithShortName() {
        List<Court> courts = Arrays.asList(mockCourt);
        Page<Court> page = new PageImpl<>(courts);
        when(courtRepository.findByShortNameContaining(eq("北京"), any(PageRequest.class)))
                .thenReturn(page);

        List<Court> result = courtService.getCourtList(1, 10, null, "北京");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("北京市第一中级人民法院", result.get(0).getFullName());
        verify(courtRepository, times(1)).findByShortNameContaining(eq("北京"), any(PageRequest.class));
    }

    @Test
    void testUpdateCourt_Success() {
        when(courtRepository.findById(1L)).thenReturn(Optional.of(mockCourt));
        when(courtRepository.save(any(Court.class))).thenReturn(mockCourt);

        Court result = courtService.updateCourt(1L, updateRequest);

        assertNotNull(result);
        verify(courtRepository, times(1)).save(any(Court.class));
    }

    @Test
    void testUpdateCourt_NotFound() {
        when(courtRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            courtService.updateCourt(999L, updateRequest);
        });

        assertEquals("法院信息不存在", exception.getMessage());
        verify(courtRepository, never()).save(any(Court.class));
    }
}
