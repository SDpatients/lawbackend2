package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.AdministratorCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffUpdateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Administrator;
import com.lawbackend2.lawbackend2.entity.AdministratorStaff;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.AdministratorRepository;
import com.lawbackend2.lawbackend2.repository.AdministratorStaffRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.impl.AdministratorServiceImpl;
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
class AdministratorServiceTest {

    @Mock
    private AdministratorRepository administratorRepository;
    @Mock
    private AdministratorStaffRepository administratorStaffRepository;
    @Mock
    private BankruptCaseRepository bankruptCaseRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdministratorServiceImpl administratorService;

    private AdministratorCreateRequest createRequest;
    private AdministratorUpdateRequest updateRequest;
    private AdministratorStaffCreateRequest staffCreateRequest;
    private Administrator mockAdministrator;
    private AdministratorStaff mockStaff;

    @BeforeEach
    void setUp() {
        createRequest = new AdministratorCreateRequest();
        createRequest.setAdministratorName("某某律师事务所");
        createRequest.setCaseId(1L);
        createRequest.setAdministratorType("律师事务所");
        createRequest.setResponsiblePersonId(2L);
        createRequest.setContactPhone("13800138000");
        createRequest.setContactEmail("admin@law.com");
        createRequest.setOfficeAddress("北京市朝阳区建国路88号");

        updateRequest = new AdministratorUpdateRequest();
        updateRequest.setAdministratorName("某某律师事务所(更新)");
        updateRequest.setContactPhone("13900139000");
        updateRequest.setContactEmail("admin2@law.com");
        updateRequest.setOfficeAddress("北京市海淀区中关村大街1号");

        staffCreateRequest = new AdministratorStaffCreateRequest();
        staffCreateRequest.setAdministratorId(1L);
        staffCreateRequest.setName("张律师");
        staffCreateRequest.setStaffType("负责人");
        staffCreateRequest.setIdNumber("110101198001011234");
        staffCreateRequest.setLawyerLicenseNumber("110119850000001");
        staffCreateRequest.setContactPhone("13800138001");
        staffCreateRequest.setEmail("zhang@law.com");
        staffCreateRequest.setResponsibility("全面负责案件管理工作");
        staffCreateRequest.setAppointmentDate(java.time.LocalDate.now());
        staffCreateRequest.setUserId(1L);

        mockAdministrator = new Administrator();
        mockAdministrator.setId(1L);
        mockAdministrator.setAdministratorName("某某律师事务所");
        mockAdministrator.setCaseId(1L);
        mockAdministrator.setAdministratorType("律师事务所");
        mockAdministrator.setContactPhone("13800138000");
        mockAdministrator.setStatus("ACTIVE");

        mockStaff = new AdministratorStaff();
        mockStaff.setId(1L);
        mockStaff.setAdministratorId(1L);
        mockStaff.setName("张律师");
        mockStaff.setStaffType("负责人");
        mockStaff.setStatus("ACTIVE");
        mockStaff.setUserId(1L);
    }

    @Test
    void testCreateAdministrator_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(new com.lawbackend2.lawbackend2.entity.BankruptCase()));
        when(administratorStaffRepository.findById(2L)).thenReturn(Optional.of(mockStaff));
        when(administratorRepository.save(any(Administrator.class))).thenReturn(mockAdministrator);

        Administrator result = administratorService.createAdministrator(createRequest, 1L);

        assertNotNull(result);
        assertEquals("律师事务所", result.getAdministratorType());
        assertEquals(1L, result.getCaseId());
        assertEquals("某某律师事务所", result.getAdministratorName());
        verify(administratorRepository, times(1)).save(any(Administrator.class));
    }

    @Test
    void testCreateAdministrator_CaseNotFound() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.createAdministrator(createRequest, 1L);
        });

        assertEquals("案件不存在", exception.getMessage());
        verify(administratorRepository, never()).save(any(Administrator.class));
    }

    @Test
    void testCreateAdministrator_StaffNotFound() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(new com.lawbackend2.lawbackend2.entity.BankruptCase()));
        when(administratorStaffRepository.findById(2L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.createAdministrator(createRequest, 1L);
        });

        assertEquals("员工不存在", exception.getMessage());
        verify(administratorRepository, never()).save(any(Administrator.class));
    }

    @Test
    void testGetAdministratorById_Success() {
        when(administratorRepository.findById(1L)).thenReturn(Optional.of(mockAdministrator));

        Administrator result = administratorService.getAdministratorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("某某律师事务所", result.getAdministratorName());
        verify(administratorRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAdministratorById_NotFound() {
        when(administratorRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.getAdministratorById(999L);
        });

        assertEquals("管理人信息不存在", exception.getMessage());
    }

    @Test
    void testGetAdministratorList_WithCaseId() {
        List<Administrator> administrators = Arrays.asList(mockAdministrator);
            Page<Administrator> page = new PageImpl<>(administrators);
        when(administratorRepository.findByCaseId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        List<Administrator> result = administratorService.getAdministratorList(1, 10, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("某某律师事务所", result.get(0).getAdministratorName());
        verify(administratorRepository, times(1)).findByCaseId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetAdministratorList_All() {
        List<Administrator> administrators = Arrays.asList(mockAdministrator);
        Page<Administrator> page = new PageImpl<>(administrators);
        when(administratorRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        List<Administrator> result = administratorService.getAdministratorList(1, 10, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(administratorRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testUpdateAdministrator_Success() {
        when(administratorRepository.findById(1L)).thenReturn(Optional.of(mockAdministrator));
        when(administratorRepository.save(any(Administrator.class))).thenReturn(mockAdministrator);

        Administrator result = administratorService.updateAdministrator(1L, updateRequest);

        assertNotNull(result);
        verify(administratorRepository, times(1)).save(any(Administrator.class));
    }

    @Test
    void testUpdateAdministrator_NotFound() {
        when(administratorRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.updateAdministrator(999L, updateRequest);
        });

        assertEquals("管理人信息不存在", exception.getMessage());
        verify(administratorRepository, never()).save(any(Administrator.class));
    }

    @Test
    void testCreateAdministratorStaff_Success() {
        when(administratorRepository.findById(1L)).thenReturn(Optional.of(mockAdministrator));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(administratorStaffRepository.save(any(AdministratorStaff.class))).thenReturn(mockStaff);

        AdministratorStaff result = administratorService.createAdministratorStaff(staffCreateRequest, 1L);

        assertNotNull(result);
        assertEquals("张律师", result.getName());
        assertEquals("负责人", result.getStaffType());
        assertEquals(1L, result.getUserId());
        verify(administratorStaffRepository, times(1)).save(any(AdministratorStaff.class));
    }

    @Test
    void testCreateAdministratorStaff_UserNotFound() {
        when(administratorRepository.findById(1L)).thenReturn(Optional.of(mockAdministrator));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.createAdministratorStaff(staffCreateRequest, 1L);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(administratorStaffRepository, never()).save(any(AdministratorStaff.class));
    }

    @Test
    void testGetAdministratorStaffList_Success() {
        when(administratorStaffRepository.findByAdministratorId(1L))
                .thenReturn(Arrays.asList(mockStaff));

        List<AdministratorStaff> result = administratorService.getAdministratorStaffList(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张律师", result.get(0).getName());
        assertEquals(1L, result.get(0).getUserId());
        verify(administratorStaffRepository, times(1)).findByAdministratorId(1L);
    }

    @Test
    void testGetAdministratorStaffById_Success() {
        when(administratorStaffRepository.findById(1L)).thenReturn(Optional.of(mockStaff));

        AdministratorStaff result = administratorService.getAdministratorStaffById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("张律师", result.getName());
        assertEquals(1L, result.getUserId());
        verify(administratorStaffRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAdministratorStaffById_NotFound() {
        when(administratorStaffRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.getAdministratorStaffById(999L);
        });

        assertEquals("管理人员工信息不存在", exception.getMessage());
        verify(administratorStaffRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateAdministrator_WithAdministratorName() {
        when(administratorRepository.findById(1L)).thenReturn(Optional.of(mockAdministrator));
        when(administratorRepository.save(any(Administrator.class))).thenReturn(mockAdministrator);

        Administrator result = administratorService.updateAdministrator(1L, updateRequest);

        assertNotNull(result);
        verify(administratorRepository, times(1)).save(any(Administrator.class));
    }

    @Test
    void testUpdateAdministratorStaff_Success() {
        AdministratorStaffUpdateRequest updateRequest = new AdministratorStaffUpdateRequest();
        updateRequest.setName("李律师");
        updateRequest.setUserId(2L);

        when(administratorStaffRepository.findById(1L)).thenReturn(Optional.of(mockStaff));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(administratorStaffRepository.save(any(AdministratorStaff.class))).thenReturn(mockStaff);

        AdministratorStaff result = administratorService.updateAdministratorStaff(1L, updateRequest);

        assertNotNull(result);
        verify(administratorStaffRepository, times(1)).save(any(AdministratorStaff.class));
    }

    @Test
    void testUpdateAdministratorStaff_UserNotFound() {
        AdministratorStaffUpdateRequest updateRequest = new AdministratorStaffUpdateRequest();
        updateRequest.setUserId(999L);

        when(administratorStaffRepository.findById(1L)).thenReturn(Optional.of(mockStaff));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.updateAdministratorStaff(1L, updateRequest);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(administratorStaffRepository, never()).save(any(AdministratorStaff.class));
    }

    @Test
    void testDeleteAdministratorStaff_Success() {
        when(administratorStaffRepository.existsById(1L)).thenReturn(true);
        doNothing().when(administratorStaffRepository).deleteById(1L);

        administratorService.deleteAdministratorStaff(1L);

        verify(administratorStaffRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAdministratorStaff_NotFound() {
        when(administratorStaffRepository.existsById(999L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            administratorService.deleteAdministratorStaff(999L);
        });

        assertEquals("管理人员工信息不存在", exception.getMessage());
        verify(administratorStaffRepository, never()).deleteById(any());
    }
}
