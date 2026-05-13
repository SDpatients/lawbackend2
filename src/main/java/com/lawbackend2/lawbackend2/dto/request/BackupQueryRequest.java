package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.lawbackend2.lawbackend2.common.PageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class BackupQueryRequest extends PageRequest {

    @Parameter(description = "备份状态: PENDING/RUNNING/SUCCESS/FAILED")
    private String status;

    @Parameter(description = "备份类型: FULL/MANUAL")
    private String backupType;

    @Parameter(description = "备份开始时间（起始）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Parameter(description = "备份开始时间（截止）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
}