package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.DebtorCreateRequest;
import com.lawbackend2.lawbackend2.dto.DebtorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.DebtorEnterprise;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.DebtorEnterpriseRepository;
import com.lawbackend2.lawbackend2.service.impl.DebtorEnterpriseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtorEnterpriseServiceTest {

    @Mock
    private DebtorEnterpriseRepository debtorEnterpriseRepository;

    @Mock
    private BankruptCaseRepository bankruptCaseRepository;

    @InjectMocks
    private DebtorEnterpriseServiceImpl debtorEnterpriseService;

    private DebtorCreateRequest createRequest;
    private DebtorUpdateRequest updateRequest;
    private DebtorEnterprise mockDebtor;
    private BankruptCase mockCase;

    @BeforeEach
    void setUp() {
        createRequest = new DebtorCreateRequest();
        createRequest.setCaseId(1L);
        createRequest.setEnterpriseName("测试企业");
        createRequest.setUnifiedSocialCreditCode("91110000MA01234567");
        createRequest.setLegalRepresentative("张三");
        createRequest.setContactPhone("13800138000");
        createRequest.setContactPerson("李四");
        createRequest.setEstablishmentDate(LocalDate.now());
        createRequest.setRegisteredCapital(new BigDecimal("1000000.00"));
        createRequest.setBusinessScope("技术开发");
        createRequest.setEnterpriseType("有限责任公司");
        createRequest.setIndustry("软件和信息技术服务业");
        createRequest.setRegisteredAddress("北京市朝阳区");

        updateRequest = new DebtorUpdateRequest();
        updateRequest.setEnterpriseName("更新后的企业名称");
        updateRequest.setContactPhone("13900139000");

        mockDebtor = new DebtorEnterprise();
        mockDebtor.setId(1L);
        mockDebtor.setEnterpriseName("测试企业");
        mockDebtor.setUnifiedSocialCreditCode("91110000MA01234567");
        mockDebtor.setLegalRepresentative("张三");

        mockCase = new BankruptCase();
        mockCase.setId(1L);
        mockCase.setCaseNumber("TEST001");
        mockCase.setCaseName("测试案件");
    }

    @Test
    void testCreateDebtor_Success() {
        when(debtorEnterpriseRepository.findByUnifiedSocialCreditCode("91110000MA01234567"))
                .thenReturn(Optional.empty());
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(debtorEnterpriseRepository.save(any(DebtorEnterprise.class))).thenReturn(mockDebtor);

        DebtorEnterprise result = debtorEnterpriseService.createDebtor(createRequest, 1L);

        assertNotNull(result);
        assertEquals("测试企业", result.getEnterpriseName());
        assertEquals("91110000MA01234567", result.getUnifiedSocialCreditCode());
        verify(debtorEnterpriseRepository, times(1)).save(any(DebtorEnterprise.class));
        verify(bankruptCaseRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDebtor_DuplicateCreditCode() {
        when(debtorEnterpriseRepository.findByUnifiedSocialCreditCode("91110000MA01234567"))
                .thenReturn(Optional.of(mockDebtor));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            debtorEnterpriseService.createDebtor(createRequest, 1L);
        });

        assertEquals("统一社会信用代码已存在", exception.getMessage());
        verify(debtorEnterpriseRepository, never()).save(any(DebtorEnterprise.class));
    }

    @Test
    void testCreateDebtor_CaseNotFound() {
        when(debtorEnterpriseRepository.findByUnifiedSocialCreditCode("91110000MA01234567"))
                .thenReturn(Optional.empty());
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            debtorEnterpriseService.createDebtor(createRequest, 1L);
        });

        assertEquals("案件不存在", exception.getMessage());
        verify(debtorEnterpriseRepository, never()).save(any(DebtorEnterprise.class));
    }

    @Test
    void testGetDebtorById_Success() {
        when(debtorEnterpriseRepository.findById(1L)).thenReturn(Optional.of(mockDebtor));

        DebtorEnterprise result = debtorEnterpriseService.getDebtorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试企业", result.getEnterpriseName());
        verify(debtorEnterpriseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDebtorById_NotFound() {
        when(debtorEnterpriseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            debtorEnterpriseService.getDebtorById(999L);
        });

        assertEquals("债务人信息不存在", exception.getMessage());
    }

    @Test
    void testGetDebtorList_WithCaseId() {
        List<DebtorEnterprise> debtors = Arrays.asList(mockDebtor);
        Page<DebtorEnterprise> page = new PageImpl<>(debtors);
        when(debtorEnterpriseRepository.findByCaseId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        List<DebtorEnterprise> result = debtorEnterpriseService.getDebtorList(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试债务人企业", result.get(0).getEnterpriseName());
        verify(debtorEnterpriseRepository, times(1)).findByCaseId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetDebtorList_WithEnterpriseName() {
        List<DebtorEnterprise> debtors = Arrays.asList(mockDebtor);
        Page<DebtorEnterprise> page = new PageImpl<>(debtors);
        when(debtorEnterpriseRepository.findByConditions(anyLong(), anyString(), any(PageRequest.class)))
                .thenReturn(page);

        List<DebtorEnterprise> result = debtorEnterpriseService.getDebtorList(1, 10, 1L, "测试");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(debtorEnterpriseRepository, times(1)).findByConditions(anyLong(), anyString(), any(PageRequest.class));
    }

    @Test
    void testUpdateDebtor_Success() {
        when(debtorEnterpriseRepository.findById(1L)).thenReturn(Optional.of(mockDebtor));
        when(debtorEnterpriseRepository.save(any(DebtorEnterprise.class))).thenReturn(mockDebtor);

        DebtorEnterprise result = debtorEnterpriseService.updateDebtor(1L, updateRequest);

        assertNotNull(result);
        verify(debtorEnterpriseRepository, times(1)).save(any(DebtorEnterprise.class));
    }

    @Test
    void testUpdateDebtor_NotFound() {
        when(debtorEnterpriseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            debtorEnterpriseService.updateDebtor(999L, updateRequest);
        });

        assertEquals("债务人信息不存在", exception.getMessage());
        verify(debtorEnterpriseRepository, never()).save(any(DebtorEnterprise.class));
    }
}
