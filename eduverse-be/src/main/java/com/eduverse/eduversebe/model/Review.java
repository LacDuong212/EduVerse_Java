package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review extends BaseEntity {

    @Field(value = "course", targetType = FieldType.OBJECT_ID)
    private String courseId;
    @Field(value = "user", targetType = FieldType.OBJECT_ID)
    private String userId;

    @Builder.Default
    private Integer rating = 0;
    private String description;

    @Builder.Default
    private boolean isDeleted = false;
}
