package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.respone.CourseEarningDataResponse;
import com.eduverse.eduversebe.dto.respone.InstructorStatsResponse;
import com.eduverse.eduversebe.dto.respone.MonthlyDataItemResponse;
import com.eduverse.eduversebe.model.Instructor;
import com.eduverse.eduversebe.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository instructorRepository;

    private final OrderService orderService;
    private final ReviewService reviewService;

    public List<String> getInstructorCourseIds(String instructorId) {
        Instructor instructor = instructorRepository.findByUser(instructorId)
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyCourses())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyCourse::getCourseId)
                .toList();
    }

    public List<String> getInstructorStudentIds(String instructorId) {
        Instructor instructor = instructorRepository.findByUser(instructorId)
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyStudents())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyStudent::getStudentId)
                .toList();
    }

    public InstructorStatsResponse getInstructorStats(String instructorId) {
        List<String> courseIds = this.getInstructorCourseIds(instructorId);
        return InstructorStatsResponse.builder()
                .totalCourses(courseIds.size())
                .totalStudents(this.getInstructorStudentIds(instructorId).size())
                .totalOrders(orderService.countCompletedOrdersByCourseIds(courseIds))
                .totalReviews(reviewService.getTotalReviewsOfCourses(courseIds))
                .averageRating(reviewService.getInstructorAvgRating(courseIds))
                .build();
    }

    public List<MonthlyDataItemResponse> getInstructorMonthlyEarning(String instructorId) {
        return orderService.getCoursesMonthlyEarningPast12Months(
                this.getInstructorCourseIds(instructorId)
        );
    }

    public List<CourseEarningDataResponse> getInstructorTop5CourseEarning(String instructorId) {
        return orderService.getTop5EarningCoursesThisMonth(
                this.getInstructorCourseIds(instructorId)
        );
    }
}
