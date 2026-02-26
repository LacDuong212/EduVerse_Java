package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiPaths;
import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.UpdateCoursePrivacyRequest;
import com.eduverse.eduversebe.dto.request.UploadVideoRequest;
import com.eduverse.eduversebe.dto.response.instructor.CourseData;
import com.eduverse.eduversebe.dto.response.instructor.InstructorStats;
import com.eduverse.eduversebe.service.*;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InstructorController {

    private final CourseService courseService;
    private final InstructorService instructorService;
    private final RevenueService revenueService;
    private final VideoService videoService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping(ApiPaths.Instructor.CHARTS + "/earning")
    public ResponseEntity<?> getMonthlyEarning(@AuthenticationPrincipal(expression = "id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MONTHLY_EARNING_SUCCESS,
                revenueService.getCoursesMonthlyEarningPast12Months(
                        instructorService.getInstructorCourseIds(userId)
                )
        ));
    }

    @GetMapping(ApiPaths.Instructor.CHARTS + "/top-5-courses")
    public ResponseEntity<?> getTop5Courses(@AuthenticationPrincipal(expression = "id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_TOP_5_COURSES_SUCCESS,
                revenueService.getTop5EarningCoursesThisMonth(
                        instructorService.getInstructorCourseIds(userId)
                )
        ));
    }

    @PostMapping(ApiPaths.Instructor.MY_COURSES)
    public ResponseEntity<?> createNewDraftCourse(@AuthenticationPrincipal(expression = "id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.CREATE_COURSE_SUCCESS,
                courseService.createDraft(userId)
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_COURSES + "/list")
    public ResponseEntity<?> getCoursesList(@AuthenticationPrincipal(expression = "id") String userId,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "5") Integer limit,
                                            @RequestParam(defaultValue = "") String search,
                                            @RequestParam(defaultValue = "") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_COURSES_LIST_SUCCESS,
                courseService.getCoursesMatchCriteriaForInstructor(
                        userId,
                        search,
                        sort,
                        Math.max(page, 1),
                        Math.max(limit, 5)
                )
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_COURSES + "/stats")
    public ResponseEntity<?> getCoursesStats(@AuthenticationPrincipal(expression = "id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_COURSES_STATS_SUCCESS,
                courseService.getInstructorCoursesStats(userId)
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_COURSES + "/{courseId}")
    public ResponseEntity<?> getCourseData(@AuthenticationPrincipal(expression = "id") String userId,
                                           @PathVariable @NonNull String courseId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_INSTRUCTOR_COURSE_DATA_SUCCESS,
                courseService.getInstructorCourseData(userId, courseId)
        ));
    }

    @PatchMapping(ApiPaths.Instructor.MY_COURSES + "/{courseId}")
    @PreAuthorize("@courseSecurity.isOwner(#courseId, principal.id)")
    public ResponseEntity<?> patchCourse(@PathVariable @NonNull String courseId,
                                              @RequestBody CourseData courseData) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.UPDATE_COURSE_SUCCESS,
                courseService.updateInstructorCourse(courseId, courseData)
        ));
    }

    @PatchMapping(ApiPaths.Instructor.MY_COURSES + "/{courseId}/draft")
    @PreAuthorize("@courseSecurity.isOwner(#courseId, principal.id)")
    public ResponseEntity<?> patchDraftCourse(@PathVariable @NonNull String courseId,
                                              @RequestBody CourseData courseData) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.UPDATE_DRAFT_COURSE_SUCCESS,
                courseService.updateInstructorDraft(courseId, courseData)
        ));
    }

    @PatchMapping(ApiPaths.Instructor.MY_COURSES + "/{courseId}/privacy")
    @PreAuthorize("@courseSecurity.isOwner(#courseId, principal.id)")
    public ResponseEntity<?> changeCoursePrivacy(@PathVariable String courseId,
                                                 @RequestBody UpdateCoursePrivacyRequest request) {
        if (courseService.updateCoursePrivacy(courseId, request.privacy()))
            return ResponseEntity.ok(ApiResponse.success(
                    SuccessCodes.UPDATE_COURSE_PRIVACY_SUCCESS
            ));
        else
            return ResponseEntity.ok(ApiResponse.error(
                    ErrorCodes.UPDATE_COURSE_PRIVACY_FAILED
            ));
    }

    @GetMapping(ApiPaths.Instructor.MY_STUDENTS + "/list")
    public ResponseEntity<?> getStudentsList(@AuthenticationPrincipal(expression = "id") String userId,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer limit,
                                             @RequestParam(defaultValue = "") String search,
                                             @RequestParam(defaultValue = "") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_STUDENTS_LIST_SUCCESS,
                instructorService.getInstructorStudentsListMatchCriteria(
                        userId,
                        page,
                        limit,
                        search,
                        sort
                )
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_STUDENTS + "/stats")
    public ResponseEntity<?> getStudentsStats(@AuthenticationPrincipal(expression = "id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_STUDENTS_STATS_SUCCESS,
                instructorService.getInstructorStudentsStats(userId)
        ));
    }

    @GetMapping(ApiPaths.Instructor.STATS)
    public ResponseEntity<?> getInstructorStats(@AuthenticationPrincipal(expression = "id") String userId) {
        List<String> courseIds = instructorService.getInstructorCourseIds(userId);

        var statsBuilder = InstructorStats.builder()
                .totalCourses(courseIds.size());

        if (!courseIds.isEmpty()) {
            statsBuilder
                    .totalStudents(instructorService.getInstructorStudentIds(userId).size())
                    .totalOrders(orderService.countCompletedOrdersByCourseIds(courseIds))
                    .totalReviews(reviewService.getTotalReviewsOfCourses(courseIds))
                    .averageRating(reviewService.getInstructorAvgRating(courseIds));
        } else {
            statsBuilder.totalStudents(0).totalOrders(0L).totalReviews(0L).averageRating(0.0);
        }

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_INSTRUCTOR_STATS_SUCCESS,
                statsBuilder.build()
        ));
    }

    @PostMapping(ApiPaths.Instructor.VIDEOS)
    public ResponseEntity<?> getVideoUploadUrl(@AuthenticationPrincipal(expression = "id") String userId,
                                               @Valid UploadVideoRequest uploadVideoRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_VIDEO_UPLOAD_URL_SUCCESS,
                videoService.getVideoUploadUrl(
                        userId,
                        uploadVideoRequest.getExtension(),
                        uploadVideoRequest.getContentType()
                )
        ));
    }
}
