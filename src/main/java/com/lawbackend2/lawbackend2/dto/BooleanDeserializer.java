package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class BooleanDeserializer extends JsonDeserializer<Boolean> {
    
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 检查是否是数字类型（0/1）
        if (p.currentToken().isNumeric()) {
            int numValue = p.getIntValue();
            return numValue != 0;
        } else {
            String value = p.getText();
            
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
            
            value = value.trim().toLowerCase();
            
            if ("true".equals(value) || "1".equals(value) || "yes".equals(value) || "是".equals(value)) {
                return true;
            }
            
            if ("false".equals(value) || "0".equals(value) || "no".equals(value) || "否".equals(value)) {
                return false;
            }
            
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
