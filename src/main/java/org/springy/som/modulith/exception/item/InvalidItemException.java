package org.springy.som.modulith.exception.item;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid item class", code = "INVALID_ITEM")
public final class InvalidItemException extends InvalidRequestApiException {
    public InvalidItemException(String message) {
        super(message);
    }
}
