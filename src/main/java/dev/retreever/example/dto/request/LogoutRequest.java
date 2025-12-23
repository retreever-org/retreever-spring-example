package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.annotation.FieldInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LogoutRequest(
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+\\.([A-Za-z0-9_-]+)\\.[A-Za-z0-9_-]+$\n",
                message = "Invalid JWT Format"
        )
        @FieldInfo(description = "Access Token (optional)")
        @JsonProperty("access_token") String accessToken,

        @NotBlank(message = "Refresh Token cannot be blank")
        @NotNull(message = "Refresh Token cannot be null")
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+\\.([A-Za-z0-9_-]+)\\.[A-Za-z0-9_-]+$\n",
                message = "Invalid JWT Format"
        )
        @FieldInfo(description = "Refresh Token")
        @JsonProperty("refresh_token") String refreshToken
) {
}
