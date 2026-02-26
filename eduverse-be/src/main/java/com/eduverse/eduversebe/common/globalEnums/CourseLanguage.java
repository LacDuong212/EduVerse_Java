package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseLanguage {
    English,
    Vietnamese,
    Others,
    
    ;
    @JsonCreator
    public static CourseLanguage fromString(String value) {
        for (CourseLanguage language : CourseLanguage.values()) {
            if (language.name().equalsIgnoreCase(value)) {
                return language;
            }
        }
        throw new AppException(ErrorCodes.INVALID_COURSE_LANGUAGE);
    }
}
