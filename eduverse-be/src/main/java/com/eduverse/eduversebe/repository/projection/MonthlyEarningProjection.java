package com.eduverse.eduversebe.repository.projection;

public interface MonthlyEarningProjection {
    Integer getYear();
    Integer getMonth();
    Double getTotalEarning();
}
