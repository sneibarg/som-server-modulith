package org.springy.som.modulith.domain.note.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid note class", code = "INVALID_NOTE")
public final class InvalidNoteException extends InvalidRequestApiException {
    public InvalidNoteException(String message) {
        super(message);
    }
}
