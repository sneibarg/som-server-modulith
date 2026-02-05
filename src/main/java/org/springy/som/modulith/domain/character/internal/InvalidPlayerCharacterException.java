package org.springy.som.modulith.domain.character.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid player character", code = "INVALID_PLAYER_CHARACTER")
public class InvalidPlayerCharacterException extends InvalidRequestApiException {
    public InvalidPlayerCharacterException(String message) {
        super(message);
    }
}
