package com.lawbackend2.lawbackend2.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    
    private static final DateTimeFormatter FLEXIBLE_ISO_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .optionalStart()
        .appendOffsetId()
        .optionalEnd()
        .toFormatter();

    private static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(value, FLEXIBLE_ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            // 继续尝试其他格式
        }

        try {
            return LocalDateTime.parse(value, STANDARD_FORMATTER);
        } catch (DateTimeParseException e) {
            // 继续尝试其他格式
        }

        try {
            return LocalDateTime.parse(value + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // 继续尝试其他格式
        }

        try {
            Instant instant = Instant.parse(value);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            // 所有格式都失败
        }
        
        throw new IOException("无法解析日期时间: " + value);
    }
}