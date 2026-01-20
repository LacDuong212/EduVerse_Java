package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.model.Instructor;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<ApiResponse<Instructor>> createInstructor(
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = instructorService.createInstructor(currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Instructor>builder()
                        .success(true)
                        .message("Instructor created successfully.")
                        .result(instructor)
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Instructor>> getCurrentInstructor(
            @AuthenticationPrincipal User currentUser
    ) {
        Instructor instructor = instructorService.getCurrentInstructor(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.<Instructor>builder()
                .success(true)
                .result(instructor)
                .build());
    }
}
