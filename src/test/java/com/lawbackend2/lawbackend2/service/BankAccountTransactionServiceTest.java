package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.BankAccountTransactionRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.impl.BankAccountTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountTransactionServiceTest {

    @Mock
    private BankAccountTransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private BankAccountTransactionServiceImpl transactionService;

    private BankAccount mockAccount;
    private BankAccountTransaction mockTransaction;
    private BankAccountTransactionCreateRequest createRequest;
    private BankAccountTransactionUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockAccount = new BankAccount();
        mockAccount.setId(1L);
        mockAccount.setAccountName("测试账户");
        mockAccount.setBankName("中国银行");
        mockAccount.setAccountNumber("1234567890123456");
        mockAccount.setAccountType("基本户");
        mockAccount.setCurrency("CNY");
        mockAccount.setCurrentBalance(new BigDecimal("1000000.00"));
        mockAccount.setOpeningDate(LocalDate.now());
        mockAccount.setPassword("encryptedPassword");
        mockAccount.setStatus("ACTIVE");
        mockAccount.setCaseId(1L);
        mockAccount.setCreateUserId(1L);

        mockTransaction = new BankAccountTransaction();
        mockTransaction.setId(1L);
        mockTransaction.setAccountId(1L);
        mockTransaction.setTransactionType("IN");
        mockTransaction.setAmount(new BigDecimal("500000.00"));
        mockTransaction.setTransactionDate(LocalDate.now());
        mockTransaction.setSummary("测试交易");
        mockTransaction.setBusinessType("收款");
        mockTransaction.setCounterpartyAccount("6222021234567890");
        mockTransaction.setCounterpartyName("张三");
        mockTransaction.setBalanceAfter(new BigDecimal("1500000.00"));
        mockTransaction.setRemark("测试备注");
        mockTransaction.setCaseId(1L);
        mockTransaction.setStatus("ACTIVE");
        mockTransaction.setCreateUserId(1L);

        createRequest = new BankAccountTransactionCreateRequest();
        createRequest.setAccountId(1L);
        createRequest.setTransactionType("IN");
        createRequest.setAmount(new BigDecimal("100000.00"));
        createRequest.setTransactionDate(LocalDate.now());
        createRequest.setSummary("新测试交易");
        createRequest.setBusinessType("收款");
        createRequest.setCounterpartyAccount("6222029876543210");
        createRequest.setCounterpartyName("李四");
        createRequest.setRemark("新测试备注");
        createRequest.setCaseId(1L);

        updateRequest = new BankAccountTransactionUpdateRequest();
        updateRequest.setAmount(new BigDecimal("600000.00"));
        updateRequest.setSummary("更新后的交易摘要");
        updateRequest.setRemark("更新后的备注");

        lenient().when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));
    }

    @Test
    void testCreateTransaction_Success_Inflow() {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);

        BigDecimal initialBalance = mockAccount.getCurrentBalance();
        Long transactionId = transactionService.createTransaction(createRequest, 1L);

        assertNotNull(transactionId);
        assertEquals(1L, transactionId);

        ArgumentCaptor<BankAccountTransaction> transactionCaptor = ArgumentCaptor.forClass(BankAccountTransaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        BankAccountTransaction savedTransaction = transactionCaptor.getValue();

        BigDecimal expectedBalanceAfter = initialBalance.add(createRequest.getAmount());
        assertEquals(expectedBalanceAfter, savedTransaction.getBalanceAfter());

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository, times(1)).save(accountCaptor.capture());
        BankAccount savedAccount = accountCaptor.getValue();
        assertEquals(expectedBalanceAfter, savedAccount.getCurrentBalance());
    }

    @Test
    void testCreateTransaction_Success_Outflow() {
        createRequest.setTransactionType("OUT");
        createRequest.setAmount(new BigDecimal("500000.00"));

        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);

        BigDecimal initialBalance = mockAccount.getCurrentBalance();
        Long transactionId = transactionService.createTransaction(createRequest, 1L);

        assertNotNull(transactionId);

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository, times(1)).save(accountCaptor.capture());
        BankAccount savedAccount = accountCaptor.getValue();

        BigDecimal expectedBalance = initialBalance.subtract(createRequest.getAmount());
        assertEquals(expectedBalance, savedAccount.getCurrentBalance());
    }

    @Test
    void testCreateTransaction_Outflow_InsufficientBalance() {
        createRequest.setTransactionType("OUT");
        createRequest.setAmount(new BigDecimal("2000000.00"));

        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.createTransaction(createRequest, 1L);
        });

        assertEquals("账户余额不足，无法完成流出交易", exception.getMessage());
        verify(transactionRepository, never()).save(any(BankAccountTransaction.class));
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void testCreateTransaction_InvalidTransactionType() {
        createRequest.setTransactionType("INVALID");

        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.createTransaction(createRequest, 1L);
        });

        assertEquals("无效的交易类型，必须是 IN(流入) 或 OUT(流出)", exception.getMessage());
        verify(transactionRepository, never()).save(any(BankAccountTransaction.class));
    }

    @Test
    void testCreateTransaction_AccountNotFound() {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.createTransaction(createRequest, 1L);
        });

        assertEquals("银行账户不存在", exception.getMessage());
        verify(bankAccountRepository, times(1)).findById(1L);
        verify(transactionRepository, never()).save(any(BankAccountTransaction.class));
    }

    @Test
    void testCreateTransaction_WithoutCaseId() {
        createRequest.setCaseId(null);
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);

        Long transactionId = transactionService.createTransaction(createRequest, 1L);

        assertNotNull(transactionId);
        verify(transactionRepository, times(1)).save(any(BankAccountTransaction.class));
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testCreateTransaction_WithNullBalance() {
        mockAccount.setCurrentBalance(null);
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);

        Long transactionId = transactionService.createTransaction(createRequest, 1L);

        assertNotNull(transactionId);

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository, times(1)).save(accountCaptor.capture());
        BankAccount savedAccount = accountCaptor.getValue();
        assertEquals(createRequest.getAmount(), savedAccount.getCurrentBalance());
    }

    @Test
    void testGetTransactionList_Success() {
        BankAccountTransactionResponse response = new BankAccountTransactionResponse(
            1L, "ACTIVE", false, null, null, 1L, 1L, 1L, "测试账户",
            "1234567890123456", "中国银行", "IN", new BigDecimal("500000.00"),
            LocalDate.now(), "测试交易", "收款", "6222021234567890", "张三",
            new BigDecimal("1500000.00"), null, null, "测试备注", 1L, "BK2026001", "测试案件"
        );

        List<BankAccountTransactionResponse> responseList = Arrays.asList(response);
        Page<BankAccountTransactionResponse> page = new PageImpl<>(responseList, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "transactionDate", "createTime")), 1L);

        lenient().when(transactionRepository.findTransactionsWithDetails(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        com.lawbackend2.lawbackend2.common.PageResult<BankAccountTransactionResponse> result =
            transactionService.getTransactionList(1, 10, 1L, "IN", "收款", null, null, 1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getTotal());
        assertNotNull(result.getList());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        verify(transactionRepository, times(1)).findTransactionsWithDetails(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    void testGetTransactionDetail_Success() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        BankAccountTransaction result = transactionService.getTransactionDetail(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("IN", result.getTransactionType());
        assertEquals(new BigDecimal("500000.00"), result.getAmount());
        verify(transactionRepository, times(1)).findById(1L);
        verify(bankAccountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionDetail_TransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.getTransactionDetail(1L, 1L);
        });

        assertEquals("交易记录不存在", exception.getMessage());
        verify(transactionRepository, times(1)).findById(1L);
        verify(bankAccountRepository, never()).findById(anyLong());
    }

    @Test
    void testGetTransactionDetail_AccountNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.getTransactionDetail(1L, 1L);
        });

        assertEquals("关联银行账户不存在", exception.getMessage());
        verify(transactionRepository, times(1)).findById(1L);
        verify(bankAccountRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateTransaction_Success_NoBalanceChange() {
        updateRequest.setTransactionType(null);
        updateRequest.setAmount(null);

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        BigDecimal initialBalance = mockAccount.getCurrentBalance();

        transactionService.updateTransaction(1L, updateRequest, 1L);

        verify(transactionRepository, times(1)).findById(1L);
        verify(bankAccountRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(any(BankAccountTransaction.class));
        verify(bankAccountRepository, never()).save(any(BankAccount.class));

        assertEquals(initialBalance, mockAccount.getCurrentBalance());
    }

    @Test
    void testUpdateTransaction_Success_AmountChanged() {
        mockTransaction.setTransactionType("IN");
        mockTransaction.setAmount(new BigDecimal("100000.00"));
        mockAccount.setCurrentBalance(new BigDecimal("1100000.00"));

        updateRequest.setAmount(new BigDecimal("200000.00"));

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        transactionService.updateTransaction(1L, updateRequest, 1L);

        verify(transactionRepository, times(1)).save(any(BankAccountTransaction.class));
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(accountCaptor.capture());

        BigDecimal expectedBalance = new BigDecimal("1100000.00")
            .subtract(new BigDecimal("100000.00"))
            .add(new BigDecimal("200000.00"));
        assertEquals(expectedBalance, accountCaptor.getValue().getCurrentBalance());
    }

    @Test
    void testUpdateTransaction_Success_TypeChanged() {
        mockTransaction.setTransactionType("IN");
        mockTransaction.setAmount(new BigDecimal("100000.00"));
        mockAccount.setCurrentBalance(new BigDecimal("1100000.00"));

        updateRequest.setTransactionType("OUT");
        updateRequest.setAmount(new BigDecimal("100000.00"));

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any(BankAccountTransaction.class))).thenReturn(mockTransaction);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        transactionService.updateTransaction(1L, updateRequest, 1L);

        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(accountCaptor.capture());

        BigDecimal expectedBalance = new BigDecimal("1100000.00")
            .subtract(new BigDecimal("100000.00"))
            .subtract(new BigDecimal("100000.00"));
        assertEquals(expectedBalance, accountCaptor.getValue().getCurrentBalance());
    }

    @Test
    void testUpdateTransaction_Outflow_InsufficientBalance() {
        mockTransaction.setTransactionType("IN");
        mockTransaction.setAmount(new BigDecimal("100000.00"));
        mockAccount.setCurrentBalance(new BigDecimal("50000.00"));

        updateRequest.setTransactionType("OUT");

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.updateTransaction(1L, updateRequest, 1L);
        });

        assertEquals("账户余额不足，无法完成流出交易", exception.getMessage());
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void testUpdateTransaction_TransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.updateTransaction(1L, updateRequest, 1L);
        });

        assertEquals("交易记录不存在", exception.getMessage());
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, never()).save(any(BankAccountTransaction.class));
    }

    @Test
    void testUpdateTransaction_AccountNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.updateTransaction(1L, updateRequest, 1L);
        });

        assertEquals("关联银行账户不存在", exception.getMessage());
    }

    @Test
    void testDeleteTransaction_Success_Inflow() {
        mockTransaction.setTransactionType("IN");
        mockTransaction.setAmount(new BigDecimal("100000.00"));
        mockAccount.setCurrentBalance(new BigDecimal("1100000.00"));

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);
        doNothing().when(transactionRepository).deleteById(anyLong());
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        transactionService.deleteTransaction(1L, 1L);

        verify(transactionRepository, times(1)).findById(1L);
        verify(bankAccountRepository, times(1)).findById(1L);

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(accountCaptor.capture());

        BigDecimal expectedBalance = new BigDecimal("1100000.00").subtract(new BigDecimal("100000.00"));
        assertEquals(expectedBalance, accountCaptor.getValue().getCurrentBalance());

        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_Success_Outflow() {
        mockTransaction.setTransactionType("OUT");
        mockTransaction.setAmount(new BigDecimal("100000.00"));
        mockAccount.setCurrentBalance(new BigDecimal("900000.00"));

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);
        doNothing().when(transactionRepository).deleteById(anyLong());
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        transactionService.deleteTransaction(1L, 1L);

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(accountCaptor.capture());

        BigDecimal expectedBalance = new BigDecimal("900000.00").add(new BigDecimal("100000.00"));
        assertEquals(expectedBalance, accountCaptor.getValue().getCurrentBalance());

        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_TransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.deleteTransaction(1L, 1L);
        });

        assertEquals("交易记录不存在", exception.getMessage());
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteTransaction_AccountNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.deleteTransaction(1L, 1L);
        });

        assertEquals("关联银行账户不存在", exception.getMessage());
    }

    @Test
    void testDeleteTransaction_WithNullBalance() {
        mockTransaction.setTransactionType("IN");
        mockTransaction.setAmount(new BigDecimal("100000.00"));
        mockAccount.setCurrentBalance(null);

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(mockTransaction));
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(mockAccount);
        doNothing().when(transactionRepository).deleteById(anyLong());
        when(userRoleRepository.findRoleIdsByUserId(anyLong())).thenReturn(Arrays.asList(1L));

        transactionService.deleteTransaction(1L, 1L);

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(accountCaptor.capture());

        assertEquals(new BigDecimal("-100000.00"), accountCaptor.getValue().getCurrentBalance());
        verify(transactionRepository, times(1)).deleteById(1L);
    }
}
