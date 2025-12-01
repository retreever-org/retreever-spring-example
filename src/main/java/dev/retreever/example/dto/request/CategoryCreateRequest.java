package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryCreateRequest(
        @JsonProperty("name") String name
) {
}
