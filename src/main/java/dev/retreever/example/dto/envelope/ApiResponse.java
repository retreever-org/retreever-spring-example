package dev.retreever.example.dto.envelope;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * API response that carries a data payload.
 * Used for successful responses that return a resource or result.
 *
 * @param success whether the operation succeeded
 * @param message human-readable message
 * @param data    payload returned on success, may be {@code null}
 */
public record ApiResponse<T>(
        @JsonProperty("success") boolean success,
        @JsonProperty("message") String message,
        @JsonProperty("data") T data
) {

    /**
     * Success response with custom message and payload.
     *
     * @param message custom message
     * @param data    payload to return
     * @param <T>     payload type
     * @return ApiResponse indicating success
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
}
