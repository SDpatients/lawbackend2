package com.lawbackend2.lawbackend2.license.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lawbackend2.lawbackend2.license.LicenseInfo;
import com.lawbackend2.lawbackend2.license.MachineCodeGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Slf4j
public class LicenseGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<String> VALID_MODULES = List.of(
            "case", "fund", "document", "creditor", 
            "approval", "report", "ai", "chat"
    );

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private static void setupConsoleEncoding() {
        try {
            String osName = System.getProperty("os.name", "").toLowerCase();
            if (osName.contains("windows")) {
                String consoleEncoding = System.getProperty("console.encoding");
                if (consoleEncoding == null || !consoleEncoding.equalsIgnoreCase("UTF-8")) {
                    System.setProperty("console.encoding", "UTF-8");
                    try {
                        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
                        System.setErr(new java.io.PrintStream(System.err, true, "UTF-8"));
                    } catch (Exception e) {
                        log.warn("无法设置控制台输出编码为UTF-8", e);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("设置控制台编码失败", e);
        }
    }

    private static Scanner createScanner() {
        String consoleEncoding = System.getProperty("console.encoding");
        Charset charset;
        if (consoleEncoding != null) {
            try {
                charset = Charset.forName(consoleEncoding);
            } catch (Exception e) {
                charset = StandardCharsets.UTF_8;
            }
        } else {
            String osName = System.getProperty("os.name", "").toLowerCase();
            if (osName.contains("windows")) {
                charset = StandardCharsets.UTF_8;
            } else {
                charset = Charset.defaultCharset();
            }
        }
        return new Scanner(new BufferedReader(
            new InputStreamReader(System.in, charset)));
    }

    public static void main(String[] args) {
        setupConsoleEncoding();
        
        System.out.println("========================================");
        System.out.println("     法律案件管理系统 - 许可证生成工具     ");
        System.out.println("========================================");
        System.out.println();

        Scanner scanner = createScanner();

        System.out.println("请选择操作模式:");
        System.out.println("1. 生成新的许可证");
        System.out.println("2. 查看当前机器码");
        System.out.println("3. 验证许可证文件");
        System.out.println();
        System.out.print("请输入选项 (1/2/3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                generateNewLicense(scanner);
                break;
            case "2":
                showMachineCode();
                break;
            case "3":
                verifyLicenseFile(scanner);
                break;
            default:
                System.out.println("无效的选项: " + choice);
                System.out.println("请重新运行程序并输入 1、2 或 3");
        }
    }

    private static void showMachineCode() {
        System.out.println();
        System.out.println("正在获取服务器机器码...");
        
        String machineCode = MachineCodeGenerator.generate();
        String error = MachineCodeGenerator.getLastError();
        
        System.out.println();
        System.out.println("当前服务器机器码:");
        System.out.println("========================================");
        System.out.println(machineCode);
        System.out.println("========================================");
        
        if (error != null) {
            System.out.println();
            System.out.println("警告: " + error);
        }
        
        System.out.println();
        System.out.println("提示: 请将此机器码发送给软件供应商以获取许可证");
    }

    private static void verifyLicenseFile(Scanner scanner) {
        try {
            System.out.println();
            System.out.print("请输入许可证文件路径: ");
            String filePath = scanner.nextLine().trim();
            
            if (filePath.isEmpty()) {
                System.out.println("错误: 文件路径不能为空");
                return;
            }
            
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("错误: 文件不存在: " + file.getAbsolutePath());
                return;
            }
            
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            LicenseInfo license = objectMapper.readValue(content, LicenseInfo.class);
            
            System.out.println();
            System.out.println("许可证信息:");
            System.out.println("========================================");
            System.out.println("许可证编号: " + license.getLicenseId());
            System.out.println("客户名称: " + license.getCustomerName());
            System.out.println("机器码: " + license.getMachineCode());
            System.out.println("授权模块: " + license.getModules());
            System.out.println("最大用户数: " + license.getMaxUsers());
            System.out.println("创建时间: " + license.getCreateTime());
            System.out.println("到期时间: " + license.getExpireDate());
            System.out.println("是否过期: " + (license.isExpired() ? "是" : "否"));
            if (!license.isExpired()) {
                System.out.println("剩余天数: " + license.getRemainingDays() + " 天");
            }
            System.out.println("========================================");
            
            try {
                license.validate();
                System.out.println();
                System.out.println("许可证格式验证: 通过");
            } catch (LicenseInfo.LicenseValidationException e) {
                System.out.println();
                System.out.println("许可证格式验证: 失败");
                System.out.println("错误原因: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println();
            System.out.println("错误: 无法读取或解析许可证文件");
            System.out.println("原因: " + e.getMessage());
            System.out.println();
            System.out.println("请确认:");
            System.out.println("  1. 文件路径是否正确");
            System.out.println("  2. 文件是否是有效的JSON格式");
            System.out.println("  3. 文件是否包含完整的许可证信息");
        }
    }

    private static void generateNewLicense(Scanner scanner) {
        try {
            System.out.println();
            System.out.println("请输入许可证信息:");
            System.out.println();

            String licenseId = readNonEmptyInput(scanner, "许可证编号 (如: LAW-2026-001): ", "许可证编号不能为空");
            String customerName = readNonEmptyInput(scanner, "客户名称: ", "客户名称不能为空");
            String machineCode = readNonEmptyInput(scanner, "机器码: ", "机器码不能为空");

            System.out.println();
            System.out.println("可选模块列表:");
            System.out.println("- case        : 案件管理");
            System.out.println("- fund        : 资金管理");
            System.out.println("- document    : 文档管理");
            System.out.println("- creditor    : 债权人管理");
            System.out.println("- approval    : 审批流程");
            System.out.println("- report      : 统计报表");
            System.out.println("- ai          : AI助手");
            System.out.println("- chat        : 实时聊天");
            System.out.println();
            
            List<String> modules;
            while (true) {
                System.out.print("授权模块 (用逗号分隔, 输入 ALL 表示全部): ");
                String modulesInput = scanner.nextLine().trim();
                
                if ("ALL".equalsIgnoreCase(modulesInput)) {
                    modules = new ArrayList<>(VALID_MODULES);
                    break;
                }
                
                modules = new ArrayList<>();
                boolean hasInvalid = false;
                for (String module : modulesInput.split(",")) {
                    String trimmed = module.trim().toLowerCase();
                    if (trimmed.isEmpty()) continue;
                    
                    if (!VALID_MODULES.contains(trimmed)) {
                        System.out.println("警告: 无效的模块名称: " + trimmed);
                        System.out.println("有效模块: " + String.join(", ", VALID_MODULES));
                        hasInvalid = true;
                    } else {
                        modules.add(trimmed);
                    }
                }
                
                if (!hasInvalid && !modules.isEmpty()) {
                    break;
                }
                
                if (modules.isEmpty()) {
                    System.out.println("错误: 请至少选择一个模块");
                }
            }

            int maxUsers = readPositiveInt(scanner, "最大用户数: ", "最大用户数必须是正整数");

            System.out.print("有效期(年, 输入 9999 表示永久): ");
            int years;
            try {
                years = Integer.parseInt(scanner.nextLine().trim());
                if (years <= 0 && years != 9999) {
                    System.out.println("警告: 有效期必须是正数，已自动设置为 1 年");
                    years = 1;
                }
            } catch (NumberFormatException e) {
                System.out.println("警告: 无效的数字格式，已自动设置为 1 年");
                years = 1;
            }
            
            LocalDateTime expireDate;
            if (years >= 9999) {
                expireDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
            } else {
                expireDate = LocalDateTime.now().plusYears(years);
            }

            System.out.println();
            System.out.print("私钥文件路径 (默认: private_key.pem): ");
            String keyInput = scanner.nextLine().trim();
            
            if (keyInput.isEmpty()) {
                keyInput = "private_key.pem";
            }

            PrivateKey privateKey = loadPrivateKeyWithValidation(keyInput);

            LicenseInfo license = LicenseInfo.builder()
                    .licenseId(licenseId)
                    .customerName(customerName)
                    .machineCode(machineCode)
                    .modules(modules)
                    .maxUsers(maxUsers)
                    .expireDate(expireDate)
                    .createTime(LocalDateTime.now())
                    .build();

            String signature = signLicense(license, privateKey);
            license.setSignature(signature);

            String licenseJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(license);

            System.out.println();
            System.out.println("许可证生成成功!");
            System.out.println("========================================");
            System.out.println(licenseJson);
            System.out.println("========================================");

            System.out.println();
            System.out.print("保存到文件 (默认: license.lic): ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                filePath = "license.lic";
            }

            Files.writeString(Paths.get(filePath), licenseJson, StandardCharsets.UTF_8);
            System.out.println();
            System.out.println("许可证已保存到: " + Paths.get(filePath).toAbsolutePath());
            System.out.println();
            System.out.println("请将此许可证文件发送给客户，客户需将其放置在配置的路径下");

        } catch (Exception e) {
            log.error("生成许可证失败", e);
            System.out.println();
            System.out.println("错误: " + e.getMessage());
            System.out.println();
            System.out.println("可能的原因:");
            if (e.getMessage() != null) {
                if (e.getMessage().contains("private key")) {
                    System.out.println("  - 私钥文件格式错误或损坏");
                    System.out.println("  - 请确认使用的是 PKCS#8 格式的私钥");
                } else if (e.getMessage().contains("File not found")) {
                    System.out.println("  - 私钥文件不存在");
                    System.out.println("  - 请确认文件路径正确");
                }
            }
            System.out.println("  - 输入参数格式错误");
            System.out.println("  - 文件写入权限问题");
        }
    }

    private static String readNonEmptyInput(Scanner scanner, String prompt, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("错误: " + errorMsg);
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("错误: " + errorMsg);
            } catch (NumberFormatException e) {
                System.out.println("错误: 请输入有效的数字");
            }
        }
    }

    private static PrivateKey loadPrivateKeyWithValidation(String keyInput) throws Exception {
        File keyFile = new File(keyInput);
        
        if (keyFile.exists()) {
            System.out.println("正在读取私钥文件: " + keyFile.getAbsolutePath());
            
            if (keyFile.length() == 0) {
                throw new Exception("私钥文件为空: " + keyFile.getAbsolutePath());
            }
            
            String keyContent = Files.readString(keyFile.toPath(), StandardCharsets.UTF_8);
            
            if (!keyContent.contains("PRIVATE KEY") && !keyContent.matches("^[A-Za-z0-9+/]+=*$")) {
                throw new Exception("私钥文件格式错误，请确认是有效的私钥文件");
            }
            
            return loadPrivateKey(keyContent);
        } else {
            System.out.println("文件不存在: " + keyFile.getAbsolutePath());
            System.out.println("尝试作为Base64私钥解析...");
            
            if (keyInput.length() < 100) {
                throw new Exception("私钥内容太短，可能不是有效的私钥");
            }
            
            return loadPrivateKey(keyInput);
        }
    }

    private static String signLicense(LicenseInfo license, PrivateKey privateKey) throws Exception {
        String dataToSign = license.getLicenseId() + "|" +
                license.getCustomerName() + "|" +
                license.getMachineCode() + "|" +
                String.join(",", license.getModules()) + "|" +
                license.getMaxUsers() + "|" +
                license.getExpireDate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        String keyContent = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        if (keyContent.isEmpty()) {
            throw new Exception("私钥内容为空");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(keyContent);
        } catch (IllegalArgumentException e) {
            throw new Exception("私钥不是有效的Base64编码格式");
        }

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        try {
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new Exception("私钥格式错误，请使用PKCS#8格式的私钥。如果是PKCS#1格式，请先转换");
        }
    }
}
