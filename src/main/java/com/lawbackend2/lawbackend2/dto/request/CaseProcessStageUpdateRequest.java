package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CaseProcessStageUpdateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    private Integer stageNum;

    private String stageName;

    private String moduleCode;

    private String moduleName;

    private String title;

    private String content;

    private LocalDateTime processDate;

    private List<MultipartFile> files;

    private String fieldData;

    private String status;
}
