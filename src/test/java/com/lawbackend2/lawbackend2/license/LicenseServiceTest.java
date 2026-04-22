package com.lawbackend2.lawbackend2.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LicenseService 集成测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LicenseServiceTest {

    @TempDir
    static Path tempDir;

    private static LicenseService licenseService;
    private static LicenseProperties licenseProperties;
    private static String licenseFilePath;
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

        licenseFilePath = licenseProperties.getFilePath();

        // 创建 LicenseService
        licenseService = new LicenseService(licenseProperties);
    }

    @Test
    @Order(1)
    @DisplayName("测试1: LicenseService初始化 - 无许可证文件")
    void testInitWithoutLicenseFile() {
        System.out.println("\n========== 测试1: LicenseService初始化 - 无许可证文件 ==========");

        // 确保许可证文件不存在
        new File(licenseFilePath).delete();

        // 在非严格模式下，应该能正常初始化
        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.init();

        assertFalse(service.isValid(), "无许可证时应返回无效");
        assertNotNull(service.getValidationErrorMessage(), "应有错误信息");
        System.out.println("错误信息: " + service.getValidationErrorMessage());
        System.out.println("✓ 无许可证文件处理正常");
    }

    @Test
    @Order(2)
    @DisplayName("测试2: 生成并保存有效许可证")
    void testGenerateAndSaveValidLicense() throws Exception {
        System.out.println("\n========== 测试2: 生成并保存有效许可证 ==========");

        // 创建许可证
        List<String> modules = Arrays.asList("case", "fund", "document", "approval");
        LicenseInfo license = LicenseInfo.builder()
                .licenseId("SERVICE-TEST-001")
                .customerName("服务测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(100)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        // 签名
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

        // 保存许可证
        String licenseJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(license);
        Files.write(Paths.get(licenseFilePath), licenseJson.getBytes(StandardCharsets.UTF_8));

        System.out.println("许可证已保存到: " + licenseFilePath);
        System.out.println("✓ 许可证生成并保存成功");
    }

    @Test
    @Order(3)
    @DisplayName("测试3: 验证有效许可证")
    void testValidateValidLicense() {
        System.out.println("\n========== 测试3: 验证有效许可证 ==========");

        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.validateLicense();

        System.out.println("验证信息: " + service.getValidationInfo());

        assertTrue(service.isValid(), "有效许可证应验证通过");
        assertNotNull(service.getCurrentLicense(), "应获取到当前许可证");
        assertEquals("SERVICE-TEST-001", service.getCurrentLicense().getLicenseId(),
                "许可证ID应匹配");

        System.out.println("✓ 有效许可证验证通过");
    }

    @Test
    @Order(4)
    @DisplayName("测试4: 模块授权检查")
    void testModuleAuthorization() {
        System.out.println("\n========== 测试4: 模块授权检查 ==========");

        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.validateLicense();

        // 已授权模块
        assertTrue(service.isModuleEnabled("case"), "case模块应已授权");
        assertTrue(service.isModuleEnabled("fund"), "fund模块应已授权");
        assertTrue(service.isModuleEnabled("document"), "document模块应已授权");

        // 未授权模块
        assertFalse(service.isModuleEnabled("ai"), "ai模块应未授权");
        assertFalse(service.isModuleEnabled("chat"), "chat模块应未授权");

        System.out.println("✓ 模块授权检查正常");
    }

    @Test
    @Order(5)
    @DisplayName("测试5: 机器码不匹配检测")
    void testMachineCodeMismatch() throws Exception {
        System.out.println("\n========== 测试5: 机器码不匹配检测 ==========");

        // 创建机器码不匹配的许可证
        List<String> modules = Arrays.asList("case");
        LicenseInfo wrongLicense = LicenseInfo.builder()
                .licenseId("WRONG-MACHINE-001")
                .customerName("测试客户")
                .machineCode("WRONGMACHINECODE12345678901234")
                .modules(modules)
                .maxUsers(10)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        // 签名
        String dataToSign = wrongLicense.getLicenseId() + "|" +
                wrongLicense.getCustomerName() + "|" +
                wrongLicense.getMachineCode() + "|" +
                String.join(",", wrongLicense.getModules()) + "|" +
                wrongLicense.getMaxUsers() + "|" +
                wrongLicense.getExpireDate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        wrongLicense.setSignature(Base64Utils.encodeToString(signature.sign()));

        // 保存错误许可证
        String licenseJson = objectMapper.writeValueAsString(wrongLicense);
        Files.write(Paths.get(licenseFilePath), licenseJson.getBytes(StandardCharsets.UTF_8));

        // 验证
        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.validateLicense();

        assertFalse(service.isValid(), "机器码不匹配时应返回无效");
        assertEquals("MACHINE_CODE_MISMATCH", service.getLastValidationErrorCode(),
                "错误代码应为 MACHINE_CODE_MISMATCH");

        System.out.println("错误信息: " + service.getValidationErrorMessage());
        System.out.println("错误详情: " + service.getLastValidationDetails());
        System.out.println("✓ 机器码不匹配检测正常");
    }

    @Test
    @Order(6)
    @DisplayName("测试6: 过期许可证检测")
    void testExpiredLicenseDetection() throws Exception {
        System.out.println("\n========== 测试6: 过期许可证检测 ==========");

        // 创建过期许可证
        List<String> modules = Arrays.asList("case");
        LicenseInfo expiredLicense = LicenseInfo.builder()
                .licenseId("EXPIRED-001")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(10)
                .expireDate(LocalDateTime.now().minusDays(1))
                .createTime(LocalDateTime.now().minusYears(1))
                .build();

        // 签名
        String dataToSign = expiredLicense.getLicenseId() + "|" +
                expiredLicense.getCustomerName() + "|" +
                expiredLicense.getMachineCode() + "|" +
                String.join(",", expiredLicense.getModules()) + "|" +
                expiredLicense.getMaxUsers() + "|" +
                expiredLicense.getExpireDate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        expiredLicense.setSignature(Base64Utils.encodeToString(signature.sign()));

        // 保存过期许可证
        String licenseJson = objectMapper.writeValueAsString(expiredLicense);
        Files.write(Paths.get(licenseFilePath), licenseJson.getBytes(StandardCharsets.UTF_8));

        // 验证
        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.validateLicense();

        assertFalse(service.isValid(), "过期许可证应返回无效");
        assertTrue(service.isExpired(), "isExpired应返回true");
        assertEquals("LICENSE_EXPIRED", service.getLastValidationErrorCode(),
                "错误代码应为 LICENSE_EXPIRED");

        System.out.println("错误信息: " + service.getValidationErrorMessage());
        System.out.println("✓ 过期许可证检测正常");
    }

    @Test
    @Order(7)
    @DisplayName("测试7: 无效签名检测")
    void testInvalidSignature() throws Exception {
        System.out.println("\n========== 测试7: 无效签名检测 ==========");

        // 创建许可证但使用错误签名
        List<String> modules = Arrays.asList("case");
        LicenseInfo invalidSigLicense = LicenseInfo.builder()
                .licenseId("INVALID-SIG-001")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(10)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .signature("invalid-signature-content")
                .build();

        // 保存
        String licenseJson = objectMapper.writeValueAsString(invalidSigLicense);
        Files.write(Paths.get(licenseFilePath), licenseJson.getBytes(StandardCharsets.UTF_8));

        // 验证
        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.validateLicense();

        assertFalse(service.isValid(), "无效签名应返回无效");
        assertEquals("SIGNATURE_INVALID", service.getLastValidationErrorCode(),
                "错误代码应为 SIGNATURE_INVALID");

        System.out.println("错误信息: " + service.getValidationErrorMessage());
        System.out.println("✓ 无效签名检测正常");
    }

    @Test
    @Order(8)
    @DisplayName("测试8: 空公钥配置测试")
    void testEmptyPublicKey() throws Exception {
        System.out.println("\n========== 测试8: 空公钥配置测试 ==========");

        // 恢复有效许可证
        List<String> modules = Arrays.asList("case", "fund");
        LicenseInfo validLicense = LicenseInfo.builder()
                .licenseId("NO-PUBKEY-TEST-001")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(10)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        // 签名
        String dataToSign = validLicense.getLicenseId() + "|" +
                validLicense.getCustomerName() + "|" +
                validLicense.getMachineCode() + "|" +
                String.join(",", validLicense.getModules()) + "|" +
                validLicense.getMaxUsers() + "|" +
                validLicense.getExpireDate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        validLicense.setSignature(Base64Utils.encodeToString(signature.sign()));

        String licenseJson = objectMapper.writeValueAsString(validLicense);
        Files.write(Paths.get(licenseFilePath), licenseJson.getBytes(StandardCharsets.UTF_8));

        // 配置无公钥
        LicenseProperties noKeyProps = new LicenseProperties();
        noKeyProps.setEnabled(true);
        noKeyProps.setFilePath(licenseFilePath);
        noKeyProps.setPublicKey(""); // 空公钥
        noKeyProps.setStrictMode(false);
        noKeyProps.validate();

        LicenseService service = new LicenseService(noKeyProps);
        service.validateLicense();

        // 无公钥时应该跳过签名验证
        assertTrue(service.isValid(), "无公钥配置时应跳过签名验证");
        System.out.println("✓ 空公钥配置处理正常（跳过签名验证）");
    }

    @Test
    @Order(9)
    @DisplayName("测试9: 禁用许可证验证")
    void testDisabledLicenseCheck() {
        System.out.println("\n========== 测试9: 禁用许可证验证 ==========");

        LicenseProperties disabledProps = new LicenseProperties();
        disabledProps.setEnabled(false);
        disabledProps.setFilePath(licenseFilePath);
        disabledProps.setPublicKey(Base64Utils.encodeToString(publicKey.getEncoded()));
        disabledProps.validate();

        LicenseService service = new LicenseService(disabledProps);
        service.init();

        // 禁用验证时，所有模块都应可用
        assertTrue(service.isModuleEnabled("case"), "禁用验证时case模块应可用");
        assertTrue(service.isModuleEnabled("ai"), "禁用验证时ai模块应可用");
        assertTrue(service.isModuleEnabled("any-module"), "禁用验证时任意模块应可用");

        assertFalse(service.isExpired(), "禁用验证时不应显示过期");

        System.out.println("✓ 禁用许可证验证处理正常");
    }

    @Test
    @Order(10)
    @DisplayName("测试10: 获取验证信息")
    void testGetValidationInfo() throws Exception {
        System.out.println("\n========== 测试10: 获取验证信息 ==========");

        // 恢复有效许可证
        List<String> modules = Arrays.asList("case", "fund", "document", "approval");
        LicenseInfo validLicense = LicenseInfo.builder()
                .licenseId("INFO-TEST-001")
                .customerName("信息测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(100)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        // 签名
        String dataToSign = validLicense.getLicenseId() + "|" +
                validLicense.getCustomerName() + "|" +
                validLicense.getMachineCode() + "|" +
                String.join(",", validLicense.getModules()) + "|" +
                validLicense.getMaxUsers() + "|" +
                validLicense.getExpireDate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        validLicense.setSignature(Base64Utils.encodeToString(signature.sign()));

        String licenseJson = objectMapper.writeValueAsString(validLicense);
        Files.write(Paths.get(licenseFilePath), licenseJson.getBytes(StandardCharsets.UTF_8));

        licenseProperties.setStrictMode(false);
        LicenseService service = new LicenseService(licenseProperties);
        service.validateLicense();

        var info = service.getValidationInfo();
        System.out.println("验证信息: " + info);

        assertNotNull(info, "验证信息不应为空");
        assertTrue((Boolean) info.get("valid"), "valid应为true");
        assertTrue((Boolean) info.get("enabled"), "enabled应为true");
        assertNotNull(info.get("licenseId"), "应包含licenseId");
        assertNotNull(info.get("customerName"), "应包含customerName");
        assertNotNull(info.get("remainingDays"), "应包含remainingDays");

        System.out.println("✓ 验证信息获取正常");
    }
}
