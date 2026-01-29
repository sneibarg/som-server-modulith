package org.springy.som.modulith.exception;

public abstract class ServiceUnavailableApiException extends RuntimeException {
    protected ServiceUnavailableApiException(String message) { super(message); }
}