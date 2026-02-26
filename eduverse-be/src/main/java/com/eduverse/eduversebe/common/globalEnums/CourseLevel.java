package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseLevel {
    All,
    Beginner,
    Intermediate,
    Advanced,

    ;
    @JsonCreator
    public static CourseLevel fromString(String value) {
        for (CourseLevel level : CourseLevel.values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new AppException(ErrorCodes.INVALID_COURSE_LEVEL);
    }
}
