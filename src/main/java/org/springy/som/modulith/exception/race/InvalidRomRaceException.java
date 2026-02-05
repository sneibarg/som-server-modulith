package org.springy.som.modulith.exception.race;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid RomRaceDocument class", code = "INVALID_ROM_RACE")
public final class InvalidRomRaceException extends InvalidRequestApiException {
    public InvalidRomRaceException(String message) {
        super(message);
    }
}
