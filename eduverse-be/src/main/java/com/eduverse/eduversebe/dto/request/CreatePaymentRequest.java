package com.eduverse.eduversebe.dto.request;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private String orderId;
    private String paymentMethod;
}
