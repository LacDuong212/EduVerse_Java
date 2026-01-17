package com.eduverse.eduversebe.dto.respone;

import com.eduverse.eduversebe.common.globalEnums.CourseLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private String id;
    private String title;
    private String subtitle;
    private String thumbnail;

    private Double price;
    private Double discountPrice;
    private Boolean isFree;

    private CategoryInfo category;

    private String instructorId;
    private String instructorName;
    private String instructorAvatar;

    @JsonProperty("rating")
    private Double averageRating;

    private Integer ratingCount;
    private Integer studentsEnrolled;
    private Integer lecturesCount;

    private CourseLevel level;
    private Double duration;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private String id;
        private String name;
    }
}
