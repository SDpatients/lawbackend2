package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApproveRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.DocumentDeliveryResponse;
import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.DocumentDelivery;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.DocumentDeliveryRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.impl.DocumentDeliveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentDeliveryServiceTest {

    @Mock
    private DocumentDeliveryRepository documentDeliveryRepository;

    @Mock
    private BankruptCaseRepository bankruptCaseRepository;

    @Mock
    private FileService fileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApprovalRepository approvalRepository;

    @InjectMocks
    private DocumentDeliveryServiceImpl documentDeliveryService;

    private DocumentDelivery mockDocumentDelivery;
    private BankruptCase mockBankruptCase;
    private User mockUser;
    private Approval mockApproval;

    @BeforeEach
    void setUp() {
        mockDocumentDelivery = new DocumentDelivery();
        mockDocumentDelivery.setId(1L);
        mockDocumentDelivery.setCaseId(123L);
        mockDocumentDelivery.setCaseNumber("CASE001");
        mockDocumentDelivery.setCaseName("测试案件");
        mockDocumentDelivery.setDocumentName("测试文书");
        mockDocumentDelivery.setDocumentType("COURT_NOTICE");
        mockDocumentDelivery.setRecipientName("张三");
        mockDocumentDelivery.setRecipientType("DEBTOR");
        mockDocumentDelivery.setContactPhone("13800138000");
        mockDocumentDelivery.setDeliveryAddress("北京市");
        mockDocumentDelivery.setDeliveryMethod("POST");
        mockDocumentDelivery.setSendStatus("PENDING");
        mockDocumentDelivery.setStatus("PENDING");
        mockDocumentDelivery.setCreateTime(LocalDateTime.now());
        mockDocumentDelivery.setUpdateTime(LocalDateTime.now());

        mockBankruptCase = new BankruptCase();
        mockBankruptCase.setId(123L);
        mockBankruptCase.setCaseNumber("CASE001");
        mockBankruptCase.setCaseName("测试案件");

        mockUser = new User();
        mockUser.setId(456L);
        mockUser.setUsername("testuser");
        mockUser.setRealName("测试用户");

        mockApproval = new Approval();
        mockApproval.setId(1L);
        mockApproval.setCaseId(123L);
        mockApproval.setLawyerId(456L);
        mockApproval.setApprovalType("DOCUMENT_DELIVERY");
        mockApproval.setApprovalStatus("PENDING");
        mockApproval.setApprovalCount(0);
        mockApproval.setStatus("ACTIVE");
        mockApproval.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testCreateDocumentDelivery_Success() {
        DocumentDeliveryCreateRequest request = new DocumentDeliveryCreateRequest();
        request.setCaseId(123L);
        request.setDocumentName("测试文书");
        request.setDocumentType("COURT_NOTICE");
        request.setRecipientName("张三");
        request.setRecipientType("DEBTOR");
        request.setContactPhone("13800138000");
        request.setDeliveryAddress("北京市");
        request.setDeliveryMethod("POST");

        when(bankruptCaseRepository.findById(123L)).thenReturn(Optional.of(mockBankruptCase));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        Long result = documentDeliveryService.createDocumentDelivery(request);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testCreateDocumentDeliveryDirect_Success() {
        DocumentDeliveryCreateRequest request = new DocumentDeliveryCreateRequest();
        request.setCaseId(123L);
        request.setDocumentName("测试文书");
        request.setDocumentType("COURT_NOTICE");
        request.setRecipientName("张三");
        request.setRecipientType("DEBTOR");
        request.setContactPhone("13800138000");
        request.setDeliveryAddress("北京市");
        request.setDeliveryMethod("POST");

        when(bankruptCaseRepository.findById(123L)).thenReturn(Optional.of(mockBankruptCase));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        Long result = documentDeliveryService.createDocumentDeliveryDirect(request);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testCreateDocumentDeliveryForApproval_Success() {
        DocumentDeliveryApprovalRequest request = new DocumentDeliveryApprovalRequest();
        request.setCaseId(123L);
        request.setDocumentName("测试文书");
        request.setDocumentType("COURT_NOTICE");
        request.setRecipientName("张三");
        request.setRecipientType("DEBTOR");
        request.setContactPhone("13800138000");
        request.setDeliveryAddress("北京市");
        request.setDeliveryMethod("POST");
        request.setApprovalTitle("文书审批");
        request.setApprovalContent("请审批");
        request.setRemark("备注");

        when(bankruptCaseRepository.findById(123L)).thenReturn(Optional.of(mockBankruptCase));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);

        Long result = documentDeliveryService.createDocumentDeliveryForApproval(request);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }

    @Test
    void testApproveDocumentDelivery_Success_Pass() {
        DocumentDeliveryApproveRequest request = new DocumentDeliveryApproveRequest();
        request.setDeliveryId(1L);
        request.setApprovalResult("PASS");
        request.setRemark("审批通过");

        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(approvalRepository.findByCaseIdAndApprovalType(123L, "DOCUMENT_DELIVERY"))
                .thenReturn(Optional.of(mockApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.approveDocumentDelivery(request);

        assertEquals("APPROVED", mockDocumentDelivery.getStatus());
        assertEquals("APPROVED", mockApproval.getApprovalStatus());
        assertEquals("PASS", mockApproval.getApprovalResult());
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testApproveDocumentDelivery_Success_Fail() {
        DocumentDeliveryApproveRequest request = new DocumentDeliveryApproveRequest();
        request.setDeliveryId(1L);
        request.setApprovalResult("FAIL");
        request.setRemark("审批不通过");

        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(approvalRepository.findByCaseIdAndApprovalType(123L, "DOCUMENT_DELIVERY"))
                .thenReturn(Optional.of(mockApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.approveDocumentDelivery(request);

        assertEquals("REJECTED", mockDocumentDelivery.getStatus());
        assertEquals("REJECTED", mockApproval.getApprovalStatus());
        assertEquals("FAIL", mockApproval.getApprovalResult());
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testApproveDocumentDelivery_AlreadyApproved() {
        mockDocumentDelivery.setStatus("APPROVED");
        DocumentDeliveryApproveRequest request = new DocumentDeliveryApproveRequest();
        request.setDeliveryId(1L);
        request.setApprovalResult("PASS");
        request.setRemark("审批通过");

        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            documentDeliveryService.approveDocumentDelivery(request);
        });

        assertTrue(exception.getMessage().contains("该文书送达已处理，无法重复审批"));
        verify(approvalRepository, never()).save(any(Approval.class));
        verify(documentDeliveryRepository, never()).save(any(DocumentDelivery.class));
    }

    @Test
    void testApproveDocumentDelivery_NotFound() {
        DocumentDeliveryApproveRequest request = new DocumentDeliveryApproveRequest();
        request.setDeliveryId(999L);
        request.setApprovalResult("PASS");
        request.setRemark("审批通过");

        when(documentDeliveryRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            documentDeliveryService.approveDocumentDelivery(request);
        });

        assertTrue(exception.getMessage().contains("文书送达记录不存在"));
        verify(approvalRepository, never()).save(any(Approval.class));
        verify(documentDeliveryRepository, never()).save(any(DocumentDelivery.class));
    }

    @Test
    void testGetDocumentDeliveryList_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocumentDelivery> page = new PageImpl<>(Arrays.asList(mockDocumentDelivery), pageable, 1);

        when(documentDeliveryRepository.findByConditions(anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        PageResult<DocumentDelivery> result = documentDeliveryService.getDocumentDeliveryList(1, 10, 123L, "CASE001",
                "COURT_NOTICE", "DEBTOR", "POST", "PENDING", "PENDING");

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        verify(documentDeliveryRepository, times(1)).findByConditions(anyLong(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void testGetDocumentDeliveryDetail_Success() {
        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));

        DocumentDelivery result = documentDeliveryService.getDocumentDeliveryDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试文书", result.getDocumentName());
        verify(documentDeliveryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDocumentDeliveryDetail_NotFound() {
        when(documentDeliveryRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            documentDeliveryService.getDocumentDeliveryDetail(999L);
        });

        assertTrue(exception.getMessage().contains("文书送达记录不存在"));
        verify(documentDeliveryRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateDocumentDelivery_Success() {
        DocumentDeliveryUpdateRequest request = new DocumentDeliveryUpdateRequest();
        request.setDocumentName("更新后的文书名称");
        request.setRecipientName("李四");
        request.setContactPhone("13900139000");

        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.updateDocumentDelivery(1L, request);

        assertEquals("更新后的文书名称", mockDocumentDelivery.getDocumentName());
        assertEquals("李四", mockDocumentDelivery.getRecipientName());
        assertEquals("13900139000", mockDocumentDelivery.getContactPhone());
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testDeleteDocumentDelivery_Success() {
        when(documentDeliveryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(documentDeliveryRepository).deleteById(1L);

        documentDeliveryService.deleteDocumentDelivery(1L);

        verify(documentDeliveryRepository, times(1)).existsById(1L);
        verify(documentDeliveryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDocumentDelivery_NotFound() {
        when(documentDeliveryRepository.existsById(999L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            documentDeliveryService.deleteDocumentDelivery(999L);
        });

        assertTrue(exception.getMessage().contains("文书送达记录不存在"));
        verify(documentDeliveryRepository, times(1)).existsById(999L);
        verify(documentDeliveryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateSendStatus_Success() {
        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.updateSendStatus(1L, "SENT", null);

        assertEquals("SENT", mockDocumentDelivery.getSendStatus());
        assertNotNull(mockDocumentDelivery.getSendTime());
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testUpdateSendStatus_Failed() {
        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.updateSendStatus(1L, "FAILED", "地址错误");

        assertEquals("FAILED", mockDocumentDelivery.getSendStatus());
        assertEquals("地址错误", mockDocumentDelivery.getFailureReason());
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_Success() {
        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.updateDeliveryStatus(1L, "DELIVERED");

        assertEquals("DELIVERED", mockDocumentDelivery.getSendStatus());
        assertNotNull(mockDocumentDelivery.getDeliveryTime());
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testUpdateStatusAndRemark_Success() {
        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(documentDeliveryRepository.save(any(DocumentDelivery.class))).thenReturn(mockDocumentDelivery);

        documentDeliveryService.updateStatusAndRemark(1L, "APPROVED", "审批通过");

        assertEquals("APPROVED", mockDocumentDelivery.getStatus());
        assertEquals("审批通过", mockDocumentDelivery.getRemark());
        verify(documentDeliveryRepository, times(1)).save(any(DocumentDelivery.class));
    }

    @Test
    void testGetDocumentDeliveryAttachments_Success() {
        FileRecord mockFileRecord = new FileRecord();
        mockFileRecord.setId(1L);
        mockFileRecord.setOriginalFileName("test.pdf");

        when(documentDeliveryRepository.findById(1L)).thenReturn(Optional.of(mockDocumentDelivery));
        when(fileService.getFileList(eq(1), eq(100), eq("DOCUMENT_DELIVERY"), eq("1"), isNull()))
                .thenReturn(new PageResult<>(1L, Arrays.asList(mockFileRecord)));

        List<FileRecord> result = documentDeliveryService.getDocumentDeliveryAttachments(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test.pdf", result.get(0).getOriginalFileName());
        verify(fileService, times(1)).getFileList(eq(1), eq(100), eq("DOCUMENT_DELIVERY"), eq("1"), isNull());
    }
}
