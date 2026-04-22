package com.lawbackend2.lawbackend2.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 许可证系统完整测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LicenseSystemTest {

    private static final String TEST_PRIVATE_KEY_FILE = "test_private_key.pem";
    private static final String TEST_PUBLIC_KEY_FILE = "test_public_key.pem";
    private static final String TEST_LICENSE_FILE = "test_license.lic";

    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static String machineCode;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterAll
    static void tearDown() {
        // 清理测试文件
        new File(TEST_PRIVATE_KEY_FILE).delete();
        new File(TEST_PUBLIC_KEY_FILE).delete();
        new File(TEST_LICENSE_FILE).delete();
    }

    @Test
    @Order(1)
    @DisplayName("测试1: RSA密钥对生成")
    void testRsaKeyGeneration() throws Exception {
        System.out.println("\n========== 测试1: RSA密钥对生成 ==========");

        // 生成RSA密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // 保存密钥到文件
        String privateKeyPem = formatAsPem("PRIVATE KEY",
                Base64Utils.encodeToString(privateKey.getEncoded()));
        String publicKeyPem = formatAsPem("PUBLIC KEY",
                Base64Utils.encodeToString(publicKey.getEncoded()));

        Files.write(Paths.get(TEST_PRIVATE_KEY_FILE), privateKeyPem.getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(TEST_PUBLIC_KEY_FILE), publicKeyPem.getBytes(StandardCharsets.UTF_8));

        System.out.println("✓ 私钥已保存到: " + TEST_PRIVATE_KEY_FILE);
        System.out.println("✓ 公钥已保存到: " + TEST_PUBLIC_KEY_FILE);

        // 验证文件存在
        assertTrue(new File(TEST_PRIVATE_KEY_FILE).exists(), "私钥文件应存在");
        assertTrue(new File(TEST_PUBLIC_KEY_FILE).exists(), "公钥文件应存在");

        System.out.println("✓ RSA密钥对生成成功");
    }

    @Test
    @Order(2)
    @DisplayName("测试2: 机器码生成")
    void testMachineCodeGeneration() {
        System.out.println("\n========== 测试2: 机器码生成 ==========");

        machineCode = MachineCodeGenerator.generate();
        String error = MachineCodeGenerator.getLastError();

        System.out.println("机器码: " + machineCode);
        System.out.println("机器码长度: " + machineCode.length());

        if (error != null) {
            System.out.println("警告: " + error);
        }

        // 验证机器码格式
        assertNotNull(machineCode, "机器码不应为空");
        assertEquals(32, machineCode.length(), "机器码应为32位MD5值");
        assertTrue(machineCode.matches("^[0-9A-F]{32}$"), "机器码应为十六进制格式");

        // 验证机器码一致性
        String machineCode2 = MachineCodeGenerator.generate();
        assertEquals(machineCode, machineCode2, "同一台机器生成的机器码应一致");

        System.out.println("✓ 机器码生成成功");
    }

    @Test
    @Order(3)
    @DisplayName("测试3: 许可证生成与签名")
    void testLicenseGeneration() throws Exception {
        System.out.println("\n========== 测试3: 许可证生成与签名 ==========");

        // 创建许可证信息
        List<String> modules = Arrays.asList("case", "fund", "document", "approval");
        LicenseInfo license = LicenseInfo.builder()
                .licenseId("TEST-2026-001")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(modules)
                .maxUsers(100)
                .expireDate(LocalDateTime.now().plusYears(1))
                .createTime(LocalDateTime.now())
                .build();

        // 签名许可证
        String dataToSign = buildDataToSign(license);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        String signatureBase64 = Base64Utils.encodeToString(signature.sign());
        license.setSignature(signatureBase64);

        // 保存许可证文件
        String licenseJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(license);
        Files.write(Paths.get(TEST_LICENSE_FILE), licenseJson.getBytes(StandardCharsets.UTF_8));

        System.out.println("许可证信息:");
        System.out.println("  - 许可证ID: " + license.getLicenseId());
        System.out.println("  - 客户名称: " + license.getCustomerName());
        System.out.println("  - 机器码: " + license.getMachineCode());
        System.out.println("  - 授权模块: " + license.getModules());
        System.out.println("  - 最大用户数: " + license.getMaxUsers());
        System.out.println("  - 到期时间: " + license.getExpireDate());
        System.out.println("  - 签名: " + signatureBase64.substring(0, 50) + "...");

        assertTrue(new File(TEST_LICENSE_FILE).exists(), "许可证文件应存在");

        System.out.println("✓ 许可证生成与签名成功");
    }

    @Test
    @Order(4)
    @DisplayName("测试4: 许可证签名验证")
    void testLicenseSignatureVerification() throws Exception {
        System.out.println("\n========== 测试4: 许可证签名验证 ==========");

        // 读取许可证文件
        String licenseContent = new String(Files.readAllBytes(Paths.get(TEST_LICENSE_FILE)),
                StandardCharsets.UTF_8);
        LicenseInfo license = objectMapper.readValue(licenseContent, LicenseInfo.class);

        // 验证签名
        String dataToVerify = buildDataToSign(license);
        byte[] signatureBytes = Base64Utils.decodeFromString(license.getSignature());

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataToVerify.getBytes(StandardCharsets.UTF_8));
        boolean isValid = signature.verify(signatureBytes);

        assertTrue(isValid, "签名验证应通过");
        System.out.println("✓ 签名验证通过");

        // 测试篡改检测
        LicenseInfo tamperedLicense = objectMapper.readValue(licenseContent, LicenseInfo.class);
        tamperedLicense.setMaxUsers(999);
        String tamperedData = buildDataToSign(tamperedLicense);

        signature.initVerify(publicKey);
        signature.update(tamperedData.getBytes(StandardCharsets.UTF_8));
        boolean isTamperedValid = signature.verify(signatureBytes);

        assertFalse(isTamperedValid, "篡改后的数据签名验证应失败");
        System.out.println("✓ 篡改检测成功");
    }

    @Test
    @Order(5)
    @DisplayName("测试5: 许可证字段验证")
    void testLicenseValidation() throws Exception {
        System.out.println("\n========== 测试5: 许可证字段验证 ==========");

        // 读取许可证
        String licenseContent = new String(Files.readAllBytes(Paths.get(TEST_LICENSE_FILE)),
                StandardCharsets.UTF_8);
        LicenseInfo license = objectMapper.readValue(licenseContent, LicenseInfo.class);

        // 验证字段完整性
        assertDoesNotThrow(() -> license.validate(), "有效许可证应通过验证");
        System.out.println("✓ 许可证字段验证通过");

        // 测试过期检测
        assertFalse(license.isExpired(), "新许可证不应过期");
        System.out.println("✓ 过期检测正常");

        // 测试模块检查
        assertTrue(license.hasModule("case"), "应包含case模块");
        assertTrue(license.hasModule("fund"), "应包含fund模块");
        assertFalse(license.hasModule("ai"), "不应包含ai模块");
        System.out.println("✓ 模块检查正常");

        // 测试剩余天数
        long remainingDays = license.getRemainingDays();
        assertTrue(remainingDays > 360 && remainingDays <= 366, "剩余天数应在360-366之间");
        System.out.println("✓ 剩余天数计算正常: " + remainingDays + " 天");
    }

    @Test
    @Order(6)
    @DisplayName("测试6: 机器码匹配验证")
    void testMachineCodeMatching() throws Exception {
        System.out.println("\n========== 测试6: 机器码匹配验证 ==========");

        // 读取许可证
        String licenseContent = new String(Files.readAllBytes(Paths.get(TEST_LICENSE_FILE)),
                StandardCharsets.UTF_8);
        LicenseInfo license = objectMapper.readValue(licenseContent, LicenseInfo.class);

        // 验证机器码匹配
        assertEquals(machineCode, license.getMachineCode(), "许可证机器码应与当前机器匹配");
        System.out.println("✓ 机器码匹配验证通过");

        // 模拟机器码不匹配的情况
        LicenseInfo wrongMachineLicense = objectMapper.readValue(licenseContent, LicenseInfo.class);
        wrongMachineLicense.setMachineCode("WRONGMACHINECODE12345678901234");

        assertNotEquals(machineCode, wrongMachineLicense.getMachineCode(),
                "错误的机器码不应匹配");
        System.out.println("✓ 机器码不匹配检测正常");
    }

    @Test
    @Order(7)
    @DisplayName("测试7: 过期许可证检测")
    void testExpiredLicenseDetection() {
        System.out.println("\n========== 测试7: 过期许可证检测 ==========");

        // 创建过期许可证
        LicenseInfo expiredLicense = LicenseInfo.builder()
                .licenseId("TEST-EXPIRED-001")
                .customerName("测试客户")
                .machineCode(machineCode)
                .modules(Arrays.asList("case"))
                .maxUsers(10)
                .expireDate(LocalDateTime.now().minusDays(1))
                .createTime(LocalDateTime.now().minusYears(1))
                .signature("dummy-signature")
                .build();

        assertTrue(expiredLicense.isExpired(), "过期许可证应被检测为过期");
        assertEquals(0, expiredLicense.getRemainingDays(), "过期许可证剩余天数应为0");
        System.out.println("✓ 过期许可证检测正常");

        // 验证过期许可证会抛出异常
        assertThrows(LicenseInfo.LicenseValidationException.class,
                () -> expiredLicense.validate(),
                "过期许可证验证应抛出异常");
        System.out.println("✓ 过期许可证验证抛出异常正常");
    }

    @Test
    @Order(8)
    @DisplayName("测试8: 许可证信息完整性验证")
    void testLicenseInfoCompleteness() {
        System.out.println("\n========== 测试8: 许可证信息完整性验证 ==========");

        // 测试缺少必填字段
        LicenseInfo incompleteLicense = new LicenseInfo();

        assertThrows(LicenseInfo.LicenseValidationException.class,
                () -> incompleteLicense.validate(),
                "缺少必填字段的许可证应抛出异常");
        System.out.println("✓ 缺少必填字段检测正常");

        // 测试部分字段
        incompleteLicense.setLicenseId("TEST-001");
        assertThrows(LicenseInfo.LicenseValidationException.class,
                () -> incompleteLicense.validate(),
                "仅包含licenseId仍应抛出异常");
        System.out.println("✓ 部分字段检测正常");
    }

    // 辅助方法
    private String formatAsPem(String label, String base64Content) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(label).append("-----\n");
        int lineLength = 64;
        for (int i = 0; i < base64Content.length(); i += lineLength) {
            int end = Math.min(i + lineLength, base64Content.length());
            sb.append(base64Content.substring(i, end)).append("\n");
        }
        sb.append("-----END ").append(label).append("-----\n");
        return sb.toString();
    }

    private String buildDataToSign(LicenseInfo license) {
        return license.getLicenseId() + "|" +
                license.getCustomerName() + "|" +
                license.getMachineCode() + "|" +
                String.join(",", license.getModules()) + "|" +
                license.getMaxUsers() + "|" +
                license.getExpireDate();
    }
}
