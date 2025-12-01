package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse (
        @JsonProperty("product_id") UUID productId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("brand") String brand,
        @JsonProperty("category_path") String categoryPath,
        @JsonProperty("avg_rating") double avgRating,
        @JsonProperty("rating_count") int ratingCount,
        @JsonProperty("created_date") Instant createdDate,
        @JsonProperty("last_modified_date") Instant lastModifiedDate,
        @JsonProperty("variants") List<ProductVariantResponse> variants,
        @JsonProperty("is_active") boolean isActive,
        @JsonProperty("is_deleted") boolean isDeleted
) {
}
