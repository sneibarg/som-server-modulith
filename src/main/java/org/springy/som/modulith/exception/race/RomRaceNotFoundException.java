package org.springy.som.modulith.exception.race;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "RomRace not found", code = "ROM_RACE_NOT_FOUND")
public class RomRaceNotFoundException extends NotFoundApiException {
    public RomRaceNotFoundException(String message) {
        super(message);
    }
}
