package dev.retreever.example.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserSnapshot(
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email
) {
}
