package org.springy.som.modulith.domain.command.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "SocialDocument not found", code = "SOCIAL_NOT_FOUND")
public class SocialNotFoundException extends NotFoundApiException {
    public SocialNotFoundException(String message) {
        super(message);
    }
}
