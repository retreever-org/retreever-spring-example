package dev.retreever.example.dto.envelope;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Lightweight API response used when no data payload is required.
 * <p>
 * Indicates whether the operation succeeded or failed, with a
 * message and optional extra details.
 */
public record ApiAck(
        @JsonProperty("success") boolean success,
        @JsonProperty("message") String message,
        @JsonProperty("additional_info") String additionalInfo
) {

    /**
     * Creates a success acknowledgement.
     *
     * @param message description of the successful operation
     * @return acknowledgement indicating success
     */
    public static ApiAck success(String message) {
        return new ApiAck(true, message, null);
    }

    /**
     * Creates a success acknowledgement with extra details.
     *
     * @param message        description of the successful operation
     * @param additionalInfo extra details such as reference IDs, may be {@code null}
     * @return acknowledgement indicating success
     */
    public static ApiAck success(String message, String additionalInfo) {
        return new ApiAck(true, message, additionalInfo);
    }

    /**
     * Creates an error acknowledgement.
     *
     * @param message description of the failure
     * @return acknowledgement indicating error
     */
    public static ApiAck error(String message) {
        return new ApiAck(false, message, null);
    }
}
