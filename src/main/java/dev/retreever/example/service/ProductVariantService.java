package dev.retreever.example.service;

import dev.retreever.example.dto.request.ProductVariantRequest;
import dev.retreever.example.dto.response.ProductVariantResponse;
import dev.retreever.example.exception.ProductNotFoundException;
import dev.retreever.example.exception.ProductVariantNotFoundException;
import dev.retreever.example.service.support.MockDataFactory;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class ProductVariantService {
    private final MockDataFactory mockDataFactory;

    public ProductVariantService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public ProductVariantResponse createProductVariant(Long productId, @Valid ProductVariantRequest request) {
        if (productId != null && productId <= 0) {
            throw new ProductNotFoundException("product_id must be greater than 0");
        }
        return mockDataFactory.productVariantResponse(mockDataFactory.nextId());
    }

    public ProductVariantResponse getVariant(Long variantId) {
        if (variantId != null && variantId <= 0) {
            throw new ProductVariantNotFoundException("variant_id must be greater than 0");
        }
        return mockDataFactory.productVariantResponse(variantId == null ? 1L : variantId);
    }

    public ProductVariantResponse updateProductVariant(Long variantId, @Valid ProductVariantRequest request) {
        if (variantId != null && variantId <= 0) {
            throw new ProductVariantNotFoundException("variant_id must be greater than 0");
        }
        return mockDataFactory.productVariantResponse(variantId == null ? 1L : variantId);
    }

    public void deleteProductVariant(Long variantId) {
    }
}
