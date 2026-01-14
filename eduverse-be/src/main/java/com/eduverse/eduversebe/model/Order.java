package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.globalEnums.OrderStatus;
import com.eduverse.eduversebe.common.globalEnums.PaymentMethod;
import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order extends BaseEntity {

    private String userId;
    private List<OrderItem> courses;
    private Double subTotal;
    private String couponId;

    @Builder.Default
    private Double discountAmount = 0.0;

    private Double totalAmount;
    private PaymentMethod paymentMethod;

    @Builder.Default
    private OrderStatus status = OrderStatus.pending;

    private LocalDateTime expiresAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String courseId;
        private Double pricePaid;
    }
}
