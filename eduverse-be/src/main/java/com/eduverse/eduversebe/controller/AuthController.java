package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.*;
import com.eduverse.eduversebe.dto.response.LoginResponse;
import com.eduverse.eduversebe.dto.response.UserResponse;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.repository.UserRepository;
import com.eduverse.eduversebe.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(summary = "abc")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {

        authService.register(request);

        return ResponseEntity
                .status(SuccessCodes.REGISTER_SUCCESS.getResponseStatus())
                .body(ApiResponse.success(SuccessCodes.REGISTER_SUCCESS));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody @Valid VerifyEmailRequest request) {

        authService.verifyEmail(request);

        return ResponseEntity
                .status(SuccessCodes.VERIFY_EMAIL_SUCCESS.getResponseStatus())
                .body(ApiResponse.success(SuccessCodes.VERIFY_EMAIL_SUCCESS));
    }

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {

        LoginResponse loginResult = authService.login(request);

        boolean isProduction = "prod".equals(activeProfile);

        ResponseCookie jwtCookie = ResponseCookie.from("token", loginResult.getAccessToken())
                .httpOnly(true)
                .secure(isProduction)
                .sameSite(isProduction ? "None" : "Strict")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(ApiResponse.success(SuccessCodes.LOGIN_SUCCESS, loginResult));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SuccessCodes.FORGOT_PASSWORD_SENT));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SuccessCodes.PASSWORD_RESET_SUCCESS));
    }

    @GetMapping("/is-auth")
    public ResponseEntity<ApiResponse<UserResponse>> isAuthenticated() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Guest user")
                    .result(null)
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }

        User user = (User) authentication.getPrincipal();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .pfpImg(user.getPfpImg())
                .interests(user.getInterests())
                .build();

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User is authenticated")
                .result(userResponse)
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {

        boolean isProduction = "prod".equals(activeProfile);

        ResponseCookie cleanCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(isProduction)
                .sameSite(isProduction ? "None" : "Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(ApiResponse.success(SuccessCodes.LOGOUT_SUCCESS));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody @Valid SendOtpRequest request) {

        authService.sendOtp(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SuccessCodes.SEND_OTP_SUCCESS));
    }
}
