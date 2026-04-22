package org.springy.som.modulith.domain.player.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springy.som.modulith.domain.character.api.CharacterDeletedEvent;
import org.springy.som.modulith.domain.character.api.NewCharacterEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class PlayerCharacterListSyncListenerTest {
    private PlayerAccountRepository playerAccountRepository;
    private PlayerCharacterListSyncListener listener;

    @BeforeEach
    void setUp() {
        playerAccountRepository = mock(PlayerAccountRepository.class);
        listener = new PlayerCharacterListSyncListener(playerAccountRepository);
    }

    @Test
    void onNewCharacter_addsCharacterIdToPlayerCharacterList() {
        listener.onNewCharacter(new NewCharacterEvent("A1", "C1"));

        verify(playerAccountRepository).findAndAddToPlayerCharacterListById("A1", "C1");
        verifyNoMoreInteractions(playerAccountRepository);
    }

    @Test
    void onCharacterDeleted_removesCharacterIdFromPlayerCharacterList() {
        listener.onCharacterDeleted(new CharacterDeletedEvent("A1", "C1"));

        verify(playerAccountRepository).findAndPopPlayerCharacterListById("A1", "C1");
        verifyNoMoreInteractions(playerAccountRepository);
    }
}
