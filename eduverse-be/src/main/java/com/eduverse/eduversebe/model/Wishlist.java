package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlists")
@CompoundIndex(name = "user_course_wishlist_idx", def = "{'userId': 1, 'courseId': 1}", unique = true)
public class Wishlist extends BaseEntity {

    private String userId;
    private String courseId;
    private Instant addedAt;
}
