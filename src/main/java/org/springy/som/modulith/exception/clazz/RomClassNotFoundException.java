package org.springy.som.modulith.exception.clazz;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "ROM class not found", code = "ROM_CLASS_NOT_FOUND")
public class RomClassNotFoundException extends NotFoundApiException {
    public RomClassNotFoundException(String message) {
        super(message);
    }
}
