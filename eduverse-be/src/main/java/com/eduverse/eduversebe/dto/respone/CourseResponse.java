package com.eduverse.eduversebe.dto.respone;

import com.eduverse.eduversebe.common.globalEnums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private String id;
    private String title;
    private String subTitle;
    private String thumbnail;

    // --- Pricing ---
    private Double price;
    private Double discountPrice;
    private Double discountAmount; // Field tính toán (price - discountPrice)

    // --- Category (Sẽ được map thủ công ở Service) ---
    private String categoryId;
    private String categoryName;
    private String categorySlug;

    // --- Instructor (Lấy từ embedded object trong Course) ---
    private String instructorName;
    private String instructorAvatar;
    private String instructorId;

    // --- Stats & Rating ---
    private Double averageRating;
    private Integer ratingCount;
    private Integer studentsEnrolled;
    private Integer lecturesCount;

    private CourseLevel level;
    private Double duration;

    // --- Meta ---
    private Boolean isFree; // Kiểm tra xem có phải khóa học miễn phí không
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
