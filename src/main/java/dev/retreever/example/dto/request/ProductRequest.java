package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductRequest(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("brand") String brand
) {
}
