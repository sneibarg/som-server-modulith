package org.springy.som.modulith.domain.race.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = RaceController.class)
public final class RaceApiExceptionHandler extends BaseApiExceptionHandler {
}
