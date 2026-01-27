package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementItemCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursement;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementRepository;
import com.lawbackend2.lawbackend2.repository.FundAccountRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.ExpenseReimbursementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseReimbursementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseReimbursementService expenseReimbursementService;

    @MockBean
    private BankruptCaseRepository bankruptCaseRepository;

    @MockBean
    private FundAccountRepository fundAccountRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ExpenseReimbursementRepository expenseReimbursementRepository;

    private BankruptCase testCase;
    private FundAccount testFundAccount;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCase = new BankruptCase();
        testCase.setId(1L);
        testCase.setCaseName("测试案件");

        testFundAccount = new FundAccount();
        testFundAccount.setId(1L);
        testFundAccount.setAccountName("测试账户");
        testFundAccount.setBankName("中国银行");
        testFundAccount.setBankAccount("1234567890");

        testUser = new User();
        testUser.setId(1L);
        testUser.setRealName("张律师");

        when(bankruptCaseRepository.findById(anyLong())).thenReturn(Optional.of(testCase));
        when(fundAccountRepository.findById(anyLong())).thenReturn(Optional.of(testFundAccount));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
    }

    @AfterEach
    void tearDown() {
        reset(expenseReimbursementService);
    }

    @Test
    void testCreateExpenseReimbursement_Success() throws Exception {
        ExpenseReimbursementCreateRequest request = new ExpenseReimbursementCreateRequest();
        request.setCaseId(1L);
        request.setFundAccountId(1L);
        request.setReimbursementDate(LocalDate.now());
        request.setDescription("测试报销");

        ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest item1 = 
            new ExpenseReimbursementCreateRequest.ExpenseReimbursementItemRequest();
        item1.setItemName("交通费");
        item1.setItemAmount(new BigDecimal("500.00"));
        item1.setItemDescription("高铁票");

        request.setItems(Arrays.asList(item1));

        when(expenseReimbursementService.createExpenseReimbursement(any(ExpenseReimbursementCreateRequest.class)))
            .thenReturn(1L);

        mockMvc.perform(post("/expense-reimbursement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reimbursementId").value(1));
    }

    @Test
    void testGetExpenseReimbursementDetail_Success() throws Exception {
        when(expenseReimbursementService.getExpenseReimbursementDetail(1L))
            .thenReturn(new com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse());

        mockMvc.perform(get("/expense-reimbursement/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetExpenseReimbursementList_Success() throws Exception {
        com.lawbackend2.lawbackend2.common.PageResult<com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse> pageResult = 
            new com.lawbackend2.lawbackend2.common.PageResult<>();
        pageResult.setTotal(1L);
        pageResult.setList(Arrays.asList(new com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse()));

        when(expenseReimbursementService.getExpenseReimbursementList(
            anyInt(), anyInt(), anyLong(), anyLong(), anyString(), any(LocalDate.class)))
            .thenReturn(pageResult);

        mockMvc.perform(get("/expense-reimbursement")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testUpdateExpenseReimbursement_Success() throws Exception {
        ExpenseReimbursementUpdateRequest request = new ExpenseReimbursementUpdateRequest();
        request.setId(1L);
        request.setCaseId(1L);
        request.setFundAccountId(1L);
        request.setReimbursementDate(LocalDate.now());
        request.setDescription("更新后的报销");

        doNothing().when(expenseReimbursementService).updateExpenseReimbursement(any(ExpenseReimbursementUpdateRequest.class));

        mockMvc.perform(put("/expense-reimbursement/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteExpenseReimbursement_Success() throws Exception {
        doNothing().when(expenseReimbursementService).deleteExpenseReimbursement(1L);

        mockMvc.perform(delete("/expense-reimbursement/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testApproveExpenseReimbursement_Success() throws Exception {
        ExpenseReimbursementApprovalRequest request = new ExpenseReimbursementApprovalRequest();
        request.setApprovalStatus("APPROVED");
        request.setApprovalOpinion("同意");

        doNothing().when(expenseReimbursementService).approveExpenseReimbursement(1L, request, anyLong());

        mockMvc.perform(post("/expense-reimbursement/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testAddExpenseReimbursementItem_Success() throws Exception {
        ExpenseReimbursementItemCreateRequest request = new ExpenseReimbursementItemCreateRequest();
        request.setItemName("餐饮费");
        request.setItemAmount(new BigDecimal("200.00"));
        request.setItemDescription("工作餐");

        when(expenseReimbursementService.addExpenseReimbursementItem(any(ExpenseReimbursementItemCreateRequest.class)))
            .thenReturn(1L);

        mockMvc.perform(post("/expense-reimbursement/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.itemId").value(1));
    }

    @Test
    void testDeleteExpenseReimbursementItem_Success() throws Exception {
        doNothing().when(expenseReimbursementService).deleteExpenseReimbursementItem(1L);

        mockMvc.perform(delete("/expense-reimbursement/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUploadExpenseReimbursementAttachment_Success() throws Exception {
        when(expenseReimbursementService.uploadExpenseReimbursementAttachment(
            anyLong(), anyString(), anyString(), anyLong(), anyString()))
            .thenReturn(1L);

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/expense-reimbursement/1/attachments")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.attachmentId").value(1));
    }

    @Test
    void testDeleteExpenseReimbursementAttachment_Success() throws Exception {
        doNothing().when(expenseReimbursementService).deleteExpenseReimbursementAttachment(1L);

        mockMvc.perform(delete("/expense-reimbursement/1/attachments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
