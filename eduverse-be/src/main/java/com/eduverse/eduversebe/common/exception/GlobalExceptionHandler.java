package com.eduverse.eduversebe.common.exception;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getFieldError() != null
                ? exception.getFieldError().getDefaultMessage()
                : exception.getMessage();

        String fieldName = exception.getFieldError() != null
                ? exception.getFieldError().getField()
                : "Object";

        log.error("[VALIDATION FAILED] Field: {} - Message: {}", fieldName, errorMessage);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(
                        ErrorCodes.VALIDATION_FAILED.getResponseCode(),
                        errorMessage
                ));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<?>> handleJsonParse(HttpMessageNotReadableException exception) {
        log.error("[JSON PARSE FAILED] {}", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(
                        ErrorCodes.JSON_PARSE_ERROR.getResponseCode(),
                        "Invalid JSON format. Please check your input data types."
                ));
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCodes errorCode = exception.getErrorCodes();

        log.warn("[APP EXCEPTION] {}", errorCode.getResponseMsg());

        return ResponseEntity
                .status(errorCode.getResponseStatus())
                .body(ApiResponse.error(
                        errorCode.getResponseCode(),
                        errorCode.getResponseMsg()
                ));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        log.error("[ACCESS ERROR] User does not have permission", exception);
        ErrorCodes errorCode = ErrorCodes.UNAUTHORIZED_ACCESS;

        return ResponseEntity
                .status(errorCode.getResponseStatus())
                .body(ApiResponse.error(
                        errorCode.getResponseCode(),
                        errorCode.getResponseMsg()
                ));
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(Exception exception) {
        log.error("[SYSTEM ERROR] ", exception);
        ErrorCodes errorCode = ErrorCodes.UNCATEGORIZED_EXCEPTION;

        return ResponseEntity
                .status(errorCode.getResponseStatus())
                .body(ApiResponse.error(
                        errorCode.getResponseCode(),
                        errorCode.getResponseMsg()
                ));
    }

    @ExceptionHandler(value = CourseValidationException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(CourseValidationException exception) {
        ErrorCodes errorCode = exception.getErrorCode();

        log.warn("[COURSE VALIDATION EXCEPTION] {}", errorCode.getResponseMsg());

        return ResponseEntity
                .status(errorCode.getResponseStatus())
                .body(ApiResponse.error(
                        errorCode,
                        exception.getErrors()
                ));
    }
}