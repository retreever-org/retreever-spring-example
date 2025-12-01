package dev.retreever.example.dto.envelope;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CustomPage<T>(
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("total_elements") long totalElements,
        @JsonProperty("total_pages") int totalPages,
        @JsonProperty("content") List<T> content
) {
    public static <T> CustomPage<T> create(int page, int size, long totalElements, int totalPages, List<T> content) {
        return new CustomPage<>(page, size, totalElements, totalPages, content);
    }
}
