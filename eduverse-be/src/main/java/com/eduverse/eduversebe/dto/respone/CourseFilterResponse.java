package com.eduverse.eduversebe.dto.respone;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseFilterResponse {

    private List<CategoryDto> categories;
    private List<String> languages;
    private List<String> levels;

    @Data
    @Builder
    public static class CategoryDto {
        private String id;
        private String name;
        private String slug;
    }
}
