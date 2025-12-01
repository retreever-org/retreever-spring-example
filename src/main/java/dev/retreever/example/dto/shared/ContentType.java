package dev.retreever.example.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

public record ContentType(
        @JsonProperty("content_type") String value
) {

    public ContentType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
        try {
            MediaType.parseMediaType(value);
        } catch (InvalidMediaTypeException e) {
            throw new IllegalArgumentException("Invalid content type: " + value);
        }
    }

    public void mustStartWith(String prefix) {
        if (!value().startsWith(prefix))
            throw new IllegalArgumentException(
                    "Invalid content type: '%s'. Must start with '%s'.".formatted(value(), prefix)
            );
    }
}

