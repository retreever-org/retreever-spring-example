package dev.retreever.example.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ProductSnapshot(
        @JsonProperty("product_id") UUID productId,
        @JsonProperty("variant_id") UUID variantId,
        @JsonProperty("product_title") String productTitle,
        @JsonProperty("variant_title") String variantTitle,
        @JsonProperty("price_per_unit") double pricePerUnit,
        @JsonProperty("thumbnail") String thumbnail
) {
}
