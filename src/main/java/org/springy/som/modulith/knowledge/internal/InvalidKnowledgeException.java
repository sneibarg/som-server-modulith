package org.springy.som.modulith.knowledge.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.BAD_REQUEST, title = "Invalid knowledge", code = "INVALID_KNOWLEDGE")
public final class InvalidKnowledgeException extends InvalidRequestApiException {
    public InvalidKnowledgeException(String message) {
        super(message);
    }
}
