package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.AddCartItemRequest;
import dev.retreever.example.dto.response.CartResponse;
import dev.retreever.example.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.Long;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @PostMapping("/carts")
    public ResponseEntity<ApiAck> createCart() {
        cartService.createCart();
        return ResponseEntity.ok(ApiAck.success("Cart created successfully."));
    }

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @PostMapping("/carts/{cartId}/items")
    public ResponseEntity<ApiAck> addItemToCart(
            @PathVariable Long cartId,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        cartService.addItemToCart(cartId, request);
        return ResponseEntity.ok(ApiAck.success("Item added to cart successfully."));
    }

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @DeleteMapping("/cart-items/{itemId}")
    public ResponseEntity<ApiAck> removeCartItem(@PathVariable Long itemId) {
        cartService.removeCartItem(itemId);
        return ResponseEntity.ok(ApiAck.success("Item removed from cart successfully."));
    }

    @PreAuthorize("hasAnyAuthority('customer','seller','admin')")
    @GetMapping("/carts/{cartId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable Long cartId) {
        var response = cartService.getCart(cartId);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully.", response));
    }

}
