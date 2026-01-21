package com.eduverse.eduversebe.common.globalEnums;

import static org.springframework.http.HttpStatus.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SuccessCodes {
    //--General Success (20xxx)
    SUCCESS(20000, "Request processed successfully", OK),

    //--Authentication & User (21xxx)
    LOGIN_SUCCESS(21000, "Login successfully", OK),
    REGISTER_SUCCESS(21001, "User registered successfully", CREATED),
    VERIFY_EMAIL_SUCCESS(21002, "Email verified successfully. Please log in to continue.", OK),
    FORGOT_PASSWORD_SENT(21003, "New OTP sent to your email", OK),
    PASSWORD_RESET_SUCCESS(21004, "Password reset successfully", OK),
    LOGOUT_SUCCESS(21005, "Logout successful", OK),
    SEND_OTP_SUCCESS(21006, "OTP sent. Please check your email!", OK),


    //--Course (22xxx)
    GET_HOME_COURSE_SUCCESS(22000, "Get home course successfully", OK),
    GET_ALL_COURSE_SUCCESS(22001, "Get all course successfully", OK),
    GET_COURSE_FILTER_SUCCESS(22002, "Get course filter successfully", OK),
    GET_TOP_5_COURSES_SUCCESS(22003, "Get top 5 courses successfully", OK),

    //--Category (23xxx)
    GET_ALL_CATEGORIES_SUCCESS(23000, "Get all categories successfully", OK),

    //--Instructor (24xxx)
    GET_INSTRUCTOR_PROFILE_SUCCESS(24000, "Get instructor profile successfully", OK),
    GET_INSTRUCTOR_STATS_SUCCESS(24001, "Get stats successfully", OK),
    GET_MONTHLY_EARNING_SUCCESS(24002, "Get monthly earning successfully", OK),

    ;

    int responseCode;
    String responseMsg;
    HttpStatus responseStatus;
}