package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.retreever.example.dto.shared.CategoryStatus;

public record CategoryUpdateRequest(
        @JsonProperty("name") String name,
        @JsonProperty("status") CategoryStatus categoryStatus
) {
}
