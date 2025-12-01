package dev.retreever.example.service;

import dev.retreever.example.dto.request.ProductVariantRequest;
import dev.retreever.example.dto.response.ProductVariantResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductVariantService {
    public ProductVariantResponse createProductVariant(UUID productId, @Valid ProductVariantRequest request) {
        return null;
    }

    public ProductVariantResponse getVariant(UUID variantId) {
        return null;
    }

    public ProductVariantResponse updateProductVariant(UUID variantId, @Valid ProductVariantRequest request) {
        return null;
    }

    public void deleteProductVariant(UUID variantId) {
    }
}
