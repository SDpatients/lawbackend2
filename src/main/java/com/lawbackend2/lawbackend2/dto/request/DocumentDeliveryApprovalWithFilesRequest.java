package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentDeliveryApprovalWithFilesRequest extends DocumentDeliveryApprovalRequest {

    @NotNull(message = "文件列表不能为空")
    private List<MultipartFile> files;

    private List<String> fileDescriptions;
}
