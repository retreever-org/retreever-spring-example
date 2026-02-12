package dev.retreever.example.service;

import dev.retreever.example.dto.request.ProductVariantRequest;
import dev.retreever.example.dto.response.ProductVariantResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class ProductVariantService {
    public ProductVariantResponse createProductVariant(Long productId, @Valid ProductVariantRequest request) {
        return null;
    }

    public ProductVariantResponse getVariant(Long variantId) {
        return null;
    }

    public ProductVariantResponse updateProductVariant(Long variantId, @Valid ProductVariantRequest request) {
        return null;
    }

    public void deleteProductVariant(Long variantId) {
    }
}
