package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.respone.UserResponse;
import com.eduverse.eduversebe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {

        // 1. Lấy ID từ Token (đã cấu hình ở bước Auth trước đó)
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Gọi Service
        UserResponse profile = userService.getProfile(userId);

        // 3. Trả về kết quả
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.LOGIN_SUCCESS, profile));
    }
}
