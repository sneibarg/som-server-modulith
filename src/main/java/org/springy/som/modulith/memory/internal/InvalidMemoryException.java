package org.springy.som.modulith.memory.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid memory", code = "INVALID_MEMORY")
public final class InvalidMemoryException extends InvalidRequestApiException {
    public InvalidMemoryException(String message) {
        super(message);
    }
}
