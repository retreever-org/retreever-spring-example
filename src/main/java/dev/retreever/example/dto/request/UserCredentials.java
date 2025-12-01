package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration.
 */
public record UserCredentials(
        @JsonProperty("email")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @JsonProperty("password")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}