package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.globalEnums.*;
import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course extends BaseEntity {

    @TextIndexed
    private String title;

    private String subtitle;
    private String description;
    private String image;

    @Field("category")
    private String categoryId;

    private String subCategory;

    private CourseInstructor instructor;

    @Builder.Default
    private CourseLanguage language = CourseLanguage.English;

    @Builder.Default
    private CourseLevel level = CourseLevel.All;

    private Double duration;

    @Builder.Default
    private CourseDurationUnit durationUnit = CourseDurationUnit.second;

    private Integer lecturesCount;

    @Builder.Default
    private List<Section> curriculum = new ArrayList<>();

    @Builder.Default
    private Integer studentsEnrolled = 0;

    @Builder.Default
    private RatingInfo rating = new RatingInfo();

    private String thumbnail;
    private String previewVideo;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private Double price;
    private Double discountPrice;

    @Builder.Default
    private Boolean enableDiscount = false;

    @Builder.Default
    private CourseStatus status = CourseStatus.Pending;

    @Builder.Default
    private Boolean isPrivate = true;

    @Builder.Default
    private Boolean isDeleted = false;

    // ================== INNER CLASSES ==================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseInstructor {
        @Field(targetType = FieldType.OBJECT_ID)
        private String ref;
        private String name;
        private String avatar;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingInfo {
        @Builder.Default
        private Double average = 0.0;
        @Builder.Default
        private Integer count = 0;
        @Builder.Default
        private Integer total = 0;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Section {
        private String section;
        @Builder.Default
        private List<Lecture> lectures = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lecture {
        @Id
        private String id;

        private String title;
        private String videoUrl;
        private Double duration;

        @Builder.Default
        private Boolean isFree = false;

        private AiData aiData;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiData {
        private String summary;
        private LessonNotes lessonNotes;
        private List<Quiz> quizzes;

        @Builder.Default
        private CourseAiStatus status = CourseAiStatus.None;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonNotes {
        private List<KeyConcept> keyConcepts;
        private List<String> mainPoints;
        private List<String> practicalTips;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyConcept {
        private String term;
        private String definition;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Quiz {
        private String question;
        private List<String> options;
        private String correctAnswer;
        private String explanation;
        @Builder.Default
        private String topic = "General Knowledge";
    }
}
