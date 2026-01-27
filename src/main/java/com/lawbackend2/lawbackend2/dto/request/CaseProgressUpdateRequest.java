package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CaseProgressUpdateRequest {
    @NotBlank(message = "案件进度不能为空")
    @Pattern(regexp = "^(FIRST|SECOND|THIRD|FOURTH|FIFTH|SIXTH|SEVENTH)$", message = "案件进度必须是以下值之一：FIRST-第一阶段, SECOND-第二阶段, THIRD-第三阶段, FOURTH-第四阶段, FIFTH-第五阶段, SIXTH-第六阶段, SEVENTH-第七阶段")
    private String caseProgress;
}