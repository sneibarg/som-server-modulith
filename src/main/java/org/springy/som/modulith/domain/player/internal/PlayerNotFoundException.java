package org.springy.som.modulith.domain.player.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "Player not found", code = "PLAYER_NOT_FOUND")
public class PlayerNotFoundException extends NotFoundApiException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
