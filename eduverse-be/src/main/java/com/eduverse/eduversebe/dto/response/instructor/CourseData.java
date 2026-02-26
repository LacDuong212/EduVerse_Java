package com.eduverse.eduversebe.dto.response.instructor;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseData {
    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String image;
    private String status;
    private String categoryId;
    private String subCategory;
    private String language;
    private String level;
    private Double duration;
    private String durationUnit;
    private Double price;
    private Double discountPrice;
    private Boolean enableDiscount;
    private String thumbnail;
    private String previewVideo;
    private Integer lecturesCount;
    private List<Section> curriculum;
    private List<String> tags;
    private Boolean isPrivate;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Section {
        private String section;
        private List<Lecture> lectures;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Lecture {
        private String id;
        private String title;
        private String videoUrl;
        private Double duration;
        private Boolean isFree;
        private AiData aiData;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AiData {
        private String summary;
        private LessonNotes lessonNotes;
        private List<Quiz> quizzes;
        private String status;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LessonNotes {
        private List<KeyConcept> keyConcepts;
        private List<String> mainPoints;
        private List<String> practicalTips;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class KeyConcept {
        private String term;
        private String definition;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Quiz {
        private String question;
        private List<String> options;
        private String correctAnswer;
        private String explanation;
        private String topic;
    }
}