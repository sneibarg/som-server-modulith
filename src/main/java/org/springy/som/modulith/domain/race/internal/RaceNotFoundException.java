package org.springy.som.modulith.domain.race.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "RaceDocument not found", code = "ROM_RACE_NOT_FOUND")
public class RaceNotFoundException extends NotFoundApiException {
    public RaceNotFoundException(String message) {
        super(message);
    }
}
