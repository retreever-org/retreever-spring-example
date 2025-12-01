package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.example.dto.shared.OrderStatus;
import dev.retreever.example.dto.shared.PaymentStatus;
import dev.retreever.example.dto.shared.ProductSnapshot;
import dev.retreever.example.dto.shared.StoreSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record OrderResponse(
        @JsonProperty("order_id") UUID orderId,
        @JsonProperty("order_date") Instant orderDate,
        @JsonProperty("total_amount") double totalAmount,
        @JsonProperty("order_status") OrderStatus orderStatus,
        @JsonProperty("payment_status") PaymentStatus paymentStatus,
        @JsonProperty("items") List<OrderItemDetails> items
) {

    public record OrderItemDetails(
            @JsonProperty("item_id") UUID itemId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("product_snapshot") ProductSnapshot productSnapshot,
            @JsonProperty("seller_info") StoreSnapshot sellerInfo
    ) {}

    public static OrderResponseBuilder builderInit(
            UUID orderId,
            Instant orderDate,
            double totalAmount,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus
    ) {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(orderDate, "orderDate cannot be null");
        Objects.requireNonNull(orderStatus, "orderStatus cannot be null");
        Objects.requireNonNull(paymentStatus, "paymentStatus cannot be null");

        return new OrderResponseBuilder(
                orderId,
                orderDate,
                totalAmount,
                orderStatus,
                paymentStatus
        );
    }

    public static class OrderResponseBuilder {
        private final UUID orderId;
        private final Instant orderDate;
        private final double totalAmount;
        private final OrderStatus orderStatus;
        private final PaymentStatus paymentStatus;
        private final List<OrderItemDetails> items = new ArrayList<>();

        private OrderResponseBuilder(
                UUID orderId,
                Instant orderDate,
                double totalAmount,
                OrderStatus orderStatus,
                PaymentStatus paymentStatus
        ) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
            this.orderStatus = orderStatus;
            this.paymentStatus = paymentStatus;
        }

        public OrderResponseBuilder addItem(
                UUID itemId,
                int quantity,
                ProductSnapshot productSnapshot,
                StoreSnapshot sellerInfo
        ) {
            Objects.requireNonNull(itemId, "itemId cannot be null");
            Objects.requireNonNull(productSnapshot, "productSnapshot cannot be null");
            Objects.requireNonNull(sellerInfo, "sellerInfo cannot be null");

            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be > 0");
            }

            items.add(new OrderItemDetails(
                    itemId,
                    quantity,
                    productSnapshot,
                    sellerInfo
            ));

            return this;
        }

        public OrderResponse build() {
            return new OrderResponse(
                    orderId,
                    orderDate,
                    totalAmount,
                    orderStatus,
                    paymentStatus,
                    List.copyOf(items)
            );
        }
    }
}
