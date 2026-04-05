package org.springy.som.modulith.domain.area.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.ConflictApiException;

@ApiError(status = HttpStatus.CONFLICT, title = "Duplicate area vnum", code = "DUPLICATE_AREA_VNUM")
public final class DuplicateAreaException extends ConflictApiException {
    public DuplicateAreaException(String message) {
        super(message);
    }
}
