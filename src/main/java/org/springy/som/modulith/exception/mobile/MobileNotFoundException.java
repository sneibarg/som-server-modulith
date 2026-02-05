package org.springy.som.modulith.exception.mobile;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "MobileDocument not found", code = "MOBILE_NOT_FOUND")
public class MobileNotFoundException extends NotFoundApiException {
    public MobileNotFoundException(String message) {
        super(message);
    }
}
