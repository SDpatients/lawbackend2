package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementItemCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursement;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursementItem;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.BankAccountTransactionRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementAttachmentRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementItemRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.BankAccountTransactionService;
import com.lawbackend2.lawbackend2.service.impl.ExpenseReimbursementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ExpenseReimbursementServiceTest {

    @Mock
    private ExpenseReimbursementRepository expenseReimbursementRepository;

    @Mock
    private ExpenseReimbursementItemRepository expenseReimbursementItemRepository;

    @Mock
    private ExpenseReimbursementAttachmentRepository expenseReimbursementAttachmentRepository;

    @Mock
    private BankruptCaseRepository bankruptCaseRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountTransactionRepository bankAccountTransactionRepository;

    @Mock
    private BankAccountTransactionService bankAccountTransactionService;

    @InjectMocks
    private ExpenseReimbursementServiceImpl expenseReimbursementService;

    private BankruptCase mockCase;
    private BankAccount mockBankAccount;
    private User mockUser;
    private ExpenseReimbursementCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        mockCase = new BankruptCase();
        mockCase.setId(1L);
        mockCase.setCaseName("测试案件");

        mockBankAccount = new BankAccount();
        mockBankAccount.setId(1L);
        mockBankAccount.setAccountName("测试账户");
        mockBankAccount.setBankName("中国银行");
        mockBankAccount.setAccountNumber("1234567890");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRealName("张律师");

        createRequest = new ExpenseReimbursementCreateRequest();
        createRequest.setCaseId(1L);
        createRequest.setFundAccountId(1L);
        createRequest.setReimbursementDate(LocalDate.now());
        createRequest.setDescription("测试报销");

        ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest item1 = 
            new ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest();
        item1.setItemName("交通费");
        item1.setItemAmount(new BigDecimal("500.00"));
        item1.setItemDescription("高铁票");

        ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest item2 = 
            new ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest();
        item2.setItemName("住宿费");
        item2.setItemAmount(new BigDecimal("800.00"));
        item2.setItemDescription("酒店");

        createRequest.setItems(Arrays.asList(item1, item2));
    }

    @Test
    void testCreateExpenseReimbursement_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(mockBankAccount));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(expenseReimbursementRepository.countByReimbursementNumberPrefix(anyString())).thenReturn(0L);

        ExpenseReimbursement savedReimbursement = new ExpenseReimbursement();
        savedReimbursement.setId(1L);
        when(expenseReimbursementRepository.save(any(ExpenseReimbursement.class))).thenReturn(savedReimbursement);
        when(expenseReimbursementItemRepository.save(any(ExpenseReimbursementItem.class))).thenReturn(new ExpenseReimbursementItem());

        Long result = expenseReimbursementService.createExpenseReimbursement(createRequest, 1L);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(expenseReimbursementRepository, times(1)).save(any(ExpenseReimbursement.class));
        verify(expenseReimbursementItemRepository, times(2)).save(any(ExpenseReimbursementItem.class));
    }

    @Test
    void testCreateExpenseReimbursement_CaseNotFound() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.createExpenseReimbursement(createRequest, 1L);
        });

        assertEquals("案件不存在", exception.getMessage());
        verify(expenseReimbursementRepository, never()).save(any(ExpenseReimbursement.class));
    }

    @Test
    void testCreateExpenseReimbursement_BankAccountNotFound() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.createExpenseReimbursement(createRequest, 1L);
        });

        assertEquals("银行账户不存在", exception.getMessage());
        verify(expenseReimbursementRepository, never()).save(any(ExpenseReimbursement.class));
    }

    @Test
    void testGetExpenseReimbursementDetail_Success() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setReimbursementNumber("BX202601240001");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));
        when(expenseReimbursementItemRepository.findByReimbursementId(1L)).thenReturn(Arrays.asList());
        when(expenseReimbursementAttachmentRepository.findByReimbursementId(1L)).thenReturn(Arrays.asList());

        ExpenseReimbursementResponse result = expenseReimbursementService.getExpenseReimbursementDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("BX202601240001", result.getReimbursementNumber());
        verify(expenseReimbursementRepository, times(1)).findById(1L);
    }

    @Test
    void testGetExpenseReimbursementDetail_NotFound() {
        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.getExpenseReimbursementDetail(1L);
        });

        assertEquals("报销单不存在", exception.getMessage());
    }

    @Test
    void testApproveExpenseReimbursement_Success() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("PENDING");
        mockReimbursement.setFundAccountId(1L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setAccountName("测试账户");
        bankAccount.setBankName("测试银行");
        bankAccount.setAccountNumber("1234567890");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(expenseReimbursementRepository.save(any(ExpenseReimbursement.class))).thenReturn(mockReimbursement);

        ExpenseReimbursementApprovalRequest request = new ExpenseReimbursementApprovalRequest();
        request.setApprovalStatus("APPROVED");
        request.setApprovalOpinion("同意");

        expenseReimbursementService.approveExpenseReimbursement(1L, request, 100L);

        verify(expenseReimbursementRepository, times(1)).save(any(ExpenseReimbursement.class));
        verify(bankAccountTransactionRepository, times(1)).save(any(BankAccountTransaction.class));
    }

    @Test
    void testApproveExpenseReimbursement_AlreadyApproved() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("APPROVED");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));

        ExpenseReimbursementApprovalRequest request = new ExpenseReimbursementApprovalRequest();
        request.setApprovalStatus("APPROVED");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.approveExpenseReimbursement(1L, request, 100L);
        });

        assertEquals("报销单已审批", exception.getMessage());
        verify(expenseReimbursementRepository, never()).save(any(ExpenseReimbursement.class));
        verify(bankAccountTransactionRepository, never()).save(any(BankAccountTransaction.class));
    }

    @Test
    void testDeleteExpenseReimbursement_Success() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("PENDING");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));
        doNothing().when(expenseReimbursementRepository).delete(any(ExpenseReimbursement.class));

        expenseReimbursementService.deleteExpenseReimbursement(1L);

        verify(expenseReimbursementRepository, times(1)).delete(any(ExpenseReimbursement.class));
    }

    @Test
    void testDeleteExpenseReimbursement_AlreadyApproved() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("APPROVED");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.deleteExpenseReimbursement(1L);
        });

        assertEquals("报销单已审批，不能删除", exception.getMessage());
        verify(expenseReimbursementRepository, never()).delete(any(ExpenseReimbursement.class));
    }

    @Test
    void testAddExpenseReimbursementItem_Success() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("PENDING");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));
        when(expenseReimbursementItemRepository.findByReimbursementId(1L)).thenReturn(Arrays.asList());

        ExpenseReimbursementItem savedItem = new ExpenseReimbursementItem();
        savedItem.setId(1L);
        when(expenseReimbursementItemRepository.save(any(ExpenseReimbursementItem.class))).thenReturn(savedItem);

        ExpenseReimbursementItemCreateRequest request = new ExpenseReimbursementItemCreateRequest();
        request.setReimbursementId(1L);
        request.setItemName("餐饮费");
        request.setItemAmount(new BigDecimal("200.00"));
        request.setItemDescription("工作餐");

        Long result = expenseReimbursementService.addExpenseReimbursementItem(request);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(expenseReimbursementItemRepository, times(1)).save(any(ExpenseReimbursementItem.class));
    }

    @Test
    void testAddExpenseReimbursementItem_AlreadyApproved() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("APPROVED");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));

        ExpenseReimbursementItemCreateRequest request = new ExpenseReimbursementItemCreateRequest();
        request.setReimbursementId(1L);
        request.setItemName("餐饮费");
        request.setItemAmount(new BigDecimal("200.00"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.addExpenseReimbursementItem(request);
        });

        assertEquals("报销单已审批，不能添加明细", exception.getMessage());
        verify(expenseReimbursementItemRepository, never()).save(any(ExpenseReimbursementItem.class));
    }
    
    @Test
    void testGetAttachmentById_Success() {
        com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment mockAttachment = 
            new com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment();
        mockAttachment.setId(1L);
        mockAttachment.setReimbursementId(1L);
        mockAttachment.setFileName("test.jpg");
        mockAttachment.setFilePath("/uploads/test.jpg");
        mockAttachment.setFileSize(1024L);
        mockAttachment.setFileType("image/jpeg");
        
        when(expenseReimbursementAttachmentRepository.findById(1L))
            .thenReturn(Optional.of(mockAttachment));
        
        com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment result = 
            expenseReimbursementService.getAttachmentById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test.jpg", result.getFileName());
        assertEquals("/uploads/test.jpg", result.getFilePath());
        verify(expenseReimbursementAttachmentRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetAttachmentById_NotFound() {
        when(expenseReimbursementAttachmentRepository.findById(1L))
            .thenReturn(Optional.empty());
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseReimbursementService.getAttachmentById(1L);
        });
        
        assertEquals("附件不存在", exception.getMessage());
        verify(expenseReimbursementAttachmentRepository, times(1)).findById(1L);
    }

    @Test
    void testApproveExpenseReimbursement_CreateTransaction() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("PENDING");
        mockReimbursement.setReimbursementNumber("BX202601240001");
        mockReimbursement.setTotalAmount(new BigDecimal("1300.00"));
        mockReimbursement.setReimbursementDate(LocalDate.now());
        mockReimbursement.setFundAccountId(1L);
        mockReimbursement.setCaseId(1L);
        mockReimbursement.setApplicantName("张律师");
        mockReimbursement.setBankAccount("1234567890");
        mockReimbursement.setDescription("测试报销");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setAccountName("测试账户");
        bankAccount.setBankName("测试银行");
        bankAccount.setAccountNumber("1234567890");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(expenseReimbursementRepository.save(any(ExpenseReimbursement.class))).thenReturn(mockReimbursement);

        ExpenseReimbursementApprovalRequest request = new ExpenseReimbursementApprovalRequest();
        request.setApprovalStatus("APPROVED");
        request.setApprovalOpinion("同意");

        expenseReimbursementService.approveExpenseReimbursement(1L, request, 100L);

        verify(expenseReimbursementRepository, times(1)).save(any(ExpenseReimbursement.class));
        verify(bankAccountTransactionRepository, times(1)).save(any(BankAccountTransaction.class));
    }

    @Test
    void testApproveExpenseReimbursement_Rejected_NoTransaction() {
        ExpenseReimbursement mockReimbursement = new ExpenseReimbursement();
        mockReimbursement.setId(1L);
        mockReimbursement.setApprovalStatus("PENDING");

        when(expenseReimbursementRepository.findById(1L)).thenReturn(Optional.of(mockReimbursement));
        when(expenseReimbursementRepository.save(any(ExpenseReimbursement.class))).thenReturn(mockReimbursement);

        ExpenseReimbursementApprovalRequest request = new ExpenseReimbursementApprovalRequest();
        request.setApprovalStatus("REJECTED");
        request.setApprovalOpinion("不同意");

        expenseReimbursementService.approveExpenseReimbursement(1L, request, 100L);

        verify(expenseReimbursementRepository, times(1)).save(any(ExpenseReimbursement.class));
        verify(bankAccountTransactionRepository, never()).save(any(BankAccountTransaction.class));
    }
}
