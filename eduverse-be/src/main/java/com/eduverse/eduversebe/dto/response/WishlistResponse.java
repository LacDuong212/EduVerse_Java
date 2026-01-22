package com.eduverse.eduversebe.dto.response;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class WishlistResponse {
    private String id;
    private String userId;
    private Instant addedAt;

    private CourseResponse course;
}
