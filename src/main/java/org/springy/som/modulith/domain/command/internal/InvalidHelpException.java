package org.springy.som.modulith.domain.command.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid help class", code = "INVALID_HELP")
public final class InvalidHelpException extends InvalidRequestApiException {
    public InvalidHelpException(String message) {
        super(message);
    }
}
