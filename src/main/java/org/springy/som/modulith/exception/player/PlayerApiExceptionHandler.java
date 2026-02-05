package org.springy.som.modulith.exception.player;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.domain.player.internal.PlayerController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = PlayerController.class)
public final class PlayerApiExceptionHandler extends BaseApiExceptionHandler {
}
