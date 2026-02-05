package org.springy.som.modulith.domain.room.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid RoomDocument class", code = "INVALID_ROOM")
public final class InvalidRoomException extends InvalidRequestApiException {
    public InvalidRoomException(String message) {
        super(message);
    }
}
