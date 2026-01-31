package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.globalEnums.LectureStatus;
import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courseprogresses")
@CompoundIndex(name = "user_course_idx", def = "{'userId': 1, 'courseId': 1}", unique = true)
public class CourseProgress extends BaseEntity {

    private String userId;
    private String courseId;

    @Builder.Default
    private Integer totalLectures = 0;
    @Builder.Default
    private Integer completedLecturesCount = 0;
    @Builder.Default
    private Integer totalTimeSpentSec = 0;

    private String lastLectureId;
    @Builder.Default
    private Integer lastPositionSec = 0;

    @Builder.Default
    private List<LectureProgress> lectures = new ArrayList<>();

    private LocalDateTime firstStartedAt;
    private LocalDateTime lastActivityAt;

    @Builder.Default
    private boolean isCompleted = false;
    private AiAssessment aiAssessment;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LectureProgress {
        private String lectureId;

        @Builder.Default
        private LectureStatus status = LectureStatus.not_started;
        @Builder.Default
        private Integer lastPositionSec = 0;
        @Builder.Default
        private Integer durationSec = 0;
        @Builder.Default
        private Integer totalTimeSpentSec = 0;
        @Builder.Default
        private Integer viewCount = 0;

        private LocalDateTime completedAt;
        private LocalDateTime lastActivityAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiAssessment {
        private LocalDateTime generatedAt;

        @Builder.Default
        private Double overallScore = 0.0;

        private String summary;
        private List<String> strengths;
        private List<String> weaknesses;
        private String recommendation;
    }
}
