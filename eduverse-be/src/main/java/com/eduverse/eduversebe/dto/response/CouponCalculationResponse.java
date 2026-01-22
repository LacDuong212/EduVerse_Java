package com.eduverse.eduversebe.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponCalculationResponse {
    private String couponCode;
    private Integer discountPercent;
    private Double discountAmount;
    private Double newPrice;
}
