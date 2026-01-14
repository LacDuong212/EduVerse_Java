package com.eduverse.eduversebe.dto.respone;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeContentResponse {
    private List<CourseResponse> newest;
    private List<CourseResponse> bestSellers;
    private List<CourseResponse> topRated;
    private List<CourseResponse> biggestDiscounts;
}
