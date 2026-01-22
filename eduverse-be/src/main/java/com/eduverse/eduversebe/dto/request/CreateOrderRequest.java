package com.eduverse.eduversebe.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<String> courseIds;
    private String paymentMethod;
    private String couponCode;
}
