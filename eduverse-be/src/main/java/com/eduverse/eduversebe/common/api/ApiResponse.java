package com.eduverse.eduversebe.common.api;

import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T result;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(SuccessCodes successCode, T result) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(successCode.getResponseCode())
                .message(successCode.getResponseMsg())
                .result(result)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(SuccessCodes successCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(successCode.getResponseCode())
                .message(successCode.getResponseMsg())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}