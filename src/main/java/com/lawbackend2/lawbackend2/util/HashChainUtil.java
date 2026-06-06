package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.entity.AuditLog;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;

@Component
public class HashChainUtil {

    private static final String GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generateHash(AuditLog auditLog, String previousHash) {
        String dataToHash = buildHashData(auditLog, previousHash);
        return applySHA256(dataToHash);
    }

    public String generateGenesisHash() {
        return GENESIS_HASH;
    }

    public boolean verifyHashChain(AuditLog currentLog, AuditLog previousLog) {
        if (previousLog == null) {
            return GENESIS_HASH.equals(currentLog.getPreviousHash());
        }

        String expectedPreviousHash = previousLog.getHashValue();
        if (expectedPreviousHash == null || !expectedPreviousHash.equals(currentLog.getPreviousHash())) {
            return false;
        }

        String expectedHash = generateHash(currentLog, expectedPreviousHash);
        return expectedHash.equals(currentLog.getHashValue());
    }

    public boolean verifyLogIntegrity(AuditLog auditLog) {
        if (auditLog.getHashValue() == null || auditLog.getHashValue().isEmpty()) {
            return false;
        }

        String previousHash = auditLog.getPreviousHash();
        if (previousHash == null) {
            previousHash = GENESIS_HASH;
        }

        String recalculatedHash = generateHash(auditLog, previousHash);
        return recalculatedHash.equals(auditLog.getHashValue());
    }

    public String generateDigitalSignature(AuditLog auditLog, String privateKey) {
        String dataToSign = buildSignatureData(auditLog);
        return applyRSAWithPrivateKey(dataToSign, privateKey);
    }

    public boolean verifyDigitalSignature(AuditLog auditLog, String publicKey) {
        if (auditLog.getDigitalSignature() == null || auditLog.getDigitalSignature().isEmpty()) {
            return false;
        }

        String dataToVerify = buildSignatureData(auditLog);
        return verifyRSAWithPublicKey(dataToVerify, auditLog.getDigitalSignature(), publicKey);
    }

    public String generateChainSequence(AuditLog previousLog) {
        if (previousLog == null || previousLog.getChainSequence() == null) {
            return "1";
        }
        return String.valueOf(previousLog.getChainSequence() + 1);
    }

    private String buildHashData(AuditLog auditLog, String previousHash) {
        StringBuilder sb = new StringBuilder();
        sb.append(auditLog.getId() != null ? auditLog.getId().toString() : "");
        sb.append("|");
        sb.append(auditLog.getUserId() != null ? auditLog.getUserId().toString() : "");
        sb.append("|");
        sb.append(auditLog.getUserAccount() != null ? auditLog.getUserAccount() : "");
        sb.append("|");
        sb.append(auditLog.getUserName() != null ? auditLog.getUserName() : "");
        sb.append("|");
        sb.append(auditLog.getModule() != null ? auditLog.getModule() : "");
        sb.append("|");
        sb.append(auditLog.getModuleName() != null ? auditLog.getModuleName() : "");
        sb.append("|");
        sb.append(auditLog.getOperationType() != null ? auditLog.getOperationType() : "");
        sb.append("|");
        sb.append(auditLog.getOperationName() != null ? auditLog.getOperationName() : "");
        sb.append("|");
        sb.append(auditLog.getBusinessType() != null ? auditLog.getBusinessType() : "");
        sb.append("|");
        sb.append(auditLog.getBusinessId() != null ? auditLog.getBusinessId().toString() : "");
        sb.append("|");
        sb.append(auditLog.getBusinessName() != null ? auditLog.getBusinessName() : "");
        sb.append("|");
        sb.append(auditLog.getRequestMethod() != null ? auditLog.getRequestMethod() : "");
        sb.append("|");
        sb.append(auditLog.getRequestUrl() != null ? auditLog.getRequestUrl() : "");
        sb.append("|");
        sb.append(auditLog.getRequestParams() != null ? auditLog.getRequestParams() : "");
        sb.append("|");
        sb.append(auditLog.getDataBefore() != null ? auditLog.getDataBefore() : "");
        sb.append("|");
        sb.append(auditLog.getDataAfter() != null ? auditLog.getDataAfter() : "");
        sb.append("|");
        sb.append(auditLog.getStatus() != null ? auditLog.getStatus() : "");
        sb.append("|");
        sb.append(auditLog.getErrorMessage() != null ? auditLog.getErrorMessage() : "");
        sb.append("|");
        sb.append(auditLog.getIpAddress() != null ? auditLog.getIpAddress() : "");
        sb.append("|");
        sb.append(auditLog.getCreateTime() != null ? auditLog.getCreateTime().format(DATE_FORMATTER) : "");
        sb.append("|");
        sb.append(previousHash != null ? previousHash : "");

        return sb.toString();
    }

    private String buildSignatureData(AuditLog auditLog) {
        StringBuilder sb = new StringBuilder();
        sb.append(auditLog.getId() != null ? auditLog.getId().toString() : "");
        sb.append("|");
        sb.append(auditLog.getHashValue() != null ? auditLog.getHashValue() : "");
        sb.append("|");
        sb.append(auditLog.getUserId() != null ? auditLog.getUserId().toString() : "");
        sb.append("|");
        sb.append(auditLog.getOperationType() != null ? auditLog.getOperationType() : "");
        sb.append("|");
        sb.append(auditLog.getBusinessType() != null ? auditLog.getBusinessType() : "");
        sb.append("|");
        sb.append(auditLog.getBusinessId() != null ? auditLog.getBusinessId().toString() : "");
        sb.append("|");
        sb.append(auditLog.getCreateTime() != null ? auditLog.getCreateTime().format(DATE_FORMATTER) : "");
        return sb.toString();
    }

    private String applySHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String applyRSAWithPrivateKey(String data, String privateKey) {
        return applySHA256(data + "|" + privateKey);
    }

    private boolean verifyRSAWithPublicKey(String data, String signature, String publicKey) {
        String expectedSignature = applySHA256(data + "|" + publicKey);
        return expectedSignature.equals(signature);
    }
}
