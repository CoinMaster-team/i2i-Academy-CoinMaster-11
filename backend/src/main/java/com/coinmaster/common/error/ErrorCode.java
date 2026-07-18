package com.coinmaster.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_SYMBOL(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    USERNAME_TAKEN(HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND),
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY),
    INSUFFICIENT_ASSET(HttpStatus.UNPROCESSABLE_ENTITY),
    DUPLICATE_ORDER(HttpStatus.CONFLICT),
    MARKET_PRICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),
    AI_NOT_CONFIGURED(HttpStatus.SERVICE_UNAVAILABLE),
    AI_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS),
    AI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
