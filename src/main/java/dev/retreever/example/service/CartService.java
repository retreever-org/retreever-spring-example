package dev.retreever.example.service;

import dev.retreever.example.dto.request.AddCartItemRequest;
import dev.retreever.example.dto.response.CartResponse;
import dev.retreever.example.service.support.MockDataFactory;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class CartService {
    private final MockDataFactory mockDataFactory;

    public CartService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public void createCart() {
    }

    public void addItemToCart(Long cartId, AddCartItemRequest request) {
    }

    public void removeCartItem(Long itemId) {
    }

    public CartResponse getCart(Long cartId) {
        return mockDataFactory.cartResponse(cartId == null ? 1L : cartId);
    }
}
