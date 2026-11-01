package org.springy.som.modulith.prompt.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "Prompt not found", code = "PROMPT_NOT_FOUND")
public class PromptNotFoundException extends NotFoundApiException {
    public PromptNotFoundException(String message) {
        super(message);
    }
}
