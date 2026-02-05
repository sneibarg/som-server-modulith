package org.springy.som.modulith.domain.character.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CharacterRepository extends MongoRepository<CharacterDocument, String> {
    @Query("{id: '?0'}")
    CharacterDocument findPlayerCharacterByCharacterId(String characterId);

    @Query("{accountId: '?0'}")
    List<CharacterDocument> findAllByAccountId(String accountId);
}
