package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.common.globalEnums.CourseStatus;
import com.eduverse.eduversebe.model.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    List<Course> findByStatusAndIsPrivateFalseAndIsDeletedFalse(CourseStatus status, Pageable pageable);

    @Query(value = "{ 'status': ?0, 'isPrivate': false, 'isDeleted': false }", sort = "{ 'rating.average': -1, 'rating.count': -1 }")
    List<Course> findTopRatedCourses(CourseStatus status, Pageable pageable);


    @Aggregation(pipeline = {
            "{ '$match': { 'status': ?0, 'isPrivate': false, 'isDeleted': false, 'discountPrice': { '$ne': null } } }",
            "{ '$addFields': { 'discountAmount': { '$subtract': ['$price', '$discountPrice'] } } }",
            "{ '$sort': { 'discountAmount': -1 } }"
    })
    List<Course> findBiggestDiscounts(CourseStatus status, Pageable pageable);

    List<Course> findTop8ByIsPrivateFalseAndIsDeletedFalseAndStatusOrderByStudentsEnrolledDesc(CourseStatus status);

    List<Course> findAllByIsPrivateFalseAndIsDeletedFalseAndStatus(CourseStatus status);

    boolean existsByIdAndInstructor_Ref(String id, String instructorId);

    Course findByIdAndInstructor_RefAndIsDeletedFalse(String id, String instructorId);

    Optional<Course> findByIdAndStatus(String id, CourseStatus status);
}
