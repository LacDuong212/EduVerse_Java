package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    pending,
    completed,
    refunded,
    cancelled,

    ;
    @JsonCreator
    public static OrderStatus fromString(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new AppException(ErrorCodes.INVALID_ORDER_STATUS);
    }
}
