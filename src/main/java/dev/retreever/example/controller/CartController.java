package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.AddCartItemRequest;
import dev.retreever.example.dto.response.CartResponse;
import dev.retreever.example.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.Long;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @RequestMapping("/carts")
    public ResponseEntity<ApiAck> createCart() {
        cartService.createCart();
        return ResponseEntity.ok(ApiAck.success("Cart created successfully."));
    }

    @PostMapping("/carts/{cartId}/items")
    public ResponseEntity<ApiAck> addItemToCart(Long cartId, AddCartItemRequest request) {
        cartService.addItemToCart(cartId, request);
        return ResponseEntity.ok(ApiAck.success("Item added to cart successfully."));
    }

    @DeleteMapping("/cart-items/{itemId}")
    public ResponseEntity<ApiAck> removeCartItem(Long itemId) {
        cartService.removeCartItem(itemId);
        return ResponseEntity.ok(ApiAck.success("Item removed from cart successfully."));
    }

    @GetMapping("/carts/{cartId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Long cartId) {
        var response = cartService.getCart(cartId);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully.", response));
    }

}
