package dev.retreever.example.exception.handler;

import dev.retreever.annotation.ApiError;
import dev.retreever.example.dto.envelope.ApiErrorResponse;
import dev.retreever.example.dto.envelope.ApiValidationError;
import dev.retreever.example.dto.envelope.FieldError;
import dev.retreever.example.exception.ProductNotFoundException;
import dev.retreever.example.exception.ProductVariantNotFoundException;
import dev.retreever.example.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ApiError(
            status = HttpStatus.BAD_REQUEST,
            description = "Request Validation Failed"
    )
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiValidationError> handleValidationErrors(Exception ex) {
        List<FieldError> fieldErrors;

        if (ex instanceof MethodArgumentNotValidException validationException) {
            fieldErrors = validationException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> new FieldError(error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                    .toList();
        } else if (ex instanceof BindException bindException) {
            fieldErrors = bindException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> new FieldError(error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                    .toList();
        } else {
            fieldErrors = List.of();
        }

        return ResponseEntity.badRequest()
                .body(ApiValidationError.of("Validation failed", fieldErrors));
    }

    @ApiError(
            status = HttpStatus.BAD_REQUEST,
            description = "Bad Request"
    )
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.build("Bad Request", ex.getMessage()));
    }

    @ApiError(
            status = HttpStatus.UNAUTHORIZED,
            description = "Authentication Failed"
    )
    @ExceptionHandler({
            BadCredentialsException.class,
            CredentialsExpiredException.class,
            AuthenticationCredentialsNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAuthenticationFailure(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.build("Unauthorized", ex.getMessage()));
    }

    @ApiError(
            status = HttpStatus.FORBIDDEN,
            description = "Access Denied"
    )
    @ExceptionHandler
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

    @ApiError(
            status = HttpStatus.NOT_FOUND,
            description = "User Not Found"
    )
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getApiErrorResponse());
    }

    @ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            description = "Unexpected Server Error"
    )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.build("Internal Server Error", ex.getMessage()));
    }
}
