package org.springy.som.modulith.domain.special.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "SpecialDocument not found", code = "SPECIAL_NOT_FOUND")
public class SpecialNotFoundException extends NotFoundApiException {
    public SpecialNotFoundException(String message) {
        super(message);
    }
}
