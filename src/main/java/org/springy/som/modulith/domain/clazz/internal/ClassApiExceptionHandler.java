package org.springy.som.modulith.domain.clazz.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = ClassController.class)
public class ClassApiExceptionHandler extends BaseApiExceptionHandler {
}
