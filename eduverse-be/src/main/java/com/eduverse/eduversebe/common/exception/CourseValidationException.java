package com.eduverse.eduversebe.common.exception;


import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import lombok.Getter;

import java.util.Map;

@Getter
public class CourseValidationException extends RuntimeException {
    private final ErrorCodes errorCode = ErrorCodes.COURSE_VALIDATION_FAILED;
    private final Map<String,String> errors;

    public CourseValidationException(Map<String, String> errors) {
        super(ErrorCodes.COURSE_VALIDATION_FAILED.getResponseMsg());
        this.errors = errors;
    }
}
