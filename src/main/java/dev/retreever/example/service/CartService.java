package dev.retreever.example.service;

import dev.retreever.example.dto.request.AddCartItemRequest;
import dev.retreever.example.dto.response.CartResponse;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class CartService {
    public void createCart() {
    }

    public void addItemToCart(Long cartId, AddCartItemRequest request) {
    }

    public void removeCartItem(Long itemId) {
    }

    public CartResponse getCart(Long cartId) {
        return null;
    }
}
