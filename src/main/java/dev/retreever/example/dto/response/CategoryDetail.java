package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.example.dto.shared.CategoryStatus;

import java.util.List;
import java.util.UUID;

public record CategoryDetail(
        @JsonProperty("category_id") UUID categoryId,
        @JsonProperty("name") String name,
        @JsonProperty("status") CategoryStatus categoryStatus,
        @JsonProperty("category_level") Integer categoryLevel,
        @JsonProperty("thumbnail") String thumbnail,
        @JsonProperty("child_category") List<CategoryDetail> childCategory
) {
}
