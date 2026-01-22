package com.eduverse.eduversebe.dto.respone;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class InstructorStatsResponse {
    Integer totalCourses;
    Integer totalStudents;
    Long totalOrders;
    Long totalReviews;

    Double averageRating;
}
