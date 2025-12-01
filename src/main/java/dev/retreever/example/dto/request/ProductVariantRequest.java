package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.annotation.FieldInfo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record ProductVariantRequest (
        @FieldInfo(
                example = "Red Mug - Ceramic",
                description = "User-friendly name of the product variant. Required."
        )
        @NotBlank(message = "Title must not be blank")
        @Size(min = 1, max = 255, message = "Title length must be between 1 and 255 characters")
        @JsonProperty("title") String title,

        @FieldInfo(
                example = "12.50",
                description = "The selling price of the variant. Must be greater than 0."
        )
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.0")
        @NotNull(message = "Price must not be null")
        @JsonProperty("price") double price,

        @FieldInfo(
                example = "50",
                description = "Current stock quantity to be set for this variant. Must be non-negative."
        )
        @Min(value = 0, message = "Quantity must be a non-negative number")
        @NotNull(message = "Quantity must not be null")
        @JsonProperty("quantity") int quantity,

        @FieldInfo(
                example = "This mug is made of high-quality ceramic, perfect for coffee.",
                description = "Detailed description of the product variant. Optional, max 2000 characters."
        )
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        @JsonProperty("description") String description,

        @FieldInfo(
                description = "Key-value pair map of variant characteristics like color, size, etc. Required."
        )
        @NotNull(message = "Attributes must not be null")
        @JsonProperty("attributes") Map<String, String> attributes
) {
}