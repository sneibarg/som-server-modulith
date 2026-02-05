package org.springy.som.modulith.exception.item;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "ItemDocument not found", code = "ITEM_NOT_FOUND")
public class ItemNotFoundException extends NotFoundApiException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
