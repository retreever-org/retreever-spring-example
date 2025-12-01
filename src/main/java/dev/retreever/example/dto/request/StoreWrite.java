package dev.retreever.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StoreWrite(
        @JsonProperty("name") String name,
        @JsonProperty("location") String location,
        @JsonProperty("contact_number") String contactNumber,
        @JsonProperty("email") String email,
        @JsonProperty("about") String about
) {
}
