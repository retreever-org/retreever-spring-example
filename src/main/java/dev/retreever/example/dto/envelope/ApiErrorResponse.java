package dev.retreever.example.dto.envelope;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiErrorResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("message") String message,
        @JsonProperty("cause") String cause
) {

    public static ApiErrorResponse build(String message, String cause) {
        return new ApiErrorResponse(false, message, cause);
    }
}
