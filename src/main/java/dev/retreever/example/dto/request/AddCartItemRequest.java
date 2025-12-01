package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AddCartItemRequest(
        @JsonProperty("product_variant_id") UUID productVariantId,
        @JsonProperty("store_id") UUID storeId,
        @JsonProperty("quantity") int quantity
) {
}
