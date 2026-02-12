package dev.retreever.example.service;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.request.ProductFilters;
import dev.retreever.example.dto.request.ProductRequest;
import dev.retreever.example.dto.response.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class ProductService {
    public Long createProduct(Long storeId, Long categoryId, @Valid ProductRequest request) {
        return null;
    }

    public ProductResponse getProduct(Long productId) {
        return null;
    }

    public void publishProduct(Long productId) {
    }

    public List<ProductResponse> getProductsByFilter(ProductFilters filters, int page, int size) {
        return null;
    }

    public ProductResponse updateProduct(Long productId, @Valid ProductRequest request) {
        return null;
    }

    public void deleteProduct(Long productId) {
    }

    public List<String> getBrands() {
        return null;
    }

    public CustomPage<ProductResponse> getProductsByStore(Long storeId, int page, int size) {
        return null;
    }
}
