package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AddCartItemRequest(
        @JsonProperty("product_variant_id") Long productVariantId,
        @JsonProperty("store_id") Long storeId,
        @JsonProperty("quantity") int quantity
) {
}
