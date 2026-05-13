package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lawbackend2.lawbackend2.annotation.Mask;
import com.lawbackend2.lawbackend2.annotation.MaskType;
import com.lawbackend2.lawbackend2.util.MaskSerializer;
import lombok.Data;

@Data
public class CreditorSimpleResponse {
    private Long id;
    private Long caseId;
    private String creditorName;

    @Mask(MaskType.ID_CARD)
    @JsonSerialize(using = MaskSerializer.class)
    private String idNumber;
    private String creditorType;
    private String legalRepresentative;
    private String address;
}
