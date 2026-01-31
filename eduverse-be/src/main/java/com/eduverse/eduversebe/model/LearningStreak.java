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
@Document(collection = "learningstreaks")
public class LearningStreak extends BaseEntity {

    @Indexed(unique = true)
    private String userId;

    @Builder.Default
    private Integer currentStreak = 0;
    @Builder.Default
    private Integer longestStreak = 0;

    private String lastActiveDate;

    @Builder.Default
    private List<String> activeDates = new ArrayList<>();
}
