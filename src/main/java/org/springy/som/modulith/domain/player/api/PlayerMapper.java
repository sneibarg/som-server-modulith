package org.springy.som.modulith.domain.player.api;

import org.springy.som.modulith.domain.player.internal.PlayerDocument;

public final class PlayerMapper {

    private PlayerMapper() {}

    public static PlayerView toView(PlayerDocument doc) {
        return new PlayerView(
                doc.getId(),
                doc.getFirstName(),
                doc.getLastName(),
                doc.getAccountName(),
                doc.getEmailAddress(),
                doc.getPassword(),
                doc.getPlayerCharacterList()
        );
    }
}
