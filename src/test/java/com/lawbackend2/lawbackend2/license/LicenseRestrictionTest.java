package com.lawbackend2.lawbackend2.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lawbackend2.lawbackend2.license.exception.LicenseException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 许可证功能限制演示测试
 * 展示不同场景下的功能限制行为
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LicenseRestrictionTest {

    @TempDir
    static Path tempDir;

    private static LicenseService licenseService;
    private static LicenseProperties licenseProperties;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static String machineCode;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 生成密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // 获取机器码
        machineCode = MachineCodeGenerator.generate();

        // 配置 LicenseProperties
        licenseProperties = new LicenseProperties();
        licenseProperties.setEnabled(true);
        licenseProperties.setFilePath(tempDir.resolve("license.lic").toString());
        licenseProperties.setPublicKey(Base64Utils.encodeToString(publicKey.getEncoded()));
        licenseProperties.setWarningDays(30);
        licenseProperties.setStrictMode(true);
        licenseProperties.validate();

        // 创建 LicenseService
        licenseService = new LicenseService(licenseProperties);
    }

    @Test
    @Order(1)
    @DisplayName("场景1: 无许可证时，isValid() 返回 false")
    void testNoLicenseIsInvalid() {
        System.out.println("\n========== 场景1: 无许可证时的状态 ==========");

        // 确保许可证文件不存在
        new java.io.File(licenseProperties.getFilePath()).delete();

        // 重新验证
        licenseService.validateLicense();

        System.out.println("许可证是否启用: " + licenseService.isLicenseEnabled());
        System.out.println("许可证是否有效: " + licenseService.isValid());
        System.out.println("错误信息: " + licenseService.getValidationErrorMessage());
        System.out.println("错误代码: " + licenseService.getLastValidationErrorCode());

        assertFalse(licenseService.isValid(), "无许可证时应返回无效");
        assertEquals("FILE_NOT_FOUND", licenseService.getLastValidationErrorCode());

        System.out.println("✓ 无许可证时系统正确识别为无效状态");
    }

    @Test
    @Order(2)
    @DisplayName("场景2: 只有 case 模块授权时")
    void testCaseModuleOnly() throws Exception {
        System.out.println("\n========== 场景2: 只有 case 模块授权 ==========");

        // 创建只有 case 模块的许可证
        createAndSaveLicense(Arrays.asList("case"), 100);

        licenseService.validateLicense();

        System.out.println("许可证是否有效: " + licenseService.isValid());
        System.out.println("授权模块: " + licenseService.getCurrentLicense().getModules());

        // 验证模块授权
        assertTrue(licenseService.isModuleEnabled("case"), "case 模块应已授权");
        assertFalse(licenseService.isModuleEnabled("ai"), "ai 模块应未授权");
        assertFalse(licenseService.isModuleEnabled("report"), "report 模块应未授权");

        System.out.println("✓ case 模块已授权，其他模块未授权");
    }

    @Test
    @Order(3)
    @DisplayName("场景3: 模拟 @RequireLicense 拦截效果")
    void testRequireLicenseInterceptor() throws Exception {
        System.out.println("\n========== 场景3: @RequireLicense 拦截效果 ==========");

        // 创建只有 case 模块的许可证
        createAndSaveLicense(Arrays.asList("case"), 100);
        licenseService.validateLicense();

        // 模拟访问需要 case 模块的方法
        String[] requiredModules = {"case"};
        boolean canAccess = checkModuleAccess(requiredModules);
        System.out.println("访问需要 case 模块的接口: " + (canAccess ? "允许" : "拒绝"));
        assertTrue(canAccess, "有 case 授权时应允许访问");

        // 模拟访问需要 ai 模块的方法
        requiredModules = new String[]{"ai"};
        canAccess = checkModuleAccess(requiredModules);
        System.out.println("访问需要 ai 模块的接口: " + (canAccess ? "允许" : "拒绝"));
        assertFalse(canAccess, "无 ai 授权时应拒绝访问");

        // 模拟访问需要多个模块的方法
        requiredModules = new String[]{"case", "report"};
        canAccess = checkModuleAccess(requiredModules);
        System.out.println("访问需要 case+report 模块的接口: " + (canAccess ? "允许" : "拒绝"));
        assertFalse(canAccess, "缺少 report 授权时应拒绝访问");

        System.out.println("✓ 模块授权检查正常工作");
    }

    @Test
    @Order(4)
    @DisplayName("场景4: 过期许可证的功能限制")
    void testExpiredLicenseRestriction() throws Exception {
        System.out.println("\n========== 场景4: 过期许可证的功能限制 ==========");

        // 创建过期许可证
        LicenseInfo expiredLicense = LicenseInfo.builder()
                .licenseId("EXPIRED-TEST")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(Arrays.asList("case", "ai", "report"))
                .maxUsers(100)
                .expireDate(LocalDateTime.now().minusDays(1))
                .createTime(LocalDateTime.now().minusYears(1))
                .build();

        // 签名
        signLicense(expiredLicense);

        // 保存
        String licenseJson = objectMapper.writeValueAsString(expiredLicense);
        Files.write(Paths.get(licenseProperties.getFilePath()), licenseJson.getBytes(StandardCharsets.UTF_8));

        licenseService.validateLicense();

        System.out.println("许可证是否有效: " + licenseService.isValid());
        System.out.println("许可证是否过期: " + licenseService.isExpired());
        System.out.println("错误信息: " + licenseService.getValidationErrorMessage());

        assertFalse(licenseService.isValid(), "过期许可证应无效");
        assertTrue(licenseService.isExpired(), "isExpired() 应返回 true");

        // 过期后，即使所有模块都"授权"，isValid() 仍为 false
        assertFalse(licenseService.isModuleEnabled("case"), "过期后模块应不可用");

        System.out.println("✓ 过期许可证正确限制所有功能");
    }

    @Test
    @Order(5)
    @DisplayName("场景5: 机器码不匹配的功能限制")
    void testMachineCodeMismatchRestriction() throws Exception {
        System.out.println("\n========== 场景5: 机器码不匹配的功能限制 ==========");

        // 创建机器码不匹配的许可证
        LicenseInfo wrongMachineLicense = LicenseInfo.builder()
                .licenseId("WRONG-MACHINE")
                .customerName("测试客户")
                .machineCode("WRONGMACHINECODE12345678901234")
                .modules(Arrays.asList("case", "ai"))
                .maxUsers(100)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        // 签名
        signLicense(wrongMachineLicense);

        // 保存
        String licenseJson = objectMapper.writeValueAsString(wrongMachineLicense);
        Files.write(Paths.get(licenseProperties.getFilePath()), licenseJson.getBytes(StandardCharsets.UTF_8));

        licenseService.validateLicense();

        System.out.println("许可证是否有效: " + licenseService.isValid());
        System.out.println("错误代码: " + licenseService.getLastValidationErrorCode());
        System.out.println("错误详情: " + licenseService.getLastValidationDetails());

        assertFalse(licenseService.isValid(), "机器码不匹配时应无效");
        assertEquals("MACHINE_CODE_MISMATCH", licenseService.getLastValidationErrorCode());

        System.out.println("✓ 机器码不匹配正确限制功能");
    }

    @Test
    @Order(6)
    @DisplayName("场景6: 用户数限制检查")
    void testMaxUsersLimit() throws Exception {
        System.out.println("\n========== 场景6: 用户数限制检查 ==========");

        // 创建限制10个用户的许可证
        createAndSaveLicense(Arrays.asList("case"), 10);
        licenseService.validateLicense();

        System.out.println("许可证是否有效: " + licenseService.isValid());
        System.out.println("最大用户数: " + licenseService.getCurrentLicense().getMaxUsers());

        assertEquals(10, licenseService.getCurrentLicense().getMaxUsers());

        // 注意：当前实现只在 LicenseInfo 中存储 maxUsers，
        // 实际的用户数限制需要在用户注册/登录时检查
        System.out.println("✓ 用户数限制已配置（需要在业务层实现检查）");
    }

    // 辅助方法
    private void createAndSaveLicense(java.util.List<String> modules, int maxUsers) throws Exception {
        LicenseInfo license = LicenseInfo.builder()
                .licenseId("TEST-LICENSE")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(maxUsers)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        signLicense(license);

        String licenseJson = objectMapper.writeValueAsString(license);
        Files.write(Paths.get(licenseProperties.getFilePath()), licenseJson.getBytes(StandardCharsets.UTF_8));
    }

    private void signLicense(LicenseInfo license) throws Exception {
        String dataToSign = license.getLicenseId() + "|" +
                license.getCustomerName() + "|" +
                license.getMachineCode() + "|" +
                String.join(",", license.getModules()) + "|" +
                license.getMaxUsers() + "|" +
                license.getExpireDate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        license.setSignature(Base64Utils.encodeToString(signature.sign()));
    }

    private boolean checkModuleAccess(String[] requiredModules) {
        // 模拟 LicenseAspect 的检查逻辑
        if (!licenseService.isValid()) {
            return false;
        }

        for (String module : requiredModules) {
            if (!licenseService.isModuleEnabled(module)) {
                return false;
            }
        }
        return true;
    }
}
