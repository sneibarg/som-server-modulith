package org.springy.som.modulith.prompt.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid prompt", code = "INVALID_PROMPT")
public final class InvalidPromptException extends InvalidRequestApiException {
    public InvalidPromptException(String message) {
        super(message);
    }
}
