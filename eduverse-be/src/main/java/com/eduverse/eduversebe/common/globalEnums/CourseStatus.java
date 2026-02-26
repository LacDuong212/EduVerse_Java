package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseStatus {
    Rejected,
    Pending,
    Live,
    Blocked,
    Draft,

    ;
    @JsonCreator
    public static CourseStatus fromString(String value) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new AppException(ErrorCodes.INVALID_COURSE_STATUS);
    }
}
