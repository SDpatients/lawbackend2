package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CaseProcessStageCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "阶段编号不能为空")
    private Integer stageNum;

    @NotBlank(message = "阶段名称不能为空")
    private String stageName;

    @NotBlank(message = "模块编码不能为空")
    private String moduleCode;

    @NotBlank(message = "模块名称不能为空")
    private String moduleName;

    private String title;

    private String content;

    private LocalDateTime processDate;

    private List<MultipartFile> files;

    @NotBlank(message = "模块特有字段数据不能为空")
    private String fieldData;

    private String status;
}
