package com.eduverse.eduversebe.dto.request;

import lombok.Data;

@Data
public class CourseFilterRequest {
    private Integer page = 1;
    private Integer limit = 10;
    private String search = "";

    // Filters
    private String category;
    private String subCategory;
    private String language;
    private String level;
    private String price;
    private String sort;
}
