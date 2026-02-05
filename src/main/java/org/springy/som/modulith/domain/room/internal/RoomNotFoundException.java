package org.springy.som.modulith.domain.room.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "RoomDocument not found", code = "ROOM_NOT_FOUND")
public class RoomNotFoundException extends NotFoundApiException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
