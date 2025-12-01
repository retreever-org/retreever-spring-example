package dev.retreever.example.dto.envelope;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageResponse<T>(
        @JsonProperty("success") boolean success,
        @JsonProperty("message") String message,
        @JsonProperty("page") CustomPage<T> page
) {
    public static <T> PageResponse<T> create(String message, CustomPage<T> page) {
        return new PageResponse<>(true, message, page);
    }
}
