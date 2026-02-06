package com.lawbackend2.lawbackend2.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板处理器注册中心
 * 管理所有模板处理器，根据模板编码分发到对应的处理器
 */
@Slf4j
@Component
public class TemplateHandlerRegistry {

    private final Map<String, TemplateHandler> handlers = new ConcurrentHashMap<>();

    /**
     * 注册处理器
     */
    public void register(TemplateHandler handler) {
        String templateCode = handler.getTemplateCode();
        handlers.put(templateCode.toLowerCase(), handler);
        log.info("注册模板处理器: {} - {}", templateCode, handler.getTemplateName());
    }

    /**
     * 获取处理器
     */
    public TemplateHandler getHandler(String templateCode) {
        if (templateCode == null) {
            return null;
        }
        return handlers.get(templateCode.toLowerCase());
    }

    /**
     * 判断是否支持该模板编码
     */
    public boolean supports(String templateCode) {
        if (templateCode == null) {
            return false;
        }
        return handlers.containsKey(templateCode.toLowerCase());
    }

    /**
     * 获取所有注册的处理器
     */
    public Map<String, TemplateHandler> getAllHandlers() {
        return new ConcurrentHashMap<>(handlers);
    }

    /**
     * 获取所有支持的模板编码
     */
    public List<String> getAllTemplateCodes() {
        return List.copyOf(handlers.keySet());
    }
}
