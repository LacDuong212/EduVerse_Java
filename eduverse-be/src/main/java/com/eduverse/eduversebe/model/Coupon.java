package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coupons")
public class Coupon extends BaseEntity {

    @Indexed(unique = true)
    private String code;

    private String description;
    private Integer discountPercent;

    private Instant startDate;
    private Instant expiryDate;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private List<String> usersUsed = new ArrayList<>();
}
