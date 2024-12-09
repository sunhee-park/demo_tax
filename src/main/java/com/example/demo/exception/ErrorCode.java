package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields."),

    // 401 Unauthorized
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid JWT token"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "You are not logged in"),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Permission denied for this resource"),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested resource was not found"),

    // 409 Conflict
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");

    private final HttpStatus httpStatus;
    private final String message;
}
