package com.eduverse.eduversebe.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {
    private PageUtils() {};

    public static Pageable getPageable(int page, int size, String sortBy, String sortDir) {
        int pageNo = (page < 1) ? 0 : page - 1;

        int pageSize = (size < 1) ? 10 : size;
        if (pageSize > 100) pageSize = 100;

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        return PageRequest.of(pageNo, pageSize, sort);
    }
}
