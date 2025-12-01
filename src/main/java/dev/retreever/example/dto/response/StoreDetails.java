package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record StoreDetails(
        @JsonProperty("store_id") UUID storeId,
        @JsonProperty("name") String name,
        @JsonProperty("location") String location,
        @JsonProperty("contact_number") String contactNumber,
        @JsonProperty("email") String email,
        @JsonProperty("about") String about,
        @JsonProperty("created_date") Instant createdDate,
        @JsonProperty("last_modified_date") Instant lastModifiedDate
) {
}
