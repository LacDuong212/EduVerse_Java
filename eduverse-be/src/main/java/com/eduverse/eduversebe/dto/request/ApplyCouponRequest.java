package com.eduverse.eduversebe.dto.request;

import lombok.Data;

@Data
public class ApplyCouponRequest {
    private String code;
    private Double originalPrice;
}
