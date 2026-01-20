package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FundAccountExport {
    private List<FundAccountExportItem> accounts;
    private String fileName;
    private Long totalCount;
}
