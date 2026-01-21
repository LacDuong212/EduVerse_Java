package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;

    @GetMapping("/api/instructor/stats")
    public ResponseEntity<?> getInstructorStats(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_INSTRUCTOR_STATS_SUCCESS,
                instructorService.getInstructorStats(currentUser.getId())
        ));
    }

    @GetMapping("/api/instructor/charts/earning")
    public ResponseEntity<?> getMonthlyEarning(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_MONTHLY_EARNING_SUCCESS,
                instructorService.getInstructorMonthlyEarning(currentUser.getId())
        ));
    }

    @GetMapping("/api/instructor/charts/top-5-courses")
    public ResponseEntity<?> getTop5Courses(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_TOP_5_COURSES_SUCCESS,
                instructorService.getInstructorTop5CourseEarning(currentUser.getId())
        ));
    }
}
