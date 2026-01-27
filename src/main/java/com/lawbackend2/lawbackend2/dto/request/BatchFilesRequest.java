package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BatchFilesRequest {

    @NotEmpty(message = "提交ID列表不能为空")
    @Size(max = 500, message = "最多支持500个提交ID")
    private List<Long> submissionIds;
}
