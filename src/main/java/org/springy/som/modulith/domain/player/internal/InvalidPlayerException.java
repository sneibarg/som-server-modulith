package org.springy.som.modulith.domain.player.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid player class", code = "INVALID_PLAYER")
public final class InvalidPlayerException extends InvalidRequestApiException {
    public InvalidPlayerException(String message) {
        super(message);
    }
}
