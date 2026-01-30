package org.springy.som.modulith.exception.reset;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.controller.ResetController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = ResetController.class)
public final class ResetApiExceptionHandler extends BaseApiExceptionHandler {
}
