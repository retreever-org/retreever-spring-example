package dev.retreever.example.exception;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(String cause) {
        super("Product not found", cause);
    }
}
