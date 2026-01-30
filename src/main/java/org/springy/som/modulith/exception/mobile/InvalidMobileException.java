package org.springy.som.modulith.exception.mobile;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid mobile class", code = "INVALID_MOBILE")
public final class InvalidMobileException extends InvalidRequestApiException {
    public InvalidMobileException(String message) {
        super(message);
    }
}
