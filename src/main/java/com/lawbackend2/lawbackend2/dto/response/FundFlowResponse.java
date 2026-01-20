package com.lawbackend2.lawbackend2.dto.response;

import com.lawbackend2.lawbackend2.entity.FundFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FundFlowResponse extends FundFlow {
    private String operatorRealName;
}