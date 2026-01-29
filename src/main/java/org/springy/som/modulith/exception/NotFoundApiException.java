package org.springy.som.modulith.exception;

public abstract class NotFoundApiException extends RuntimeException {
    protected NotFoundApiException(String message) { super(message); }
}
