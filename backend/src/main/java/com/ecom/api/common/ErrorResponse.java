package com.ecom.api.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * T006 – Standard error envelope returned for all API error responses.
 * <pre>
 * {
 *   "code": "VALIDATION_ERROR",
 *   "message": "...",
 *   "errors": [{ "field": "email", "message": "must not be blank" }],
 *   "timestamp": "2026-04-02T10:00:00Z",
 *   "correlationId": "uuid"
 * }
 * </pre>
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    String code;
    String message;
    List<FieldError> errors;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant timestamp;

    String correlationId;

    @Value
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        String field;
        String message;
    }
}
