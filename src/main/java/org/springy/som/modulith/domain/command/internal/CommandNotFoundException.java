package org.springy.som.modulith.domain.command.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "CommandDocument not found", code = "COMMAND_NOT_FOUND")
public class CommandNotFoundException extends NotFoundApiException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}
