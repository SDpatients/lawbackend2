package com.lawbackend2.lawbackend2.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.lawbackend2.lawbackend2.annotation.Mask;
import com.lawbackend2.lawbackend2.annotation.MaskType;

import java.io.IOException;

public class MaskSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private MaskType maskType;

    public MaskSerializer() {
        this.maskType = MaskType.DEFAULT;
    }

    public MaskSerializer(MaskType maskType) {
        this.maskType = maskType;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        if (shouldSkipMask()) {
            gen.writeString(value);
            return;
        }
        gen.writeString(MaskUtil.mask(value, maskType));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            Mask mask = property.getAnnotation(Mask.class);
            if (mask != null) {
                return new MaskSerializer(mask.value());
            }
        }
        return this;
    }

    private boolean shouldSkipMask() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return UserActivityTracker.isUserActive(userId);
        } catch (Exception e) {
            return false;
        }
    }
}