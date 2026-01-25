package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "instructors")
public class Instructor extends BaseEntity {

    @Field(value = "user", targetType = FieldType.OBJECT_ID)
    private String userId;

    @Builder.Default
    private Stats stats = new Stats();
    @Builder.Default
    private Rating rating = new Rating();

    @Builder.Default
    private List<LinkedAccount> linkedAccounts = new ArrayList<>();
    @Builder.Default
    private List<MyCourse> myCourses = new ArrayList<>();
    @Builder.Default
    private List<MyStudent> myStudents = new ArrayList<>();
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder.Default
    private String introduction = "";
    @Builder.Default
    private String address = "";
    @Builder.Default
    private String occupation = "";

    @Builder.Default
    private List<Skill> skills = new ArrayList<>();
    @Builder.Default
    private List<Education> education = new ArrayList<>();

    @Builder.Default
    private boolean isApproved = false;

    // --- Inner Classes ---
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        @Builder.Default
        private Integer totalCourses = 0;
        @Builder.Default
        private Integer totalStudents = 0;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rating {
        @Builder.Default
        private Double averageRating = 0.0;
        @Builder.Default
        private Integer totalRatings = 0;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkedAccount {
        private String platform;
        private String profileUrl;

        @Builder.Default
        private LocalDateTime addedAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCourse {
        @Field(value = "course", targetType = FieldType.OBJECT_ID)
        private String courseId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyStudent {
        @Field(value = "student", targetType = FieldType.OBJECT_ID)
        private String studentId;
        @Builder.Default
        private LocalDateTime addedAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enrollment {
        @Field(value = "course", targetType = FieldType.OBJECT_ID)
        private String courseId;
        @Field(value = "student", targetType = FieldType.OBJECT_ID)
        private String studentId;

        @Builder.Default
        private boolean isActive = false;
        @Builder.Default
        private LocalDateTime enrolledAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skill {
        private String name;

        @Builder.Default
        private Integer level = 0;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
        private String institution;

        @Builder.Default
        private String degree = "";

        private String fieldOfStudy;
        private Date startDate;
        private Date endDate;

        @Builder.Default
        private LocalDateTime addedAt = LocalDateTime.now();
    }
}
