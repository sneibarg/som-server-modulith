package org.springy.som.modulith.domain.character.api;

import org.springy.som.modulith.domain.character.internal.CharacterDocument;

import java.util.List;

public interface CharacterApi {
    List<CharacterDocument> getAllPlayerCharacters();
    List<CharacterDocument> getPlayerCharactersByAccountId(String accountId);
    CharacterDocument getPlayerCharacterById(String id);
    CharacterDocument createPlayerCharacter(CharacterDocument characterDocument);
    CharacterDocument savePlayerCharacterForId(String id, CharacterDocument characterDocument);
    void deletePlayerCharacterById(String id);
    long deleteAllPlayerCharacters();
}
