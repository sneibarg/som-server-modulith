package org.springy.som.modulith.domain.shop.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = ShopController.class)
public final class ShopApiExceptionHandler extends BaseApiExceptionHandler {
}
