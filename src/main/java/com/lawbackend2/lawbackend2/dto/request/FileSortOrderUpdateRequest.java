package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FileSortOrderUpdateRequest {

    @NotEmpty(message = "文件列表不能为空")
    @Valid
    private List<FileSortOrderItem> files;

    @Data
    public static class FileSortOrderItem {
        @NotNull(message = "文件ID不能为空")
        private Long fileId;

        @NotNull(message = "排序序号不能为空")
        private Integer sortOrder;
    }
}
