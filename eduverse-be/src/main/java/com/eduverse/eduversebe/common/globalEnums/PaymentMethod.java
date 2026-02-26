package com.eduverse.eduversebe.common.globalEnums;

import com.eduverse.eduversebe.common.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    momo,
    vnpay,
    free,

    ;
    @JsonCreator
    public static PaymentMethod fromString(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new AppException(ErrorCodes.INVALID_PAYMENT_METHOD);
    }
}
