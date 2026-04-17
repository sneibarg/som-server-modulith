package org.springy.som.modulith.domain.spell.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "SpellDocument not found", code = "SPELL_NOT_FOUND")
public class SpellNotFoundException extends NotFoundApiException {
    public SpellNotFoundException(String message) {
        super(message);
    }
}

