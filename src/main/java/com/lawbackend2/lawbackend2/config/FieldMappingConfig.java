package com.lawbackend2.lawbackend2.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FieldMappingConfig {
    
    private final Map<String, String> fieldMapping;
    
    public FieldMappingConfig() {
        this.fieldMapping = new HashMap<>();
        initializeMapping();
    }
    
    private void initializeMapping() {
        fieldMapping.put("收件编号", "receiptNumber");
        fieldMapping.put("编号", "receiptNumber");
        fieldMapping.put("序号", "receiptNumber");
        
        fieldMapping.put("债权人", "creditorName");
        fieldMapping.put("债权人名称", "creditorName");
        fieldMapping.put("申报人", "creditorName");
        
        fieldMapping.put("申报时间", "declarationTime");
        fieldMapping.put("申报日期", "declarationTime");
        fieldMapping.put("登记日期", "declarationTime");
        
        fieldMapping.put("住所/邮编", "addressAndPostalCode");
        fieldMapping.put("住所", "addressAndPostalCode");
        fieldMapping.put("地址", "addressAndPostalCode");
        fieldMapping.put("邮编", "addressAndPostalCode");
        fieldMapping.put("送达地址", "serviceAddress");
        
        fieldMapping.put("联系电话", "contactPhone");
        fieldMapping.put("电话", "contactPhone");
        fieldMapping.put("手机号", "contactPhone");
        
        fieldMapping.put("申报金额", "declaredAmount");
        fieldMapping.put("金额", "declaredAmount");
        fieldMapping.put("债权金额", "declaredAmount");
        fieldMapping.put("总金额", "declaredAmount");
        
        fieldMapping.put("性质", "nature");
        fieldMapping.put("债权性质", "claimNature");
        
        fieldMapping.put("法定代表人", "legalRepresentative");
        fieldMapping.put("法人", "legalRepresentative");
        
        fieldMapping.put("代理人", "agentName");
        fieldMapping.put("委托代理人", "agentName");
        
        fieldMapping.put("代理人电话", "agentPhone");
        fieldMapping.put("代理人联系电话", "agentPhone");
        
        fieldMapping.put("债权种类", "claimType");
        fieldMapping.put("债权类型", "claimType");
        
        fieldMapping.put("开户名", "accountName");
        fieldMapping.put("账户名", "accountName");
        
        fieldMapping.put("开户行", "bankName");
        fieldMapping.put("开户银行", "bankName");
        fieldMapping.put("银行名称", "bankName");
        
        fieldMapping.put("账号", "bankAccount");
        fieldMapping.put("银行账号", "bankAccount");
        fieldMapping.put("债权人银行账号", "creditorBankAccount");
        
        fieldMapping.put("涉讼", "litigationStatus");
        fieldMapping.put("诉讼情况", "litigationStatus");
        
        fieldMapping.put("备注", "remarks");
        fieldMapping.put("说明", "remarks");
    }
    
    public String getFieldName(String header) {
        return fieldMapping.get(header);
    }
    
    public Map<String, String> getFieldMapping() {
        return new HashMap<>(fieldMapping);
    }
}
