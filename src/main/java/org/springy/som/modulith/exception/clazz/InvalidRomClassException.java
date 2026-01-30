package org.springy.som.modulith.exception.clazz;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid ROM class", code = "INVALID_ROM_CLASS")
public final class InvalidRomClassException extends InvalidRequestApiException {
    public InvalidRomClassException(String message) {
        super(message);
    }
}
