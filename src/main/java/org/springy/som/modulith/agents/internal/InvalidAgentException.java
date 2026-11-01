package org.springy.som.modulith.agents.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid agent", code = "INVALID_AGENT")
public final class InvalidAgentException extends InvalidRequestApiException {
    public InvalidAgentException(String message) {
        super(message);
    }
}
