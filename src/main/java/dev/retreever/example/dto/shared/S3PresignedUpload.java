package dev.retreever.example.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

public record S3PresignedUpload(
        @JsonProperty("object_key") String objectKey,
        @JsonProperty("url") String url,
        @JsonProperty("upload_window") Long uploadWindow
) {
}
