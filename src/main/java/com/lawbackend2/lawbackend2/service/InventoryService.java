package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.InventoryCreateRequest;
import com.lawbackend2.lawbackend2.entity.Inventory;

public interface InventoryService {

    Long createInventory(InventoryCreateRequest request);

    PageResult<Inventory> getInventoryList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String inventoryType, String inventoryStatus, String managementStatus);

    Inventory getInventoryDetail(Long inventoryId);

    void updateInventory(Long inventoryId, InventoryCreateRequest request);

    void deleteInventory(Long inventoryId);
}