package org.springy.som.modulith.exception.shop;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid Shop class", code = "INVALID_SHOP")
public final class InvalidShopException extends InvalidRequestApiException {
    public InvalidShopException(String message) {
        super(message);
    }
}
