package org.springy.som.modulith.domain.mobile.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.ConflictApiException;

@ApiError(status = HttpStatus.CONFLICT, title = "Duplicate mobile vnum", code = "DUPLICATE_MOBILE_VNUM")
public final class DuplicateMobileException extends ConflictApiException {
    public DuplicateMobileException(String message) {
        super(message);
    }
}
