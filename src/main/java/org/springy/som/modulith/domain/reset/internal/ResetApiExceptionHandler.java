package org.springy.som.modulith.domain.reset.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = ResetController.class)
public final class ResetApiExceptionHandler extends BaseApiExceptionHandler {
}
