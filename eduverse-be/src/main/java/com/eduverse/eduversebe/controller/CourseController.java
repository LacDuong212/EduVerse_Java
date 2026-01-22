package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.CourseFilterRequest;
import com.eduverse.eduversebe.dto.response.*;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.CourseService;
import com.eduverse.eduversebe.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final RecommendationService recommendationService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<CourseStatsResponse>> getCourseStats() {
        CourseStatsResponse stats = courseService.getCourseStats();
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_COURSE_STATS_SUCCESS ,stats));
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeContentResponse>> getHomeCourses() {

        HomeContentResponse data = courseService.getHomeCourses();

        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_HOME_COURSE_SUCCESS, data));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<RecommendationResponse>> getRecommendedCourses(
            @AuthenticationPrincipal User user
    ) {
        // Lấy ID user từ token (nếu user chưa login thì user sẽ là null)
        String userId = (user != null) ? user.getId() : null;

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_RECOMMENDATION_SUCCESS,
                recommendationService.getRecommendedCourses(userId)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getAllCourses(
            @ModelAttribute CourseFilterRequest request
    ) {
        PageResponse<CourseResponse> response = courseService.getAllCourses(request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_ALL_COURSE_SUCCESS, response));
    }

    @GetMapping("/filters")
    public ResponseEntity<ApiResponse<CourseFilterResponse>> getCourseFilters() {
        CourseFilterResponse filters = courseService.getCourseFilters();

        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_COURSE_FILTER_SUCCESS, filters));
    }
}
