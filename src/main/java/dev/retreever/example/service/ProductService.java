package dev.retreever.example.service;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.request.ProductFilters;
import dev.retreever.example.dto.request.ProductRequest;
import dev.retreever.example.dto.response.ProductResponse;
import dev.retreever.example.exception.ProductNotFoundException;
import dev.retreever.example.service.support.MockDataFactory;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class ProductService {
    private final MockDataFactory mockDataFactory;

    public ProductService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public Long createProduct(Long storeId, Long categoryId, @Valid ProductRequest request) {
        return mockDataFactory.nextId();
    }

    public ProductResponse getProduct(Long productId) {
        if (productId != null && productId <= 0) {
            throw new ProductNotFoundException("product_id must be greater than 0");
        }
        return mockDataFactory.productResponse(productId == null ? 1L : productId);
    }

    public void publishProduct(Long productId) {
    }

    public List<ProductResponse> getProductsByFilter(ProductFilters filters, int page, int size) {
        return List.of(
                mockDataFactory.productResponse(page + 1L),
                mockDataFactory.productResponse(page + 2L)
        );
    }

    public ProductResponse updateProduct(Long productId, @Valid ProductRequest request) {
        return mockDataFactory.productResponse(productId == null ? 1L : productId);
    }

    public void deleteProduct(Long productId) {
    }

    public List<String> getBrands() {
        return mockDataFactory.brands();
    }

    public CustomPage<ProductResponse> getProductsByStore(Long storeId, int page, int size) {
        return mockDataFactory.productPage(page, size, storeId == null ? 1L : storeId);
    }
}
