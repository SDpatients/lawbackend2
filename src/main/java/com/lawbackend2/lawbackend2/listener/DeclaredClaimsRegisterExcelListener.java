package com.lawbackend2.lawbackend2.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationCreateRequest;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeclaredClaimsRegisterExcelListener extends AnalysisEventListener<DeclaredClaimsRegisterExcelImportDTO> {

    private final ClaimRegistrationService claimRegistrationService;
    private final Long caseId;
    private final Long userId;
    private final List<ClaimRegistrationCreateRequest> dataList = new ArrayList<>();
    private static final int BATCH_SIZE = 100;

    public DeclaredClaimsRegisterExcelListener(ClaimRegistrationService claimRegistrationService, Long caseId, Long userId) {
        this.claimRegistrationService = claimRegistrationService;
        this.caseId = caseId;
        this.userId = userId;
        log.info("初始化DeclaredClaimsRegisterExcelListener - caseId: {}, userId: {}", caseId, userId);
    }

    @Override
    public void invoke(DeclaredClaimsRegisterExcelImportDTO data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex() + 1; // Excel行号从1开始
        
        // 详细记录每一行的所有字段值
        log.debug("处理Excel行 {}: 收件编号={}, 债权人={}, 申报时间={}, 住所/邮编={}, 联系电话={}, 申报金额={}, 性质={}, 法定代表人={}, 代理人={}, 联系电话={}, 债权性质={}, 债权种类={}, 开户名={}, 开户行={}, 账号={}, 涉讼={}, 备注={}", 
                rowIndex, data.getReceiptNumber(), data.getCreditorName(), data.getDeclarationTime(), 
                data.getAddressAndPostalCode(), data.getContactPhone(), data.getDeclaredAmount(), 
                data.getNature(), data.getLegalRepresentative(), data.getAgentName(), 
                data.getAgentPhone(), data.getClaimNature(), data.getClaimType(), 
                data.getAccountName(), data.getBankName(), data.getBankAccount(), 
                data.getLitigationStatus(), data.getRemarks());

        // 跳过表头和空行
        if (data.getCreditorName() == null || data.getCreditorName().trim().isEmpty()) {
            log.debug("跳过空行或无效行: 行号={}", rowIndex);
            return;
        }

        // 转换为创建请求对象
        ClaimRegistrationCreateRequest request = convertToCreateRequest(data);
        if (request != null) {
            dataList.add(request);
            log.debug("添加数据到批处理列表: 债权人={}, 案件ID={}", request.getCreditorName(), request.getCaseId());
        } else {
            log.warn("数据转换失败，跳过: 行号={}, 债权人={}", rowIndex, data.getCreditorName());
        }

        // 达到BATCH_SIZE，需要存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (dataList.size() >= BATCH_SIZE) {
            log.info("达到批处理阈值({}条)，执行批量保存", BATCH_SIZE);
            saveData();
            dataList.clear();
            log.info("批处理完成，清空数据列表");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成，处理最后一批数据");
        // 所有数据解析完成后，处理剩余数据
        if (!dataList.isEmpty()) {
            log.info("剩余 {} 条数据需要保存", dataList.size());
            saveData();
        } else {
            log.info("没有剩余数据需要保存");
        }
        log.info("Excel 解析完成！总计处理 {} 条有效数据", dataList.size());
    }

    private void saveData() {
        log.info("开始保存 {} 条数据", dataList.size());
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < dataList.size(); i++) {
            ClaimRegistrationCreateRequest request = dataList.get(i);
            try {
                log.debug("保存第 {} 条数据: 债权人={}, 案件ID={}", i+1, request.getCreditorName(), request.getCaseId());
                claimRegistrationService.createClaim(request, userId);
                successCount++;
                log.debug("保存成功: 债权人={}", request.getCreditorName());
            } catch (Exception e) {
                failCount++;
                log.error("保存数据失败 (第 {} 条): 债权人={}, 错误: {}", i+1, request.getCreditorName(), e.getMessage(), e);
            }
        }
        
        log.info("保存完成！成功: {} 条, 失败: {} 条", successCount, failCount);
    }

    private ClaimRegistrationCreateRequest convertToCreateRequest(DeclaredClaimsRegisterExcelImportDTO data) {
        ClaimRegistrationCreateRequest request = new ClaimRegistrationCreateRequest();

        // 基本信息
        request.setCaseId(caseId);
        request.setCreditorName(data.getCreditorName());
        request.setLegalRepresentative(data.getLegalRepresentative());
        request.setServiceAddress(data.getAddressAndPostalCode());

        // 代理人信息
        request.setAgentName(data.getAgentName());
        request.setAgentPhone(data.getAgentPhone());

        // 银行信息
        request.setAccountName(data.getAccountName());
        request.setCreditorBankAccount(data.getBankAccount());
        request.setBankName(data.getBankName());

        // 债权信息
        request.setClaimNature(data.getClaimNature());
        request.setClaimType(data.getClaimType());
        
        // 处理金额字符串
        if (data.getDeclaredAmount() != null && !data.getDeclaredAmount().trim().isEmpty()) {
            try {
                // 移除可能的逗号分隔符
                String amountStr = data.getDeclaredAmount().replace(",", "");
                request.setTotalAmount(new java.math.BigDecimal(amountStr));
            } catch (NumberFormatException e) {
                log.warn("金额格式错误: {}", data.getDeclaredAmount());
                request.setTotalAmount(java.math.BigDecimal.ZERO);
            }
        }

        // 默认值
        request.setCreditorType("自然人");

        // 其他信息
        request.setRemarks(data.getRemarks());

        log.debug("数据转换完成: 债权人={}, 案件ID={}, 总金额={}, 申报时间={}", 
                request.getCreditorName(), request.getCaseId(), request.getTotalAmount(), data.getDeclarationTime());
        
        return request;
    }
}
