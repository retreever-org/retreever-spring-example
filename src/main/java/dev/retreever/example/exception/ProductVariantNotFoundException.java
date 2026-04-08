package dev.retreever.example.exception;

public class ProductVariantNotFoundException extends BaseException {
    public ProductVariantNotFoundException(String cause) {
        super("Product variant not found", cause);
    }
}
