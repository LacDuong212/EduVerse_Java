package com.eduverse.eduversebe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCompletionService {

    private final CourseService courseService;
    private final InstructorService instructorService;
    private final StudentService studentService;

    public void handleOrderCompleted(String userId, List<String> courseIds) {
        studentService.addCourses(userId, courseIds);
        courseService.bulkUpdateCoursesEnrollmentCount(courseIds);
        instructorService.updateInstructorsStudentListFromCourseIds(userId, courseIds);
    }
}
