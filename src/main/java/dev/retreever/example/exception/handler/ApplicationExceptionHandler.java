package dev.retreever.example.exception.handler;

import dev.retreever.annotation.ApiError;
import dev.retreever.example.dto.envelope.ApiErrorResponse;
import dev.retreever.example.exception.ProductNotFoundException;
import dev.retreever.example.exception.ProductVariantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ApiError(
            status = HttpStatus.FORBIDDEN,
            description = "Access Denied"
    )
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.build("Access Denied", ex.getMessage()));
    }

    @ApiError(
            status = HttpStatus.NOT_FOUND,
            description = "Product Not Found"
    )
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getApiErrorResponse());
    }

    @ApiError(
            status = HttpStatus.NOT_FOUND,
            description = "Product Variant Not Found"
    )
    @ExceptionHandler(ProductVariantNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductVariantNotFound(ProductVariantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getApiErrorResponse());
    }

}
