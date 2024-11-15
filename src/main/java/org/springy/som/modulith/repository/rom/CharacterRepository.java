package org.springy.som.modulith.repository.rom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.character.PlayerCharacter;

import java.util.List;

public interface CharacterRepository extends MongoRepository<PlayerCharacter, String> {
    @Query("{id: '?0'}")
    PlayerCharacter findPlayerCharacterByCharacterId(String characterId);

    @Query("{accountId: '?0'}")
    List<PlayerCharacter> findAllByAccountId(String accountId);
}
