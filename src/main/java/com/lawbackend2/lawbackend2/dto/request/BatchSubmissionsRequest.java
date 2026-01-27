package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BatchSubmissionsRequest {

    @NotEmpty(message = "任务ID列表不能为空")
    @Size(max = 100, message = "最多支持100个任务ID")
    private List<Long> caseTaskIds;
}
