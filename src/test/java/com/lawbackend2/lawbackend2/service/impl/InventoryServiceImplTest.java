package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.InventoryCreateRequest;
import com.lawbackend2.lawbackend2.entity.Inventory;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.InventoryRepository;
import com.lawbackend2.lawbackend2.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_INVENTORY_ID = 1L;
    private static final String TEST_INVENTORY_TYPE = "RAW_MATERIAL";
    private static final String TEST_INVENTORY_NAME = "测试存货";

    private InventoryCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new InventoryCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setInventoryType(TEST_INVENTORY_TYPE);
        createRequest.setInventoryName(TEST_INVENTORY_NAME);
        createRequest.setQuantity(new BigDecimal("100"));
        createRequest.setUnitPrice(new BigDecimal("50.00"));
        createRequest.setTotalValue(new BigDecimal("5000.00"));

        Inventory mockInventory = new Inventory();
        mockInventory.setId(TEST_INVENTORY_ID);
        mockInventory.setCaseId(TEST_CASE_ID);
        mockInventory.setInventoryType(TEST_INVENTORY_TYPE);
        mockInventory.setInventoryName(TEST_INVENTORY_NAME);
        mockInventory.setInventoryStatus("NORMAL");
        mockInventory.setManagementStatus("PENDING");

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(mockInventory);
        when(inventoryRepository.findById(TEST_INVENTORY_ID))
                .thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateInventory_Success() {
        Long inventoryId = inventoryService.createInventory(createRequest);

        assertNotNull(inventoryId);
        assertEquals(TEST_INVENTORY_ID, inventoryId);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testGetInventoryDetail_Success() {
        Inventory inventory = inventoryService.getInventoryDetail(TEST_INVENTORY_ID);

        assertNotNull(inventory);
        assertEquals(TEST_INVENTORY_ID, inventory.getId());
        assertEquals(TEST_CASE_ID, inventory.getCaseId());
        verify(inventoryRepository, times(1)).findById(TEST_INVENTORY_ID);
    }

    @Test
    void testGetInventoryDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inventoryService.getInventoryDetail(999L);
        });

        assertEquals("存货不存在", exception.getMessage());
    }

    @Test
    void testUpdateInventory_Success() {
        inventoryService.updateInventory(TEST_INVENTORY_ID, createRequest);

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testDeleteInventory_Success() {
        inventoryService.deleteInventory(TEST_INVENTORY_ID);

        verify(inventoryRepository, times(1)).delete(any(Inventory.class));
    }
}