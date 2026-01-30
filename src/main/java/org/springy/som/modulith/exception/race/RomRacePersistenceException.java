package org.springy.som.modulith.exception.race;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.ServiceUnavailableApiException;

@ApiError(status = HttpStatus.SERVICE_UNAVAILABLE, title = "Persistence service unavailable", code = "SERVICE_UNAVAILABLE")
public final class RomRacePersistenceException extends ServiceUnavailableApiException {
    public RomRacePersistenceException(String message) {
        super("Service unavailable "+message);
    }
}
