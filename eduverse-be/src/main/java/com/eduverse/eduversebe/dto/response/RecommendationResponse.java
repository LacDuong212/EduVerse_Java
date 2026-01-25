package com.eduverse.eduversebe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationResponse {
    private boolean success;
    private String debugSource;
    private List<CourseResponse> courses;
}
