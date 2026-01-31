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
    GET_COURSE_STATS_SUCCESS(22000, "Get course stats successfully", OK),
    GET_HOME_COURSE_SUCCESS(22001, "Get home course successfully", OK),
    GET_ALL_COURSE_SUCCESS(22002, "Get all course successfully", OK),
    GET_COURSE_FILTER_SUCCESS(22003, "Get course filter successfully", OK),
    GET_RECOMMENDATION_SUCCESS(22004, "Get recommendation successfully", OK),

    //--Category (23xxx)
    GET_ALL_CATEGORIES_SUCCESS(23000, "Get all categories successfully", OK),

    //--Notification (24xxx)
    GET_USER_NOTIFICATIONS_SUCCESS(24000, "Get user notifications successfully", OK),
    ALL_NOTIFICATIONS_MARKED(24001, "All notifications marked as read", OK),
    NOTIFICATION_MARKED(24002, "Notification marked as read", OK),
    ALL_NOTIFICATIONS_DELETED(24003, "All notifications deleted", OK),

    //--Wishlist (25xxx)
    ADDED_TO_WISHLIST(25000, "Added to wishlist successfully", CREATED),
    REMOVED_FROM_WISHLIST(25001, "Removed from wishlist successfully", OK),
    GET_WISHLIST_SUCCESS(25002, "Get wishlist successfully", OK),
    CHECK_WISHLIST_SUCCESS(25003, "Check wishlist status successfully", OK),
    COUNT_WISHLIST_SUCCESS(24004, "Count wishlist successfully", OK),

    //--Cart (26xxx)
    GET_CART_SUCCESS(26000, "Get cart successfully", OK),
    ADDED_TO_CART(26001, "Add cart successfully", OK),
    REMOVED_FROM_CART(26002, "Remove cart successfully", OK),
    CLEAR_CART_SUCCESS(26003, "Clear cart successfully", OK),
    GET_CART_COUNT_SUCCESS(26004, "Get cart count successfully", OK),

    //--Coupon(27xxx)
    GET_COUPONS_SUCCESS(27000, "Get coupons successfully", OK),
    APPLY_COUPONS_SUCCESS(27001, "Apply coupons successfully", OK),

    //--Order(28xxx)
    GET_ORDERS_SUCCESS(28000, "Get orders successfully", OK),
    GET_ORDER_BY_ID_SUCCESS(28001, "Get order by id successfully", OK),
    CREATE_ORDER_SUCCESS(28002, "Create order successfully", CREATED),
    UPDATE_ORDER_SUCCESS(28003, "Update order successfully", OK),

    //--Payment (29xxx)
    CREATE_PAYMENT_SUCCESS(28004, "Create payment successfully", CREATED),


    //--Instructor (30xxx)
    GET_INSTRUCTOR_PROFILE_SUCCESS(30000, "Get instructor profile successfully", OK),
    GET_INSTRUCTOR_STATS_SUCCESS(30001, "Get stats successfully", OK),
    GET_MONTHLY_EARNING_SUCCESS(30002, "Get monthly earning successfully", OK),
    GET_TOP_5_COURSES_SUCCESS(30003, "Get top 5 courses successfully", OK),
    GET_MY_COURSES_LIST_SUCCESS(30004, "Get my courses list successfully", OK),
    GET_MY_COURSES_STATS_SUCCESS(30005, "Get courses stats successfully", OK),
    UPDATE_COURSE_PRIVACY_SUCCESS(30006, "Update course privacy successfully", OK),
    GET_MY_STUDENTS_LIST_SUCCESS(30007, "Get my students list successfully", OK),
    GET_MY_STUDENTS_STATS_SUCCESS(30008, "Get my students stats successfully", OK),
    GET_UPLOAD_URL_SUCCESS(30009, "Get video upload url successfully", OK),

    //--Video (31xxx)
    GET_VIDEO_SUCCESS(31000, "Get video successfully", OK),

    ;

    int responseCode;
    String responseMsg;
    HttpStatus responseStatus;
}