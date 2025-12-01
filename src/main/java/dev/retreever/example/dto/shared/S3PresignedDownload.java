package dev.retreever.example.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record S3PresignedDownload(
        @JsonProperty("url") String url,
        @JsonProperty("download_window") Long downloadWindow
) {
}
