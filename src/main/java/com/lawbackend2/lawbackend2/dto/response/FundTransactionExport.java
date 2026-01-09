package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FundTransactionExport {

    private List<FundTransactionExportItem> transactions;
    private String fileName;
    private Long totalCount;
}
