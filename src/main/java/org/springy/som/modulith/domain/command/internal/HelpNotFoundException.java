package org.springy.som.modulith.domain.command.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "HelpDocument not found", code = "HELP_NOT_FOUND")
public class HelpNotFoundException extends NotFoundApiException {
    public HelpNotFoundException(String message) {
        super(message);
    }
}
