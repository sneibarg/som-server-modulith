package org.springy.som.modulith.domain.note.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "NoteDocument not found", code = "NOTE_NOT_FOUND")
public class NoteNotFoundException extends NotFoundApiException {
    public NoteNotFoundException(String message) {
        super(message);
    }
}
