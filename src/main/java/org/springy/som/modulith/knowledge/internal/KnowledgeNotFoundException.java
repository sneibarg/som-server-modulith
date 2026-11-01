package org.springy.som.modulith.knowledge.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "Knowledge not found", code = "KNOWLEDGE_NOT_FOUND")
public class KnowledgeNotFoundException extends NotFoundApiException {
    public KnowledgeNotFoundException(String message) {
        super(message);
    }
}
