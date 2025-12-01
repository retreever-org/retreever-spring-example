package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.envelope.PageResponse;
import dev.retreever.example.dto.request.ProductFilters;
import dev.retreever.example.dto.request.ProductRequest;
import dev.retreever.example.dto.response.ProductResponse;
import dev.retreever.example.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/stores/{storeId}/categories/{categoryId}/products")
    public ResponseEntity<ApiAck> createProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID categoryId,
            @RequestBody @Valid ProductRequest request
    ) {
        UUID id = productService.createProduct(storeId, categoryId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/public/products/" + id))
                .body(ApiAck.success("Product Created."));
    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductForPublic(@PathVariable UUID productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(
                "Product Found",
                response
        ));
    }

    @PostMapping("/products/{productId}/publish")
    public ResponseEntity<ApiAck> publishProduct(@PathVariable UUID productId) {
        productService.publishProduct(productId);
        return ResponseEntity.ok(ApiAck.success("Product Published."));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(
                "Product Found",
                response
        ));
    }

    @GetMapping("/public/products/filter")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByFilter(
            @ModelAttribute ProductFilters filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {

        log.debug("Searching products by filters: brand: {} categories: {} rating: {} minPrice: {} maxPrice: {}",
                filters.brand(), Arrays.toString(filters.categories()), filters.rating(), filters.minPrice(), filters.maxPrice());

        List<ProductResponse> response = productService.getProductsByFilter(filters, page, size);
        return ResponseEntity.ok(ApiResponse.success("Products Found", response));
    }


    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @RequestBody @Valid ProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Product Updated",
                response
        ));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiAck> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiAck.success("Product Deleted."));
    }

    @GetMapping("/public/brands")
    public ResponseEntity<ApiResponse<List<String>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.success("Brands Found", productService.getBrands()));
    }

    @GetMapping("/stores/{storeId}/products")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByStore(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        log.debug("Fetching products by store: {}", storeId);
        CustomPage<ProductResponse> response = productService.getProductsByStore(storeId, page, size);
        return ResponseEntity.ok(PageResponse.create("Products Found", response));
    }
}
