package com.lawbackend2.lawbackend2.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
        ISO_FORMATTER,
        STANDARD_FORMATTER,
        DATE_FORMATTER
    );
    
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                if (formatter == DATE_FORMATTER) {
                    return LocalDateTime.parse(value + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    return LocalDateTime.parse(value, formatter);
                }
            } catch (DateTimeParseException e) {
                // 尝试下一个格式
            }
        }
        
        throw new IOException("无法解析日期时间: " + value + ", 支持的格式: yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd");
    }
}