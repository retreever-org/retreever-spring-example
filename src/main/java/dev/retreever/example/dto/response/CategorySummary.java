package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.example.dto.shared.CategoryStatus;

import java.util.UUID;

public record CategorySummary(
        @JsonProperty("category_id") UUID categoryId,
        @JsonProperty("name") String name,
        @JsonProperty("status") CategoryStatus categoryStatus,
        @JsonProperty("category_level") Integer categoryLevel,
        @JsonProperty("thumbnail") String thumbnail
) {
}
