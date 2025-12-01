package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.annotation.FieldInfo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ProductVariantResponse(

        @FieldInfo(
                example = "78e3c4a1-0f2b-4d9e-8c7a-51f6920d3b4f",
                description = "Primary Key of the Product Variant"
        )
        @NotNull(message = "Variant ID must not be null")
        @JsonProperty("variant_id") UUID variantId,

        @FieldInfo(
                example = "Blue T-Shirt - Large",
                description = "User-friendly name of the product variant"
        )
        @NotBlank(message = "Title must not be blank")
        @Size(min = 1, max = 255, message = "Title length must be between 1 and 255 characters")
        @JsonProperty("title") String title,

        @FieldInfo(
                example = "19.99",
                description = "The selling price of the variant"
        )
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.0")
        @NotNull(message = "Price must not be null")
        @JsonProperty("price") double price,

        @FieldInfo(
                example = "150",
                description = "Current stock quantity available for this variant"
        )
        @Min(value = 0, message = "Quantity must be a non-negative number")
        @NotNull(message = "Quantity must not be null")
        @JsonProperty("quantity") int quantity,

        @FieldInfo(
                example = "A comfortable cotton t-shirt in deep blue.",
                description = "Detailed description of the product variant. Nullable."
        )
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        @JsonProperty("description") String description,

        @FieldInfo(
                example = "2023-10-27T10:00:00Z",
                description = "Timestamp when the variant was first created"
        )
        @NotNull(message = "Created date must not be null")
        @PastOrPresent(message = "Created date must be in the past or present")
        @JsonProperty("created_date") Instant createdDate,

        @FieldInfo(
                example = "2023-10-27T10:30:00Z",
                description = "Timestamp when the variant was last updated"
        )
        @NotNull(message = "Last modified date must not be null")
        @PastOrPresent(message = "Last modified date must be in the past or present")
        @JsonProperty("last_modified_date") Instant lastModifiedDate,

        @FieldInfo(
                description = "Key-value pair map of variant characteristics"
        )
        @NotNull(message = "Attributes must not be null")
        @JsonProperty("attributes") Map<String, String> attributes,

        @FieldInfo(
                description = "Set of public URLs pointing to images of the variant"
        )
        @NotNull(message = "Image URIs must not be null")
        @NotEmpty(message = "Image URIs must not be empty")
        @JsonProperty("image_uris") Set<String> imageURIs,

        @FieldInfo(
                example = "true",
                description = "Indicates if the variant is currently available for sale"
        )
        @NotNull(message = "Is active status must not be null")
        @JsonProperty("is_active") boolean isActive,

        @FieldInfo(
                example = "false",
                description = "Indicates if the variant has been soft-deleted"
        )
        @NotNull(message = "Is deleted status must not be null")
        @JsonProperty("is_deleted") boolean isDeleted
) {
}