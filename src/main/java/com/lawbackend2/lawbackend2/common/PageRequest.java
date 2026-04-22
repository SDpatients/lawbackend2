package com.lawbackend2.lawbackend2.common;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public Integer getPage() {
        return pageNum;
    }

    public Integer getSize() {
        return pageSize;
    }
}
