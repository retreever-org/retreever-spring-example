package dev.retreever.example.controller;

import dev.retreever.annotation.ApiEndpoint;
import dev.retreever.annotation.ApiGroup;
import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.ProductVariantRequest;
import dev.retreever.example.dto.response.ProductVariantResponse;
import dev.retreever.example.exception.ProductNotFoundException;
import dev.retreever.example.exception.ProductVariantNotFoundException;
import dev.retreever.example.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@ApiGroup(
        name = "Product Variant APIs",
        description = "APIs for managing product variants"
)
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ProductVariantController {
    private final ProductVariantService variantService;

    @ApiEndpoint(
            name = "Create Product Variant",
            description = "Create a new product variant",
            secured = true,
            headers = HttpHeaders.AUTHORIZATION,
            errors = {
                    AccessDeniedException.class,
                    ProductNotFoundException.class,
                    MethodArgumentNotValidException.class
            }
    )
    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createVariant(
            @PathVariable UUID productId,
            @RequestBody @Valid ProductVariantRequest request
    ) {
        var response = variantService.createProductVariant(productId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/products/" + productId))
                .body(ApiResponse.success("Product Variant Created.", response));
    }

    /**
     * Get product by variant ID.
     * Useful when product details need to be found when associated with orders, sales, etc.
     */
    @ApiEndpoint(
            name = "Get Product Variant",
            description = "Get a product variant by its ID",
            errors = ProductVariantNotFoundException.class
    )
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getVariant(@PathVariable UUID variantId) {
        var response = variantService.getVariant(variantId);
        return ResponseEntity.ok(ApiResponse.success(
                response.isDeleted() ? "Retrieved deleted product record." : "Product found.",
                response
        ));
    }

    @ApiEndpoint(
            name = "Update Product Variant",
            description = "Update an existing product variant by ID",
            secured = true,
            headers = HttpHeaders.AUTHORIZATION,
            errors = {
                    AccessDeniedException.class,
                    ProductVariantNotFoundException.class,
                    MethodArgumentNotValidException.class
            }
    )
    @PutMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateVariant(
            @PathVariable UUID variantId,
            @RequestBody @Valid ProductVariantRequest request
    ) {
        var response = variantService.updateProductVariant(variantId, request);
        return ResponseEntity.ok(ApiResponse.success("Product Variant Updated.", response));
    }

    @ApiEndpoint(
            name = "Delete Product Variant",
            description = "Delete an existing product variant by ID",
            secured = true,
            headers = HttpHeaders.AUTHORIZATION,
            errors = {
                    AccessDeniedException.class,
                    ProductVariantNotFoundException.class,
                    ProductNotFoundException.class
            }
    )
    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<ApiAck> deleteVariant(@PathVariable UUID variantId) {
        variantService.deleteProductVariant(variantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiAck.success("Product Variant Deleted."));
    }

}
