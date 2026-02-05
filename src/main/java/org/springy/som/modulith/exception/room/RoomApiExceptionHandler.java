package org.springy.som.modulith.exception.room;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.domain.room.internal.RoomController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = RoomController.class)
public final class RoomApiExceptionHandler extends BaseApiExceptionHandler {
}
