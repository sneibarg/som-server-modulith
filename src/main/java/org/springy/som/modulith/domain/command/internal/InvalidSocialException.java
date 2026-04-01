package org.springy.som.modulith.domain.command.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid social class", code = "INVALID_SOCIAL")
public final class InvalidSocialException extends InvalidRequestApiException {
    public InvalidSocialException(String message) {
        super(message);
    }
}
