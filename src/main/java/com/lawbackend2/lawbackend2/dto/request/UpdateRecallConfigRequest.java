package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateRecallConfigRequest {

    @NotNull(message = "配置类型不能为空")
    private String configType;

    private Long targetId;

    private Integer recallTimeLimit;

    private Boolean allowRecall;

    private Integer maxRecallTimes;

    private String remark;
}
