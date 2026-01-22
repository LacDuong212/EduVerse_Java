package com.eduverse.eduversebe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> data;
    private Pagination pagination;

    @Data
    @Builder
    public static class Pagination {
        private long total;
        private int page;
        private int totalPages;
    }
}
