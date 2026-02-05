package org.springy.som.modulith.domain.reset.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "ResetDocument not found", code = "RESET_NOT_FOUND")
public class ResetNotFoundException extends NotFoundApiException {
    public ResetNotFoundException(String message) {
        super(message);
    }
}
