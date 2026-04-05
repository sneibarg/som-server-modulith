package org.springy.som.modulith.domain.room.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.ConflictApiException;

@ApiError(status = HttpStatus.CONFLICT, title = "Duplicate room vnum", code = "DUPLICATE_ROOM_VNUM")
public final class DuplicateRoomException extends ConflictApiException {
    public DuplicateRoomException(String message) {
        super(message);
    }
}
