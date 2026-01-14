package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students")
public class Student extends BaseEntity {

    @Indexed(unique = true)
    private String userId;

    @Builder.Default
    private StudentStats stats = new StudentStats();

    @Builder.Default
    private List<MyCourse> myCourses = new ArrayList<>();

    @Builder.Default
    private String address = "";

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStats {
        @Builder.Default
        private Integer totalCourses = 0;
        @Builder.Default
        private Integer completedCourses = 0;
        @Builder.Default
        private Integer totalLessons = 0;
        @Builder.Default
        private Integer completedLessons = 0;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCourse {
        private String courseId;
    }
}
