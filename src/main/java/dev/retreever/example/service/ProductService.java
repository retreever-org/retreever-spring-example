package dev.retreever.example.service;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.request.ProductFilters;
import dev.retreever.example.dto.request.ProductRequest;
import dev.retreever.example.dto.response.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    public UUID createProduct(UUID storeId, UUID categoryId, @Valid ProductRequest request) {
        return null;
    }

    public ProductResponse getProduct(UUID productId) {
        return null;
    }

    public void publishProduct(UUID productId) {
    }

    public List<ProductResponse> getProductsByFilter(ProductFilters filters, int page, int size) {
        return null;
    }

    public ProductResponse updateProduct(UUID productId, @Valid ProductRequest request) {
        return null;
    }

    public void deleteProduct(UUID productId) {
    }

    public List<String> getBrands() {
        return null;
    }

    public CustomPage<ProductResponse> getProductsByStore(UUID storeId, int page, int size) {
        return null;
    }
}
