package com.lawbackend2.lawbackend2.common;

import lombok.Data;

@Data
public class PageResult<T> {
    private Long total;
    private java.util.List<T> list;
    private Integer pageNum;
    private Integer pageSize;

    public PageResult() {
    }

    public PageResult(Long total, java.util.List<T> list) {
        this.total = total;
        this.list = list;
    }

    public PageResult(java.util.List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(Long total, java.util.List<T> list) {
        return new PageResult<>(total, list);
    }

    public static <T> PageResult<T> of(Long total, java.util.List<T> list, Integer pageNum, Integer pageSize) {
        return new PageResult<>(list, total, pageNum, pageSize);
    }
}
