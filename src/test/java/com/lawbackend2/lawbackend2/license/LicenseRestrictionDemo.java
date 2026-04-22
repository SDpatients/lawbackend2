package com.lawbackend2.lawbackend2.license;

import com.lawbackend2.lawbackend2.license.annotation.RequireLicense;
import org.springframework.web.bind.annotation.*;

/**
 * 许可证功能限制示例
 * 展示如何在实际业务中使用 @RequireLicense 注解
 */
@RestController
@RequestMapping("/demo/license-restriction")
public class LicenseRestrictionDemo {

    /**
     * 场景1: 整个模块需要授权
     * 案件管理模块 - 需要 "case" 模块授权
     */
    @GetMapping("/cases")
    @RequireLicense(modules = {"case"})
    public String listCases() {
        return "案件列表";
    }

    @PostMapping("/cases")
    @RequireLicense(modules = {"case"})
    public String createCase() {
        return "创建案件";
    }

    /**
     * 场景2: 特定功能需要授权
     * AI助手功能 - 需要 "ai" 模块授权
     */
    @PostMapping("/ai/chat")
    @RequireLicense(modules = {"ai"})
    public String aiChat() {
        return "AI回复";
    }

    /**
     * 场景3: 高级功能需要多个模块授权
     * 统计报表 - 需要 "report" 和 "case" 模块授权
     */
    @GetMapping("/reports/case-statistics")
    @RequireLicense(modules = {"report", "case"})
    public String caseStatistics() {
        return "案件统计报表";
    }

    /**
     * 场景4: 基础功能无需授权
     * 系统信息 - 任何人都可以访问
     */
    @GetMapping("/system/info")
    public String systemInfo() {
        return "系统信息";
    }

    /**
     * 场景5: 许可证管理功能（已在 LicenseController 中实现）
     * 这些接口不需要 @RequireLicense，因为拦截器已特殊处理
     */
}
