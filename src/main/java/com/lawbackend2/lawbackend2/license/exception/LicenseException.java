package com.lawbackend2.lawbackend2.license.exception;

public class LicenseException extends RuntimeException {

    private String errorCode;
    private String module;
    private String helpText;

    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LicenseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LicenseException(String errorCode, String message, String helpText) {
        super(message);
        this.errorCode = errorCode;
        this.helpText = helpText;
    }

    public LicenseException(String errorCode, String message, String module, String helpText) {
        super(message);
        this.errorCode = errorCode;
        this.module = module;
        this.helpText = helpText;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getModule() {
        return module;
    }

    public String getHelpText() {
        return helpText;
    }

    public static LicenseException moduleNotLicensed(String module) {
        return new LicenseException(
            "MODULE_NOT_LICENSED",
            "当前许可证未授权使用该功能模块: " + module,
            module,
            "请联系软件供应商购买该模块的授权，或升级您的许可证"
        );
    }

    public static LicenseException licenseExpired(String expireDate) {
        return new LicenseException(
            "LICENSE_EXPIRED",
            "许可证已于 " + expireDate + " 过期",
            null,
            "请联系软件供应商续期许可证"
        );
    }

    public static LicenseException licenseNotFound() {
        return new LicenseException(
            "LICENSE_NOT_FOUND",
            "未找到有效的许可证文件",
            null,
            "请上传许可证文件，或联系软件供应商获取许可证"
        );
    }

    public static LicenseException licenseInvalid(String reason) {
        return new LicenseException(
            "LICENSE_INVALID",
            "许可证无效: " + reason,
            null,
            "请检查许可证文件是否正确，或联系软件供应商获取帮助"
        );
    }

    public static LicenseException machineCodeMismatch(String expected, String actual) {
        return new LicenseException(
            "MACHINE_CODE_MISMATCH",
            "许可证与当前服务器不匹配",
            null,
            "该许可证绑定于其他服务器，如需迁移请联系软件供应商重新生成许可证"
        );
    }

    public static LicenseException signatureInvalid() {
        return new LicenseException(
            "SIGNATURE_INVALID",
            "许可证签名验证失败",
            null,
            "许可证可能被篡改或损坏，请重新获取许可证文件"
        );
    }
}
