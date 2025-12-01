package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.example.dto.shared.ProductSnapshot;
import dev.retreever.example.dto.shared.UserSnapshot;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for an order item. Specially for seller view.
 */
public record OrderItemResponse(
        @JsonProperty("item_id") UUID itemId,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("created_date") Instant createdDate,
        @JsonProperty("product_snapshot") ProductSnapshot productSnapshot,
        @JsonProperty("user_info") UserSnapshot userSnapshot
) {
}

