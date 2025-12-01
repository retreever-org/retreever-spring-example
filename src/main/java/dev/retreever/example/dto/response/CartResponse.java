package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.example.dto.shared.ProductSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CartResponse(
        @JsonProperty("cart_id") UUID cartId,
        @JsonProperty("items") List<CartItemDetail> cartItemDetails
) {

    public record CartItemDetail(
            @JsonProperty("item_id") UUID itemId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("product_snapshot") ProductSnapshot productSnapshot
    ) {
    }

    public static CartResponseBuilder builder(UUID cartId) {
        return new CartResponseBuilder(cartId);
    }

    public static class CartResponseBuilder {
        private final UUID cartId;
        private final List<CartItemDetail> items = new ArrayList<>();

        public CartResponseBuilder(UUID cartId) {
            this.cartId = cartId;
        }

        public CartResponseBuilder addItem(UUID itemId, int quantity, ProductSnapshot productSnapshot) {
            Objects.requireNonNull(itemId, "itemId");
            Objects.requireNonNull(productSnapshot, "productSnapshot");
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

            var item = new CartItemDetail(
                    itemId,
                    quantity,
                    productSnapshot
            );

            items.add(item);
            return this;
        }

        public CartResponse build() {
            List<CartItemDetail> immutableItems = List.copyOf(items);
            return new CartResponse(cartId, immutableItems);
        }
    }
}