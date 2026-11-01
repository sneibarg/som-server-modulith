package org.springy.som.modulith.agents.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.ServiceUnavailableApiException;

@ApiError(status = HttpStatus.SERVICE_UNAVAILABLE, title = "Persistence service unavailable", code = "SERVICE_UNAVAILABLE")
public final class AgentPersistenceException extends ServiceUnavailableApiException {
    public AgentPersistenceException(String message) {
        super(message);
    }
}
