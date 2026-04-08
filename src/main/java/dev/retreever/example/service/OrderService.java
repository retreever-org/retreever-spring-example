package dev.retreever.example.service;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.response.OrderItemResponse;
import dev.retreever.example.dto.response.OrderResponse;
import dev.retreever.example.security.MockIdentityService;
import dev.retreever.example.service.support.MockDataFactory;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class OrderService {
    private final MockDataFactory mockDataFactory;
    private final MockIdentityService identityService;

    public OrderService(MockDataFactory mockDataFactory, MockIdentityService identityService) {
        this.mockDataFactory = mockDataFactory;
        this.identityService = identityService;
    }

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
        return mockDataFactory.orderResponse(orderId == null ? 1L : orderId);
    }

    public CustomPage<OrderResponse> getMyOrder(int page, int size) {
        return mockDataFactory.orderPage(page, size, 20L);
    }

    public CustomPage<OrderItemResponse> getOrdersForSeller(Long storeId, int page, int size) {
        return mockDataFactory.sellerOrderPage(
                page,
                size,
                storeId == null ? 1L : storeId,
                identityService.loadAccountByEmail("seller@quickcart.test").email()
        );
    }
}
