package com.lawbackend2.lawbackend2.license;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MachineCodeGenerator {

    private static String lastError;
    private static Map<String, String> lastComponents;

    public static String generate() {
        lastError = null;
        lastComponents = new HashMap<>();
        
        try {
            String cpuId = getCpuId();
            String macAddress = getMacAddress();
            String osName = System.getProperty("os.name");
            String userName = System.getProperty("user.name");

            lastComponents.put("cpuId", cpuId);
            lastComponents.put("macAddress", macAddress);
            lastComponents.put("osName", osName);
            lastComponents.put("userName", userName);

            if ("UNKNOWN_CPU".equals(cpuId) && "UNKNOWN_MAC".equals(macAddress)) {
                lastError = "无法获取CPU ID和MAC地址，机器码可能不够稳定";
                log.warn(lastError);
            }

            String combined = cpuId + "|" + macAddress + "|" + osName + "|" + userName;
            log.debug("Machine code source: {}", combined);

            String machineCode = md5(combined);
            log.info("机器码生成成功: {}", machineCode);
            return machineCode;
        } catch (Exception e) {
            lastError = "生成机器码时发生异常: " + e.getMessage();
            log.error("Failed to generate machine code", e);
            return md5(System.getProperty("os.name") + System.currentTimeMillis());
        }
    }

    public static String getLastError() {
        return lastError;
    }

    public static Map<String, String> getLastComponents() {
        return lastComponents != null ? new HashMap<>(lastComponents) : new HashMap<>();
    }

    public static Map<String, Object> getGenerationDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("machineCode", generate());
        details.put("components", getLastComponents());
        details.put("error", lastError);
        details.put("success", lastError == null);
        return details;
    }

    private static String getCpuId() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String cpuId = "";

            if (os.contains("win")) {
                cpuId = executeCommand("wmic cpu get ProcessorId");
                if (cpuId.isEmpty()) {
                    log.warn("Windows系统获取CPU ID失败，尝试备用方法");
                    cpuId = executeCommand("wmic cpu get Name");
                }
            } else if (os.contains("linux")) {
                cpuId = executeCommand("cat /proc/cpuinfo | grep 'serial' | head -1");
                if (cpuId.isEmpty()) {
                    cpuId = executeCommand("cat /proc/cpuinfo | grep 'model name' | head -1");
                }
                if (cpuId.isEmpty()) {
                    cpuId = executeCommand("cat /proc/cpuinfo | grep 'cpu' | head -1");
                }
            } else if (os.contains("mac")) {
                cpuId = executeCommand("sysctl -n machdep.cpu.brand_string");
                if (cpuId.isEmpty()) {
                    cpuId = executeCommand("sysctl -n hw.model");
                }
            } else {
                log.warn("未知的操作系统: {}, 无法获取CPU ID", os);
            }

            String result = cpuId.replaceAll("\\s+", "").trim();
            if (result.isEmpty()) {
                log.warn("CPU ID为空，使用备用标识");
                return "UNKNOWN_CPU";
            }
            return result;
        } catch (Exception e) {
            log.warn("Failed to get CPU ID: {}", e.getMessage());
            return "UNKNOWN_CPU";
        }
    }

    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) {
                log.warn("无法获取网络接口列表");
                return "UNKNOWN_MAC";
            }
            
            int interfaceCount = 0;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                interfaceCount++;
                
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }

                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                    String macStr = sb.toString();
                    log.debug("找到有效MAC地址: {} (接口: {})", macStr, ni.getName());
                    return macStr;
                }
            }
            
            log.warn("检查了 {} 个网络接口，未找到有效的MAC地址", interfaceCount);
        } catch (Exception e) {
            log.warn("Failed to get MAC address: {}", e.getMessage());
        }
        return "UNKNOWN_MAC";
    }

    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process;
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
            } else {
                process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.contains("ProcessorId") && !line.contains("Name")) {
                    output.append(line.trim());
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.debug("命令执行返回非零退出码: {} (命令: {})", exitCode, command);
            }
        } catch (Exception e) {
            log.debug("Command execution failed: {} - {}", command, e.getMessage());
        }
        return output.toString();
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            log.error("MD5计算失败", e);
            return String.valueOf(Math.abs(input.hashCode()));
        }
    }
}
