package org.springy.som.modulith.domain.mobile.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = MobileController.class)
public final class MobileApiExceptionHandler extends BaseApiExceptionHandler {
}
