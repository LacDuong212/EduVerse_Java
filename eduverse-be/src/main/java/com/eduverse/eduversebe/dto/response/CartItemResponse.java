package com.eduverse.eduversebe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CartItemResponse {
    private CourseResponse course;

    private Instant addedAt;
}
