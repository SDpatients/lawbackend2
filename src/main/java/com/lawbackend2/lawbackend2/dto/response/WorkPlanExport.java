package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class WorkPlanExport {
    private List<WorkPlanExportItem> plans;
    private String fileName;
    private Long totalCount;
}
