package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgreementRecordRequest {

    @NotBlank(message = "协议类型不能为空")
    private String agreementType;

    @NotBlank(message = "协议版本不能为空")
    private String agreementVersion;

    @NotNull(message = "必须同意协议")
    private Boolean agreed;

    private String agreementContent;
}