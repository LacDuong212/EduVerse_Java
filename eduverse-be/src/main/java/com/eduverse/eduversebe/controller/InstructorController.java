package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiPaths;
import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.UpdateCoursePrivacyRequest;
import com.eduverse.eduversebe.dto.request.UploadVideoRequest;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.InstructorService;
import com.eduverse.eduversebe.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;
    private final VideoService videoService;

    @GetMapping(ApiPaths.Instructor.CHARTS + "/earning")
    public ResponseEntity<?> getMonthlyEarning(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MONTHLY_EARNING_SUCCESS,
                instructorService.getCoursesMonthlyEarningPast12Months(currentUser.getId())
        ));
    }

    @GetMapping(ApiPaths.Instructor.CHARTS + "/top-5-courses")
    public ResponseEntity<?> getTop5Courses(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_TOP_5_COURSES_SUCCESS,
                instructorService.getTop5EarningCoursesThisMonth(currentUser.getId())
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_COURSES + "/list")
    public ResponseEntity<?> getCoursesList(@AuthenticationPrincipal User currentUser,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "5") Integer limit,
                                            @RequestParam(defaultValue = "") String search,
                                            @RequestParam(defaultValue = "") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_COURSES_LIST_SUCCESS,
                instructorService.getInstructorCoursesListMatchCriteria(
                        currentUser.getId(),
                        page,
                        limit,
                        search,
                        sort
                )
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_COURSES + "/stats")
    public ResponseEntity<?> getCoursesStats(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_COURSES_STATS_SUCCESS,
                instructorService.getInstructorCoursesStats(currentUser.getId())
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_STUDENTS + "/list")
    public ResponseEntity<?> getStudentsList(@AuthenticationPrincipal User currentUser,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer limit,
                                            @RequestParam(defaultValue = "") String search,
                                            @RequestParam(defaultValue = "") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_STUDENTS_LIST_SUCCESS,
                instructorService.getInstructorStudentsListMatchCriteria(
                        currentUser.getId(),
                        page,
                        limit,
                        search,
                        sort
                )
        ));
    }

    @GetMapping(ApiPaths.Instructor.MY_STUDENTS + "/stats")
    public ResponseEntity<?> getStudentsStats(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MY_STUDENTS_STATS_SUCCESS,
                instructorService.getInstructorStudentsStats(currentUser.getId())
        ));
    }

    @GetMapping(ApiPaths.Instructor.STATS)
    public ResponseEntity<?> getInstructorStats(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_INSTRUCTOR_STATS_SUCCESS,
                instructorService.getInstructorStats(currentUser.getId())
        ));
    }

    @PostMapping(ApiPaths.Instructor.VIDEOS)
    public ResponseEntity<?> getVideoUploadUrl(@AuthenticationPrincipal User currentUser,
                                               @Valid UploadVideoRequest uploadVideoRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_UPLOAD_URL_SUCCESS,
                videoService.getVideoUploadUrl(
                        currentUser.getId(),
                        uploadVideoRequest.getExtension(),
                        uploadVideoRequest.getContentType()
                )
        ));
    }

    @PatchMapping(ApiPaths.Instructor.MY_COURSES + "/{courseId}/privacy")
    public ResponseEntity<?> changeCoursePrivacy(@AuthenticationPrincipal User currentUser,
                                                 @PathVariable String courseId,
                                                 @RequestBody UpdateCoursePrivacyRequest request) {
        if (!instructorService.checkCourseOwnership(currentUser.getId(), courseId))
            throw new AppException(ErrorCodes.COURSE_ACCESS_DENIED);

        if (instructorService.changeCoursePrivacy(courseId, request))
            return ResponseEntity.ok(ApiResponse.success(
                    SuccessCodes.UPDATE_COURSE_PRIVACY_SUCCESS
            ));
        else
            return ResponseEntity.ok(ApiResponse.error(
                    ErrorCodes.UPDATE_COURSE_PRIVACY_FAILED
            ));
    }
}
