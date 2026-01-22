package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Review;
import com.eduverse.eduversebe.repository.projection.CourseAvgRatingProjection;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    @Aggregation(pipeline = {
            "{ $match: { " +
                    "courseId: { $in: ?0 }, " +
                    "isDeleted: false " +
                    "} }",
            // avg per course
            "{ $group: { " +
                    "_id: '$courseId', " +
                    "avgRating: { $avg: '$rating' } " +
                    "} }",
            // avg of course averages
            "{ $group: { " +
                    "_id: null, " +
                    "overallAvgRating: { $avg: '$avgRating' } " +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "overallAvgRating: { $round: ['$overallAvgRating', 2] } " +
                    "} }"
    })
    Double calOverallAvgRatingOfCourses(List<String> courseIds);

    @Aggregation(pipeline = {
            "{ $match: { " +
                    "courseId: { $in: ?0 }, " +
                    "isDeleted: false " +
                    "} }",
            // totalReviews per course
            "{ $group: { " +
                    "_id: '$courseId', " +
                    "totalReviews: { $sum: 1 } " +
                    "} }",
            // sum of all courses' totalReviews
            "{ $group: { " +
                    "_id: null, " +
                    "totalReviews: { $sum: '$totalReviews' } " +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "totalReviews: 1 " +
                    "} }"
    })
    Long getTotalReviewsOfCourses(List<String> courseIds);

    @Aggregation(pipeline = {
            "{ $match: { " +
                    "courseId: { $in: ?0 }, " +
                    "isDeleted: false " +
                    "} }",
            "{ $group: { " +
                    "_id: '$courseId', " +
                    "avgRating: { $avg: '$rating' }, " +
                    "totalReviews: { $sum: 1 } " +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "courseId: '$_id', " +
                    "avgRating: { $round: ['$avgRating', 2] }, " +
                    "totalReviews: 1 " +
                    "} }"
    })
    List<CourseAvgRatingProjection> getCoursesAverageRating(List<String> courseIds);

}
