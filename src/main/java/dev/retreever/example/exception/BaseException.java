package dev.retreever.example.exception;

import dev.retreever.example.dto.envelope.ApiErrorResponse;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private ApiErrorResponse apiErrorResponse;

    public BaseException(String message, String cause) {
        this.apiErrorResponse = ApiErrorResponse.build(message, cause);
    }
}
