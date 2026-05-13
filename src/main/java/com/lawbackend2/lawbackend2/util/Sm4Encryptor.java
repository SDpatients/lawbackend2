package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.license.MachineCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class Sm4Encryptor {

    private final String seedKey;
    private Sm4Util sm4Util;

    public Sm4Encryptor(@Value("${sm4.seed-key:LawBankruptSM4SeedKey2024!#}") String seedKey) {
        this.seedKey = seedKey;
    }

    @PostConstruct
    public void init() {
        String currentMachineCode = MachineCodeGenerator.generate();
        byte[] derivedKey = deriveKey(seedKey, currentMachineCode);
        String keyHex = bytesToHex(derivedKey);
        this.sm4Util = new Sm4Util(keyHex);
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        ensureInitialized();
        return sm4Util.encrypt(plainText);
    }

    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        ensureInitialized();
        try {
            return sm4Util.decrypt(cipherText);
        } catch (Exception e) {
            return cipherText;
        }
    }

    private void ensureInitialized() {
        if (sm4Util == null) {
            init();
        }
    }

    static byte[] deriveKey(String seed, String machineCode) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(seed.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] derived = mac.doFinal(machineCode.getBytes(StandardCharsets.UTF_8));
            byte[] key = new byte[16];
            System.arraycopy(derived, 0, key, 0, 16);
            return key;
        } catch (Exception e) {
            byte[] key = new byte[16];
            byte[] seedBytes = seed.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(seedBytes, 0, key, 0, Math.min(seedBytes.length, 16));
            return key;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}