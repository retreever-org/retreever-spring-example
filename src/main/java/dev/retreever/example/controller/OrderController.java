package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.envelope.PageResponse;
import dev.retreever.example.dto.request.OrderCreationRequest;
import dev.retreever.example.dto.response.OrderItemResponse;
import dev.retreever.example.dto.response.OrderResponse;
import dev.retreever.example.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.Long;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @PostMapping("/carts/{cartId}/orders")
    public ResponseEntity<ApiAck> createOrder(@PathVariable Long cartId, @Valid @RequestBody OrderCreationRequest request) {
        orderService.createOrder(cartId, request.shippingAddress());
        return ResponseEntity.ok(ApiAck.success("Order created successfully."));
    }

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @PostMapping("/orders/{orderId}/confirm")
    public ResponseEntity<ApiAck> confirmOrder(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok(ApiAck.success("Order confirmed successfully."));
    }

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @PostMapping("/orders/{orderId}/ship")
    public ResponseEntity<ApiAck> shipOrder(@PathVariable Long orderId) {
        orderService.shipOrder(orderId);
        return ResponseEntity.ok(ApiAck.success("Order shipped successfully."));
    }

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @PostMapping("/orders/{orderId}/complete")
    public ResponseEntity<ApiAck> completeOrderDelivery(@PathVariable Long orderId) {
        orderService.completeOrderDelivery(orderId);
        return ResponseEntity.ok(ApiAck.success("Order completed successfully."));
    }

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @DeleteMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiAck> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiAck.success("Order cancelled successfully."));
    }

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        var response = orderService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully.", response));
    }

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @GetMapping("/orders")
    public ResponseEntity<PageResponse<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var orders = orderService.getMyOrder(page, size);
        return ResponseEntity.ok(PageResponse.create("Orders retrieved successfully.", orders));
    }

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @GetMapping("/orders/seller/{storeId}")
    public ResponseEntity<PageResponse<OrderItemResponse>> getOrdersForSeller(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var orders = orderService.getOrdersForSeller(storeId, page, size);
        return ResponseEntity.ok(PageResponse.create("Orders retrieved successfully.", orders));
    }
}
