package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseDurationUnit {
    hour,
    minute,
    second,
    day,

    ;
    @JsonCreator
    public static CourseDurationUnit fromString(String value) {
        for (CourseDurationUnit unit : CourseDurationUnit.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new AppException(ErrorCodes.INVALID_COURSE_DURATION_UNITS);
    }
}
