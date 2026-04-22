package com.lawbackend2.lawbackend2.license.generator;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

@Slf4j
public class RsaKeyGenerator {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("     RSA 密钥对生成工具                  ");
        System.out.println("========================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入密钥长度 (默认 2048): ");
        String keySizeInput = scanner.nextLine().trim();
        int keySize = keySizeInput.isEmpty() ? 2048 : Integer.parseInt(keySizeInput);

        System.out.print("请输入私钥保存路径 (默认 private_key.pem): ");
        String privateKeyPath = scanner.nextLine().trim();
        if (privateKeyPath.isEmpty()) {
            privateKeyPath = "private_key.pem";
        }

        System.out.print("请输入公钥保存路径 (默认 public_key.pem): ");
        String publicKeyPath = scanner.nextLine().trim();
        if (publicKeyPath.isEmpty()) {
            publicKeyPath = "public_key.pem";
        }

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            String privateKeyPem = formatAsPem(
                    "PRIVATE KEY",
                    Base64.getEncoder().encodeToString(privateKey.getEncoded())
            );

            String publicKeyPem = formatAsPem(
                    "PUBLIC KEY",
                    Base64.getEncoder().encodeToString(publicKey.getEncoded())
            );

            try (FileOutputStream fos = new FileOutputStream(privateKeyPath)) {
                fos.write(privateKeyPem.getBytes());
            }

            try (FileOutputStream fos = new FileOutputStream(publicKeyPath)) {
                fos.write(publicKeyPem.getBytes());
            }

            System.out.println();
            System.out.println("密钥对生成成功!");
            System.out.println("========================================");
            System.out.println("私钥已保存到: " + privateKeyPath);
            System.out.println("公钥已保存到: " + publicKeyPath);
            System.out.println("========================================");
            System.out.println();
            System.out.println("【重要提示】");
            System.out.println("1. 私钥 (private_key.pem) 用于生成许可证，请妥善保管，不要泄露!");
            System.out.println("2. 公钥 (public_key.pem) 需要配置到 application.yml 中");
            System.out.println();
            System.out.println("公钥内容 (可直接复制到 application.yml):");
            System.out.println("----------------------------------------");
            System.out.println(publicKeyPem);
            System.out.println("----------------------------------------");

        } catch (Exception e) {
            log.error("生成密钥对失败", e);
            System.out.println("错误: " + e.getMessage());
        }
    }

    private static String formatAsPem(String label, String base64Content) {
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
}
