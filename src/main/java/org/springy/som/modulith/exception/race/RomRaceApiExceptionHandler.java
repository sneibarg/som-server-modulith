package org.springy.som.modulith.exception.race;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.controller.RaceController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = RaceController.class)
public final class RomRaceApiExceptionHandler extends BaseApiExceptionHandler {
}
