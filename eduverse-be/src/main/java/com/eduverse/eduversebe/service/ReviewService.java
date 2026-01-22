package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalLong;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public double getInstructorAvgRating(List<String> courseIds) {
        return OptionalDouble.of(reviewRepository.calOverallAvgRatingOfCourses(courseIds))
                .orElse(0.0);
    }

    public long getTotalReviewsOfCourses(List<String> courseIds) {
        return OptionalLong.of(reviewRepository.getTotalReviewsOfCourses(courseIds))
                .orElse(0L);
    }
}
