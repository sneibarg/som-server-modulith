package org.springy.som.modulith.domain.spell.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid area", code = "INVALID_AREA")
public final class InvalidSpellException extends InvalidRequestApiException {
    public InvalidSpellException(String message) {
        super(message);
    }
}
