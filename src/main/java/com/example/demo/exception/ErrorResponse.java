package com.example.demo.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String errorCode;
    private final String message;
    private final List<String> validations;

    public static ErrorResponse of(ErrorCode errorCode, List<String> validations) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .errorCode(errorCode.name())
                .message(errorCode.getMessage())
                .validations(validations)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, null);
    }
}
