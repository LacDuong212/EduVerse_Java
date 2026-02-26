package com.eduverse.eduversebe.security.evaluator;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.service.CourseService;
import org.springframework.stereotype.Component;

@Component("courseSecurity")
public class CourseSecurityEvaluator {
    private final CourseService courseService;

    public CourseSecurityEvaluator(CourseService courseService) {
        this.courseService = courseService;
    }

    public boolean isOwner(String courseId, String userId) {
        if (!courseService.checkCourseOwnership(courseId, userId)) {
            throw new AppException(ErrorCodes.COURSE_ACCESS_DENIED);
        }

        return true;
    }
}
