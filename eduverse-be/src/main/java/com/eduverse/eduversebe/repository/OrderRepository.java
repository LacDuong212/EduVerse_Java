package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    @Query(value = "{ 'userId': ?0, 'status': 'COMPLETED', 'courses.courseId': ?1 }", exists = true)
    boolean existsByUserIdAndCourseIdAndStatusCompleted(String userId, String courseId);

    @Query(value = "{ 'userId': ?0, 'status': 'PENDING', 'courses.courseId': ?1, 'expiresAt': { $gt: ?2 } }", exists = true)
    boolean existsByUserIdAndCourseIdAndStatusPending(String userId, String courseId, Instant now);
}
