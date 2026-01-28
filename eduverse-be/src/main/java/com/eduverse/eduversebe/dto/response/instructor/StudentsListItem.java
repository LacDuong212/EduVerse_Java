package com.eduverse.eduversebe.dto.response.instructor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentsListItem {
    String id;
    String name;
    String email;
    String pfpImg;
    Integer coursesEnrolled;
    Boolean isActive;
}
