package com.eduverse.eduversebe.dto.respone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseStatsResponse {
    private long totalCourses;
    private long totalLearners;
    private double totalHours;
    private long totalInstructors;
}
