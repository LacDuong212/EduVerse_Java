package com.eduverse.eduversebe.common.exception;

import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private final ErrorCodes errorCodes;

    public AppException(ErrorCodes errorCodes) {
        super(errorCodes.getResponseMsg());
        this.errorCodes = errorCodes;
    }
}
