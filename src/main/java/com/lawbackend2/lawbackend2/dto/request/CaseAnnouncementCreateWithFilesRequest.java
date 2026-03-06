package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CaseAnnouncementCreateWithFilesRequest {

    @NotNull(message = "案件 ID 不能为空")
    private Long caseId;

    private String caseNumber;

    private String principalOfficer;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "公告类型不能为空")
    private String announcementType;

    @NotNull(message = "文件列表不能为空")
    private List<MultipartFile> files;

    private List<String> fileDescriptions;
}
