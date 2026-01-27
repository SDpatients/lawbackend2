package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApproveRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.DocumentDeliveryResponse;
import com.lawbackend2.lawbackend2.entity.DocumentDelivery;
import com.lawbackend2.lawbackend2.service.DocumentDeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentDeliveryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentDeliveryService documentDeliveryService;

    private DocumentDelivery mockDocumentDelivery;

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
    }

    @Test
    void testCreateDocumentDelivery_Success() throws Exception {
        DocumentDeliveryCreateRequest request = new DocumentDeliveryCreateRequest();
        request.setCaseId(123L);
        request.setDocumentName("测试文书");
        request.setDocumentType("COURT_NOTICE");
        request.setRecipientName("张三");
        request.setRecipientType("DEBTOR");
        request.setContactPhone("13800138000");
        request.setDeliveryAddress("北京市");
        request.setDeliveryMethod("POST");

        when(documentDeliveryService.createDocumentDelivery(any(DocumentDeliveryCreateRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/document-delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deliveryId").value(1L));
    }

    @Test
    void testCreateDocumentDeliveryDirect_Success() throws Exception {
        DocumentDeliveryCreateRequest request = new DocumentDeliveryCreateRequest();
        request.setCaseId(123L);
        request.setDocumentName("测试文书");
        request.setDocumentType("COURT_NOTICE");
        request.setRecipientName("张三");
        request.setRecipientType("DEBTOR");
        request.setContactPhone("13800138000");
        request.setDeliveryAddress("北京市");
        request.setDeliveryMethod("POST");

        when(documentDeliveryService.createDocumentDeliveryDirect(any(DocumentDeliveryCreateRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/document-delivery/direct-upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deliveryId").value(1L))
                .andExpect(jsonPath("$.message").value("文书上传成功"));
    }

    @Test
    void testCreateDocumentDeliveryForApproval_Success() throws Exception {
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

        when(documentDeliveryService.createDocumentDeliveryForApproval(any(DocumentDeliveryApprovalRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/document-delivery/submit-for-approval")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deliveryId").value(1L))
                .andExpect(jsonPath("$.message").value("文书审批提交成功，等待管理员审批"));
    }

    @Test
    void testApproveDocumentDelivery_Success() throws Exception {
        DocumentDeliveryApproveRequest request = new DocumentDeliveryApproveRequest();
        request.setDeliveryId(1L);
        request.setApprovalResult("PASS");
        request.setRemark("审批通过");

        doNothing().when(documentDeliveryService).approveDocumentDelivery(any(DocumentDeliveryApproveRequest.class));

        mockMvc.perform(post("/document-delivery/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("文书审批完成"));
    }

    @Test
    void testGetDocumentDeliveryList_Success() throws Exception {
        PageResult<DocumentDelivery> pageResult = new PageResult<>();
        pageResult.setTotal(1L);
        pageResult.setList(Arrays.asList(mockDocumentDelivery));

        when(documentDeliveryService.getDocumentDeliveryList(anyInt(), anyInt(), anyLong(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(pageResult);

        mockMvc.perform(get("/document-delivery/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("caseId", "123")
                        .param("caseNumber", "CASE001")
                        .param("documentType", "COURT_NOTICE")
                        .param("recipientType", "DEBTOR")
                        .param("deliveryMethod", "POST")
                        .param("sendStatus", "PENDING")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].id").value(1L))
                .andExpect(jsonPath("$.data.list[0].documentName").value("测试文书"));
    }

    @Test
    void testGetDocumentDeliveryDetail_Success() throws Exception {
        when(documentDeliveryService.getDocumentDeliveryDetail(1L)).thenReturn(mockDocumentDelivery);

        mockMvc.perform(get("/document-delivery/{deliveryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.documentName").value("测试文书"))
                .andExpect(jsonPath("$.data.recipientName").value("张三"));
    }

    @Test
    void testGetDocumentDeliveryDetailWithCase_Success() throws Exception {
        DocumentDeliveryResponse response = DocumentDeliveryResponse.builder()
                .id(1L)
                .caseId(123L)
                .caseNumber("CASE001")
                .caseName("测试案件")
                .documentName("测试文书")
                .documentType("COURT_NOTICE")
                .recipientName("张三")
                .recipientType("DEBTOR")
                .contactPhone("13800138000")
                .deliveryAddress("北京市")
                .deliveryMethod("POST")
                .sendStatus("PENDING")
                .status("PENDING")
                .build();

        when(documentDeliveryService.getDocumentDeliveryDetailWithCase(1L)).thenReturn(response);

        mockMvc.perform(get("/document-delivery/{deliveryId}/detail", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.documentName").value("测试文书"))
                .andExpect(jsonPath("$.data.caseNumber").value("CASE001"));
    }

    @Test
    void testUpdateDocumentDelivery_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("documentName", "更新后的文书名称");
        request.put("recipientName", "李四");
        request.put("contactPhone", "13900139000");

        doNothing().when(documentDeliveryService).updateDocumentDelivery(anyLong(), any());

        mockMvc.perform(put("/document-delivery/{deliveryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteDocumentDelivery_Success() throws Exception {
        doNothing().when(documentDeliveryService).deleteDocumentDelivery(anyLong());

        mockMvc.perform(delete("/document-delivery/{deliveryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateSendStatus_Success() throws Exception {
        doNothing().when(documentDeliveryService).updateSendStatus(anyLong(), anyString(), anyString());

        mockMvc.perform(put("/document-delivery/{deliveryId}/send-status", 1L)
                        .param("sendStatus", "SENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateSendStatus_WithFailureReason() throws Exception {
        doNothing().when(documentDeliveryService).updateSendStatus(anyLong(), anyString(), anyString());

        mockMvc.perform(put("/document-delivery/{deliveryId}/send-status", 1L)
                        .param("sendStatus", "FAILED")
                        .param("failureReason", "地址错误"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateDeliveryStatus_Success() throws Exception {
        doNothing().when(documentDeliveryService).updateDeliveryStatus(anyLong(), anyString());

        mockMvc.perform(put("/document-delivery/{deliveryId}/delivery-status", 1L)
                        .param("sendStatus", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetAllDocumentDeliveryList_Success() throws Exception {
        PageResult<DocumentDelivery> pageResult = new PageResult<>();
        pageResult.setTotal(1L);
        pageResult.setList(Arrays.asList(mockDocumentDelivery));

        when(documentDeliveryService.getAllDocumentDeliveryList(anyInt(), anyInt(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(pageResult);

        mockMvc.perform(get("/document-delivery/all")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("documentType", "COURT_NOTICE")
                        .param("status", "PENDING")
                        .param("caseNumber", "CASE001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testUpdateStatusAndRemark_Success() throws Exception {
        doNothing().when(documentDeliveryService).updateStatusAndRemark(anyLong(), anyString(), anyString());

        mockMvc.perform(put("/document-delivery/{deliveryId}/status-remark", 1L)
                        .param("status", "APPROVED")
                        .param("remark", "审批通过"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
