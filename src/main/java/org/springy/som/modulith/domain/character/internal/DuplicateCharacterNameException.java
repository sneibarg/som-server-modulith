package org.springy.som.modulith.domain.character.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.InvalidRequestApiException;

@ApiError(status = HttpStatus.CONFLICT, title = "Duplicate character name", code = "DUPLICATE_CHARACTER_NAME")
public class DuplicateCharacterNameException extends InvalidRequestApiException {
    public DuplicateCharacterNameException(String name) {
        super("A character with the name '" + name + "' already exists");
    }
}
