package org.springy.som.modulith.domain.command.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = HelpController.class)
public class HelpApiExceptionHandler extends BaseApiExceptionHandler {
}
