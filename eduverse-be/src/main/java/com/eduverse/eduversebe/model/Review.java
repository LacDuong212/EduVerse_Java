package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review extends BaseEntity {

    private String courseId;
    private String userId;

    @Builder.Default
    private Integer rating = 0;
    private String description;

    @Builder.Default
    private boolean isDeleted = false;
}
