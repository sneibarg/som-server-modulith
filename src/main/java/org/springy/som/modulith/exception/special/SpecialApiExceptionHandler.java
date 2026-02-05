package org.springy.som.modulith.exception.special;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.domain.special.internal.SpecialController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = SpecialController.class)
public final class SpecialApiExceptionHandler extends BaseApiExceptionHandler {
}
