package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.envelope.PageResponse;
import dev.retreever.example.dto.request.OrderCreationRequest;
import dev.retreever.example.dto.response.OrderItemResponse;
import dev.retreever.example.dto.response.OrderResponse;
import dev.retreever.example.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/carts/{cartId}/orders")
    public ResponseEntity<ApiAck> createOrder(@PathVariable UUID cartId, OrderCreationRequest request) {
        orderService.createOrder(cartId, request.shippingAddress());
        return ResponseEntity.ok(ApiAck.success("Order created successfully."));
    }

    @PostMapping("/orders/{orderId}/confirm")
    public ResponseEntity<ApiAck> confirmOrder(@PathVariable UUID orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok(ApiAck.success("Order confirmed successfully."));
    }

    @PostMapping("/orders/{orderId}/ship")
    public ResponseEntity<ApiAck> shipOrder(@PathVariable UUID orderId) {
        orderService.shipOrder(orderId);
        return ResponseEntity.ok(ApiAck.success("Order shipped successfully."));
    }

    @PostMapping("/orders/{orderId}/complete")
    public ResponseEntity<ApiAck> completeOrderDelivery(@PathVariable UUID orderId) {
        orderService.completeOrderDelivery(orderId);
        return ResponseEntity.ok(ApiAck.success("Order completed successfully."));
    }

    @DeleteMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiAck> cancelOrder(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiAck.success("Order cancelled successfully."));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID orderId) {
        var response = orderService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully.", response));
    }

    @GetMapping("/orders")
    public ResponseEntity<PageResponse<OrderResponse>> getMyOrders(int page, int size) {
        var orders = orderService.getMyOrder(page, size);
        return ResponseEntity.ok(PageResponse.create("Orders retrieved successfully.", orders));
    }

    @GetMapping("/orders/seller/{storeId}")
    public ResponseEntity<PageResponse<OrderItemResponse>> getOrdersForSeller(@PathVariable UUID storeId, int page, int size) {
        var orders = orderService.getOrdersForSeller(storeId, page, size);
        return ResponseEntity.ok(PageResponse.create("Orders retrieved successfully.", orders));
    }
}
