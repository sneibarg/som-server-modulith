package org.springy.som.modulith.exception.special;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid Special class", code = "INVALID_SPECIAL")
public final class InvalidSpecialException extends InvalidRequestApiException {
    public InvalidSpecialException(String message) {
        super(message);
    }
}
