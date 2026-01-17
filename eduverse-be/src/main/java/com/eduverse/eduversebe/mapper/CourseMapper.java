package com.eduverse.eduversebe.mapper;

import com.eduverse.eduversebe.dto.respone.CourseResponse;
import com.eduverse.eduversebe.model.Course;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(source = "instructor.ref", target = "instructorId")
    @Mapping(source = "instructor.name", target = "instructorName")
    @Mapping(source = "instructor.avatar", target = "instructorAvatar")

    @Mapping(source = "rating.average", target = "averageRating")
    @Mapping(source = "rating.count", target = "ratingCount")

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "category.name", ignore = true)

    @Mapping(target = "isFree", ignore = true)

    @Mapping(target = "thumbnail", expression = "java(course.getThumbnail() != null ? course.getThumbnail() : course.getImage())")

    CourseResponse toCourseResponse(Course course);

    @AfterMapping
    default void calculateFields(Course course, @MappingTarget CourseResponse response) {
        Double finalPrice = (course.getDiscountPrice() != null) ? course.getDiscountPrice() : course.getPrice();
        response.setIsFree(finalPrice == null || finalPrice == 0);

        if (response.getAverageRating() == null) response.setAverageRating(0.0);
    }
}
