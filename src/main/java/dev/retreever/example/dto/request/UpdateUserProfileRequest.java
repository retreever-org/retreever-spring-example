package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for updating user profile.
 * All fields are optional for partial updates.
 */
public record UpdateUserProfileRequest(
        @JsonProperty("first_name")
        String firstName,
        
        @JsonProperty("last_name")
        String lastName,
        
        @JsonProperty("phone_number")
        String phoneNumber
) {
}