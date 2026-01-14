package com.eduverse.eduversebe.dto.respone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String slug;
}
