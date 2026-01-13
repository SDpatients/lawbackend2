package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.InventoryCreateRequest;
import com.lawbackend2.lawbackend2.entity.Inventory;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.InventoryRepository;
import com.lawbackend2.lawbackend2.service.InventoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Long createInventory(InventoryCreateRequest request) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(request, inventory);
        inventory.setInventoryNo(generateInventoryNo());
        inventory.setInventoryStatus("NORMAL");
        inventory.setManagementStatus("PENDING");
        inventory.setStatus("ACTIVE");

        Inventory saved = inventoryRepository.save(inventory);
        return saved.getId();
    }

    @Override
    public PageResult<Inventory> getInventoryList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String inventoryType, String inventoryStatus, String managementStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Inventory> page = inventoryRepository.findByConditions(caseId, inventoryType, inventoryStatus, managementStatus, pageable);

        PageResult<Inventory> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public Inventory getInventoryDetail(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException("存货不存在"));
    }

    @Override
    public void updateInventory(Long inventoryId, InventoryCreateRequest request) {
        Inventory inventory = getInventoryDetail(inventoryId);
        BeanUtils.copyProperties(request, inventory, "id", "inventoryNo", "caseId", "caseName", "propertyId");
        inventoryRepository.save(inventory);
    }

    @Override
    public void deleteInventory(Long inventoryId) {
        Inventory inventory = getInventoryDetail(inventoryId);
        inventoryRepository.delete(inventory);
    }

    private String generateInventoryNo() {
        return "INV" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}