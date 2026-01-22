package com.eduverse.eduversebe.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String payUrl;
    private String deepLink;
    private String paymentMethod;
}
