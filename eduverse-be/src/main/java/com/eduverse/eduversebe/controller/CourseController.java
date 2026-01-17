package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.CourseFilterRequest;
import com.eduverse.eduversebe.dto.respone.CourseFilterResponse;
import com.eduverse.eduversebe.dto.respone.CourseResponse;
import com.eduverse.eduversebe.dto.respone.HomeContentResponse;
import com.eduverse.eduversebe.dto.respone.PageResponse;
import com.eduverse.eduversebe.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeContentResponse>> getHomeCourses() {

        HomeContentResponse data = courseService.getHomeCourses();

        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_HOME_COURSE_SUCCESS, data));
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
