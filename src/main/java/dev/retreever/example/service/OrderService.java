package dev.retreever.example.service;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.response.OrderItemResponse;
import dev.retreever.example.dto.response.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    public void createOrder(UUID cartId, String s) {
    }

    public void confirmOrder(UUID orderId) {
    }

    public void shipOrder(UUID orderId) {
    }

    public void completeOrderDelivery(UUID orderId) {
    }

    public void cancelOrder(UUID orderId) {
    }

    public OrderResponse getOrder(UUID orderId) {
        return null;
    }

    public CustomPage<OrderResponse> getMyOrder(int page, int size) {
        return null;
    }

    public CustomPage<OrderItemResponse> getOrdersForSeller(UUID storeId, int page, int size) {
        return null;
    }
}
