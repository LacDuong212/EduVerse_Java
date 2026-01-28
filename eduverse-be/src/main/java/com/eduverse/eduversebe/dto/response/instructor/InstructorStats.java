package com.eduverse.eduversebe.dto.response.instructor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class InstructorStats {
    @Builder.Default
    Integer totalCourses = 0;

    @Builder.Default
    Integer totalStudents = 0;

    @Builder.Default
    Long totalOrders = 0L;

    @Builder.Default
    Long totalReviews = 0L;

    @Builder.Default
    Double averageRating = 0.0;
}
