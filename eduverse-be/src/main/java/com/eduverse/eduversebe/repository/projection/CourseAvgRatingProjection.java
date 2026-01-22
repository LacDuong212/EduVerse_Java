package com.eduverse.eduversebe.repository.projection;

public interface CourseAvgRatingProjection {
    String getCourseId();
    Double getAvgRating();
    Long getTotalReviews();
}
