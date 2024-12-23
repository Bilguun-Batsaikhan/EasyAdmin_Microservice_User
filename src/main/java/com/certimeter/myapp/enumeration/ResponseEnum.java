package com.certimeter.myapp.enumeration;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseEnum {
    SUCCESS(200, "Success", HttpStatus.OK),
    CREATED(201, "User added successfully.", HttpStatus.CREATED),
    AUTHORIZATION_FAILED(123, "Authorization failed", HttpStatus.FORBIDDEN),
    AUTHENTICATION_FAILED(401, "Authentication failed", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Missing permissions", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    INVALID_INPUT(422, "Input validation failed", HttpStatus.BAD_REQUEST),
    UNEXPECTED_ERROR(500, "Generic error", HttpStatus.INTERNAL_SERVER_ERROR),

    USERNAME_ALREADY_EXISTS(1400, "Username already exists", HttpStatus.CONFLICT),
    EXPIRED_ACCESS_TOKEN(1420, "Expired access token", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1421, "Invalid refresh token", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN(20103, "Invalid access token", HttpStatus.UNAUTHORIZED),
    MALFORMED_ACCESS_TOKEN(401, "Malformed access token", HttpStatus.UNAUTHORIZED),
    MALFORMED_REFRESH_TOKEN(401, "Malformed refresh token", HttpStatus.UNAUTHORIZED),
    // HttpStatus codes will help to understand the error type for whoever is consuming the API, in my case it's the BFF layer server.
    FOREIGN_KEY_CONSTRAINT_VIOLATION(1451, "Foreign key constraint violation", HttpStatus.CONFLICT),
    DUPLICATE_ENTRY(1062, "Duplicate entry", HttpStatus.CONFLICT);

    private final int id;
    private final String description;
    private final HttpStatus httpStatus;

    ResponseEnum(int id, String description, HttpStatus httpStatus) {
        this.id = id;
        this.description = description;
        this.httpStatus = httpStatus;
    }

}
