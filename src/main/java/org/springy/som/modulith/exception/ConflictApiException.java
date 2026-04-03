package org.springy.som.modulith.exception;

public abstract class ConflictApiException extends RuntimeException {
    protected ConflictApiException(String message) { super(message); }
}
