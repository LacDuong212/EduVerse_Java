package com.eduverse.eduversebe.dto.respone;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class CourseEarningDataResponse {
    String id;
    String title;
    Double totalEarning;
    Long totalSales;
}
