package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseAiStatus {
    None,
    Processing,
    Completed,
    Failed,

    ;
    @JsonCreator
    public static CourseAiStatus fromString(String value) {
        for (CourseAiStatus status : CourseAiStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new AppException(ErrorCodes.INVALID_COURSE_AI_STATUS);
    }
}
