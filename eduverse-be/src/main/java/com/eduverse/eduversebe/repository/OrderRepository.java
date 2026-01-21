package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Order;
import com.eduverse.eduversebe.repository.projection.CourseEarningThisMonth;
import com.eduverse.eduversebe.repository.projection.MonthlyEarningProjection;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    @Query(value = "{ 'userId': ?0, 'status': 'COMPLETED', 'courses.courseId': ?1 }", exists = true)
    boolean existsByUserIdAndCourseIdAndStatusCompleted(String userId, String courseId);

    @Query(value = "{ 'userId': ?0, 'status': 'PENDING', 'courses.courseId': ?1, 'expiresAt': { $gt: ?2 } }", exists = true)
    boolean existsByUserIdAndCourseIdAndStatusPending(String userId, String courseId, Instant now);

    @Aggregation(pipeline = {
            "{ $match: { " +
                    "status: 'completed', " +
                    "createdAt: { $gte: ?1, $lte: ?2 } " +
                    "} }",
            "{ $unwind: '$courses' }",
            "{ $match: { " +
                    "'courses.courseId': { $in: ?0 } " +
                    "} }",
            "{ $addFields: { " +
                    "yearMonth: { $dateToParts: { date: '$createdAt', timezone: 'UTC' } } " +
                    "} }",
            "{ $group: { " +
                    "_id: { year: '$yearMonth.year', month: '$yearMonth.month' }, " +
                    "totalEarning: { $sum: '$courses.pricePaid' } " +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "year: '$_id.year', " +
                    "month: '$_id.month', " +
                    "totalEarning: 1 " +
                    "} }",
            "{ $sort: { '_id.year': 1, '_id.month': 1 } }"
    })
    List<MonthlyEarningProjection> getCoursesMonthlyEarningFromRange(
            List<String> courseIds,
            Instant startDay,
            Instant endDay
    );

    @Aggregation(pipeline = {
            "{ $match: { " +
                    "status: 'completed', " +
                    "createdAt: { $gte: ?1, $lt: ?2 } " +
                    "} }",
            "{ $unwind: '$courses' }",
            "{ $match: { 'courses.courseId': { $in: ?0 } } }",
            "{ $group: { " +
                    "_id: '$courses.courseId', " +
                    "totalEarning: { $sum: '$courses.pricePaid' }, " +
                    "totalSales: { $sum: 1 } " +
                    "} }",
            "{ $sort: { totalEarning: -1 } }",
            "{ $limit: ?3 }",
            "{ $lookup: { " +
                    "from: 'courses', " +
                    "localField: '_id', " +
                    "foreignField: '_id', " +
                    "as: 'course' " +
                    "} }",
            "{ $unwind: '$course' }",
            "{ $project: { " +
                    "courseId: '$_id', " +
                    "title: '$course.title', " +
                    "totalEarning: 1, " +
                    "totalSales: 1, " +
                    "_id: 0 " +
                    "} }"
    })
    List<CourseEarningThisMonth> getTopEarningCoursesThisMonth(
            List<String> courseIds,
            Instant startOfMonth,
            Instant startOfNextMonth,
            int limit
    );

    @Aggregation(pipeline = {
            "{ $match: { status: 'completed', 'courses.courseId': { $in: ?0 } } }",
            "{ $count: 'total' }"
    })
    Long countCompletedOrdersByCourseIds(List<String> courseIds);
}
