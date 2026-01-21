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
public enum ErrorCodes {
    //--General (10xxx) - Lỗi hệ thống & Dữ liệu đầu vào
    UNCATEGORIZED_EXCEPTION(10000, "Unaware exception was thrown on server", INTERNAL_SERVER_ERROR),
    VALIDATION_FAILED(10001, "Validation failed on field '{}'", BAD_REQUEST),
    INVALID_KEY(10002, "Invalid key provided", BAD_REQUEST),
    JSON_PARSE_ERROR(10003, "Cannot parse because of weird data type on field '{}'", BAD_REQUEST),
    RESOURCE_NOT_FOUND(10004, "Resource not found", NOT_FOUND),

    //--Authentication & User (11xxx)
    UNAUTHENTICATED(11000, "Unauthenticated user", UNAUTHORIZED),
    UNAUTHORIZED_ACCESS(11001, "You do not have permission", FORBIDDEN),
    USER_NOT_FOUND(11002, "User not found", NOT_FOUND),
    USER_EXISTED(11003, "User already exists", BAD_REQUEST),
    USER_ALREADY_VERIFIED(11004, "Account already verified", BAD_REQUEST),
    INVALID_OTP(11005, "Invalid OTP", BAD_REQUEST),
    OTP_EXPIRED(11006, "OTP has expired", BAD_REQUEST),
    LOGIN_FAILED(11007, "Invalid email or password", UNAUTHORIZED),
    USER_IS_BLOCKED(11008, "Your account has been blocked. Please contact the administrator.", FORBIDDEN),
    USER_NOT_VERIFIED(11009, "Please verify your email first", UNAUTHORIZED),
    INSTRUCTOR_NOT_FOUND(11010, "Instructor not found", NOT_FOUND),
    ;

    int responseCode;
    String responseMsg;
    HttpStatus responseStatus;
}