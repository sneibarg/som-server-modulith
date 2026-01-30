package org.springy.som.modulith.exception.reset;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid Reset class", code = "INVALID_RESET")
public final class InvalidResetException extends InvalidRequestApiException {
    public InvalidResetException(String message) {
        super(message);
    }
}
