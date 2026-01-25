package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.CourseStatus;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.request.UpdateCoursePrivacyRequest;
import com.eduverse.eduversebe.dto.response.*;
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

    private final CourseService courseService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public List<String> getInstructorCourseIds(String instructorId) {
        Instructor instructor = instructorRepository.findByUserId(instructorId)
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyCourses())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyCourse::getCourseId)
                .toList();
    }

    public List<String> getInstructorStudentIds(String instructorId) {
        Instructor instructor = instructorRepository.findByUserId((instructorId))
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyStudents())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyStudent::getStudentId)
                .toList();
    }

    public boolean existInstructorById(String instructorId) {
        return instructorRepository.existsById(instructorId);
    }

    public boolean checkCourseOwnership(String instructorId, String courseId) {
        return courseService.checkCourseOwnership(courseId, instructorId);
    }

    public InstructorStatsResponse getInstructorStats(String instructorId) {
        List<String> courseIds = this.getInstructorCourseIds(instructorId);
        if (courseIds.isEmpty()) return InstructorStatsResponse.builder().build();

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

    public PageResponse<InstructorCoursesListItemResponse> getInstructorCoursesListMatchCriteria(
            String instructorId,
            int page,
            int limit,
            String searchKey,
            String sortKey
    ) {
        return courseService.getCoursesMatchCriteriaForInstructor(
                instructorId,
                searchKey,
                sortKey,
                Math.max(page, 1),
                Math.max(limit, 5)
        );
    }

    public boolean changeCoursePrivacy(String courseId, UpdateCoursePrivacyRequest request) {
        return courseService.updateCoursePrivacy(courseId, request.privacy());
    }

    public InstructorCoursesStatsResponse getInstructorCoursesStats(String instructorId) {
        return InstructorCoursesStatsResponse.builder()
                .totalCourses(courseService.countInstructorCourses(instructorId))
                .totalLive(courseService.countCoursesWithStatusForInstructor(instructorId, CourseStatus.Live))
                .totalPending(courseService.countCoursesWithStatusForInstructor(instructorId, CourseStatus.Pending))
                .totalRejected(courseService.countCoursesWithStatusForInstructor(instructorId, CourseStatus.Rejected))
                .totalBlocked(courseService.countCoursesWithStatusForInstructor(instructorId, CourseStatus.Blocked))
                .build();
    }
}
