package org.springy.som.modulith.domain.player.internal;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springy.som.modulith.domain.character.api.CharacterDeletedEvent;
import org.springy.som.modulith.domain.character.api.NewCharacterEvent;

@Component
public class PlayerCharacterListSyncListener {
    private final PlayerAccountRepository playerAccountRepository;

    PlayerCharacterListSyncListener(PlayerAccountRepository playerAccountRepository) {
        this.playerAccountRepository = playerAccountRepository;
    }

    @Async
    @EventListener
    void onNewCharacter(NewCharacterEvent event) {
        playerAccountRepository.findAndAddToPlayerCharacterListById(event.accountId(), event.characterId());
    }

    @Async
    @EventListener
    void onCharacterDeleted(CharacterDeletedEvent event) {
        playerAccountRepository.findAndPopPlayerCharacterListById(event.accountId(), event.characterId());
    }
}
