package org.springy.som.modulith.domain.clazz.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid ROM class", code = "INVALID_ROM_CLASS")
public final class InvalidClassException extends InvalidRequestApiException {
    public InvalidClassException(String message) {
        super(message);
    }
}
