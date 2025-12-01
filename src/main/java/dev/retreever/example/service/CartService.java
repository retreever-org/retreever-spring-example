package dev.retreever.example.service;

import dev.retreever.example.dto.request.AddCartItemRequest;
import dev.retreever.example.dto.response.CartResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartService {
    public void createCart() {
    }

    public void addItemToCart(UUID cartId, AddCartItemRequest request) {
    }

    public void removeCartItem(UUID itemId) {
    }

    public CartResponse getCart(UUID cartId) {
        return null;
    }
}
