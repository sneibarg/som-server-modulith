package org.springy.som.modulith.domain.area.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "AreaDocument not found", code = "AREA_NOT_FOUND")
public class AreaNotFoundException extends NotFoundApiException {
    public AreaNotFoundException(String message) {
        super(message);
    }
}

