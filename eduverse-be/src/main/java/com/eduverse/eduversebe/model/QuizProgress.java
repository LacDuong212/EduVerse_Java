package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quiz_progress")
@CompoundIndex(name = "user_course_quiz_idx", def = "{'userId': 1, 'courseId': 1}", unique = true)
public class QuizProgress extends BaseEntity {

    private String userId;
    private String courseId;

    @Builder.Default
    private List<QuizResult> quizzes = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizResult {
        private String lectureId;
        private Double score;
        private Integer totalQuestions;
        private List<WrongAnswer> wrongAnswers;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrongAnswer {
        private String question;
        private String topic;
    }
}
