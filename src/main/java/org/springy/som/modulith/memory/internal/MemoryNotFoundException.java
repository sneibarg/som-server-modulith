package org.springy.som.modulith.memory.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "Memory not found", code = "MEMORY_NOT_FOUND")
public class MemoryNotFoundException extends NotFoundApiException {
    public MemoryNotFoundException(String message) {
        super(message);
    }
}
