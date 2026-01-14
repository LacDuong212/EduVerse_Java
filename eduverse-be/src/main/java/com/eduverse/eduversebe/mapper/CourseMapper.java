package com.eduverse.eduversebe.mapper;

import com.eduverse.eduversebe.dto.respone.CourseResponse;
import com.eduverse.eduversebe.model.Course;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    // 1. Map các trường lồng nhau (Nested Fields) ra ngoài phẳng
    @Mapping(source = "instructor.name", target = "instructorName")
    @Mapping(source = "instructor.avatar", target = "instructorAvatar")
    @Mapping(source = "instructor.ref", target = "instructorId") // ref là ID của user

    @Mapping(source = "rating.average", target = "averageRating")
    @Mapping(source = "rating.count", target = "ratingCount")

    // categoryId giữ nguyên, còn name/slug sẽ set ở Service
    CourseResponse toCourseResponse(Course course);

    // 2. Hàm xử lý logic tính toán sau khi Map xong
    @AfterMapping
    default void calculateFields(Course course, @MappingTarget CourseResponse response) {
        // Tính discountAmount: Nếu có giá giảm thì trừ, không thì bằng 0
        if (course.getPrice() != null && course.getDiscountPrice() != null) {
            response.setDiscountAmount(course.getPrice() - course.getDiscountPrice());
        } else {
            response.setDiscountAmount(0.0);
        }

        // Tính isFree: Nếu giá = 0 hoặc null thì là free
        if (course.getPrice() == null || course.getPrice() == 0) {
            response.setIsFree(true);
        } else {
            response.setIsFree(false);
        }
    }
}
