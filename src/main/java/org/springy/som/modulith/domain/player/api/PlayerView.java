package org.springy.som.modulith.domain.player.api;

import java.util.List;

public record PlayerView(
        String id,
        String firstName,
        String lastName,
        String accountName,
        String emailAddress,
        String password,
        List<String> playerCharacterList
) {}
