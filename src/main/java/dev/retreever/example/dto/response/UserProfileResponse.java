package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Public API response: base user fields + polymorphic role blocks.
 */
public record UserProfileResponse(
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,

        @JsonProperty("profiles")
        List<UserRoleProfile.Role> roleProfile
) {
}
