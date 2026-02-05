package org.springy.som.modulith.domain.command.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid command class", code = "INVALID_COMMAND")
public final class InvalidCommandException extends InvalidRequestApiException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
