package dev.retreever.example.dto.envelope;

public record FieldError(
        String field,
        Object rejectedValue,
        String message
) {
}
