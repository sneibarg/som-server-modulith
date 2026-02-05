package org.springy.som.modulith.domain.player.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = PlayerController.class)
public final class PlayerApiExceptionHandler extends BaseApiExceptionHandler {
}
