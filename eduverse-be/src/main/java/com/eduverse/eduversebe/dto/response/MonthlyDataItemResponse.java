package com.eduverse.eduversebe.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.YearMonth;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class MonthlyDataItemResponse {
    YearMonth period;
    Double value;
}
