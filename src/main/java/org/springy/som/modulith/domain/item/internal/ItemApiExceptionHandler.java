package org.springy.som.modulith.domain.item.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = ItemController.class)
public class ItemApiExceptionHandler extends BaseApiExceptionHandler {
}
