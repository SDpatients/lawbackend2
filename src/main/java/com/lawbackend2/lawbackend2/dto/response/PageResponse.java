package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private Long total;
    private List<T> list;

    public PageResponse() {
    }

    public PageResponse(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public static <T> PageResponse<T> of(Long total, List<T> list) {
        return new PageResponse<>(total, list);
    }
}
