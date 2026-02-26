package com.eduverse.eduversebe.dto.response.instructor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyCoursesStats {
    Integer totalCourses;
    Integer totalDraft;
    Integer totalLive;
    Integer totalPending;
    Integer totalRejected;
    Integer totalBlocked;
}
