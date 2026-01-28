package com.eduverse.eduversebe.dto.response.instructor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CoursesListItem {
    String id;
    String title;
    String subtitle;
    String image;
    String status;
    Double price;
    Double discountPrice;
    Boolean enableDiscount;
    Boolean isPrivate;

    Double averageRating;
    Long studentsEnrolled;
    Integer sectionsCount;
    Integer lecturesCount;

    Instant updatedAt;
}
