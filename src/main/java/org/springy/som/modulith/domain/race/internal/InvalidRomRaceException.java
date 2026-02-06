package org.springy.som.modulith.domain.race.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid RaceDocument class", code = "INVALID_ROM_RACE")
public final class InvalidRomRaceException extends InvalidRequestApiException {
    public InvalidRomRaceException(String message) {
        super(message);
    }
}
