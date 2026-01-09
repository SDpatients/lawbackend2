package com.lawbackend2.lawbackend2.common;

import lombok.Data;

@Data
public class PageResult<T> {
    private Long total;
    private java.util.List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, java.util.List<T> list) {
        this.total = total;
        this.list = list;
    }

    public static <T> PageResult<T> of(Long total, java.util.List<T> list) {
        return new PageResult<>(total, list);
    }
}
