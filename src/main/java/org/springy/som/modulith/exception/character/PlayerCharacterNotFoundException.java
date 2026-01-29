package org.springy.som.modulith.exception.character;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "Player character not found", code = "PC_NOT_FOUND")
public class PlayerCharacterNotFoundException extends NotFoundApiException {
    public PlayerCharacterNotFoundException(String message) {
        super(message);
    }
}
