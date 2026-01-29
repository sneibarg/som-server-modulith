package org.springy.som.modulith.exception;

public abstract class InvalidRequestApiException extends RuntimeException {
    protected InvalidRequestApiException(String message) { super(message); }
}
