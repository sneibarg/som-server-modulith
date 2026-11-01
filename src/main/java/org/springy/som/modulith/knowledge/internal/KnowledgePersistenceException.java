package org.springy.som.modulith.knowledge.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.ServiceUnavailableApiException;

@ApiError(status = HttpStatus.SERVICE_UNAVAILABLE, title = "Persistence service unavailable", code = "SERVICE_UNAVAILABLE")
public final class KnowledgePersistenceException extends ServiceUnavailableApiException {
    public KnowledgePersistenceException(String message) {
        super(message);
    }
}
