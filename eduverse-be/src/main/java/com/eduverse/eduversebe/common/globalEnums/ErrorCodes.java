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

    //--Instructor (12xxx)
    INSTRUCTOR_ALREADY_EXISTS(12000, "You are already registered as an instructor.", BAD_REQUEST),
    INSTRUCTOR_NOT_FOUND(12001, "Instructor not found for this user.", NOT_FOUND),

    //--Notification (13xxx)
    NOTIFICATION_NOT_FOUND(13000, "Notification not found for this user.", NOT_FOUND),

    //--Course (14xxx)
    COURSE_NOT_FOUND(14000, "Course not found for this user.", NOT_FOUND),
    UPDATE_COURSE_PRIVACY_FAILED(14001, "Update course privacy failed", BAD_REQUEST),
    COURSE_ACCESS_DENIED(14002, "You do not have access to this course", FORBIDDEN),
    DRAFT_COURSE_NOT_FOUND(14003, "Draft not found for this course", NOT_FOUND),
    COURSE_VALIDATION_FAILED(14004, "Validate course failed with errors", BAD_REQUEST),
    INVALID_COURSE_LANGUAGE(14005, "Invalid course language", BAD_REQUEST),
    INVALID_COURSE_LEVEL(14006, "Invalid course level", BAD_REQUEST),
    INVALID_COURSE_AI_STATUS(14007, "Invalid course AI status", BAD_REQUEST),
    INVALID_COURSE_DURATION_UNITS(14008, "Invalid course duration unit count", BAD_REQUEST),
    INVALID_COURSE_STATUS(14009, "Invalid course status", BAD_REQUEST),

    //--Cart (15xxx)

    //--Wishlist (16xxx)
    COURSE_ALREADY_IN_WISHLIST(16000, "Course already in Wishlist.", BAD_REQUEST),

    //--Cart (17xxx)
    COURSE_ALREADY_IN_CART(17000, "Course already in Cart", BAD_REQUEST),
    YOU_ALREADY_OWN_THIS_COURSE(17001, "You already own this course", CONFLICT),
    COURSE_IN_ACTIVE_ORDER(17002, "This course is already in your active order", CONFLICT),

    //--Coupon (18xxx)
    COUPON_NOT_FOUND(18000, "Invalid coupon code!", NOT_FOUND),
    COUPON_NOT_ACTIVE_YET(18001, "This coupon is not active yet.", BAD_REQUEST),
    COUPON_EXPIRED(18002, "The coupon code has expired.", BAD_REQUEST),
    COUPON_ALREADY_USED(18003, "You have already used this code!", BAD_REQUEST),

    //--Order (19xxx)
    ORDER_NOT_FOUND(19000, "Order not found", NOT_FOUND),
    CART_EMPTY(19001, "Cart is empty or invalid", BAD_REQUEST),
    INVALID_PAYMENT_METHOD(19002, "Invalid payment method", BAD_REQUEST),
    ORDER_NOT_CANCELLABLE(19003, "Processed orders cannot be canceled", BAD_REQUEST),
    INVALID_ORDER_STATUS(19004, "Invalid order status", BAD_REQUEST),
    PAYMENT_METHOD_NOT_FOUND(19005, "Payment method not found", NOT_FOUND),

    //--Student (20xxx)
    ADD_COURSES_FAILED(20001, "There are no courses to add", BAD_REQUEST),

    //-- Video (21xxx)
    DELETE_VIDEO_FAILED(21000, "Failed to delete video", BAD_REQUEST),

    ;

    int responseCode;
    String responseMsg;
    HttpStatus responseStatus;
}