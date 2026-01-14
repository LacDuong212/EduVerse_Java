package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.respone.CourseResponse;
import com.eduverse.eduversebe.dto.respone.HomeContentResponse;
import com.eduverse.eduversebe.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeContentResponse>> getHomeCourses() {
        HomeContentResponse result = courseService.getHomeCourses();

        // Dùng ApiResponse chuẩn của bạn
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_HOME_COURSE_SUCCESS, result));
    }

    @GetMapping("/test-all")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCoursesTest() {
        List<CourseResponse> result = courseService.getAllCoursesTest();

        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_HOME_COURSE_SUCCESS, result));
    }
}
