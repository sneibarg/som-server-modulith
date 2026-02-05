package org.springy.som.modulith.exception.command;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.domain.command.internal.CommandController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = CommandController.class)
public class CommandApiExceptionHandler extends BaseApiExceptionHandler {
}
