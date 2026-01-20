package com.eduverse.eduversebe.dto.respone;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CartItemResponse {
    private CourseResponse course;

    private Instant addedAt;
}
