package dev.retreever.example.service;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.response.OrderItemResponse;
import dev.retreever.example.dto.response.OrderResponse;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class OrderService {
    public void createOrder(Long cartId, String s) {
    }

    public void confirmOrder(Long orderId) {
    }

    public void shipOrder(Long orderId) {
    }

    public void completeOrderDelivery(Long orderId) {
    }

    public void cancelOrder(Long orderId) {
    }

    public OrderResponse getOrder(Long orderId) {
        return null;
    }

    public CustomPage<OrderResponse> getMyOrder(int page, int size) {
        return null;
    }

    public CustomPage<OrderItemResponse> getOrdersForSeller(Long storeId, int page, int size) {
        return null;
    }
}
