package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderCreationRequest(
        @JsonProperty("shipping_address") String shippingAddress
) {
}
