package org.springy.som.modulith.exception.shop;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "ShopDocument not found", code = "SHOP_NOT_FOUND")
public class ShopNotFoundException extends NotFoundApiException {
    public ShopNotFoundException(String message) {
        super(message);
    }
}
