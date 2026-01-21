package com.eduverse.eduversebe.repository.projection;

public interface CourseEarningThisMonth {
    String getCourseId();
    String getTitle();
    Double getTotalEarning();
    Long getTotalSales();
}
