package org.springy.som.modulith.domain.player.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface PlayerAccountRepository extends MongoRepository<PlayerDocument, String> {
    @Query("{accountName: '?0'}")
    PlayerDocument findPlayerAccountByName(String accountName);

    @Query("{id: '?0'}")
    PlayerDocument findPlayerAccountById(String id);

    @Update("{ '$addToSet' : { 'playerCharacterList' : ?1 } }")
    void findAndAddToPlayerCharacterListByAccountName(String accountName, String playerCharacterList);

    @Update("{ '$pull' : { 'playerCharacterList' : ?1 } }")
    void findAndPopPlayerCharacterListByAccountName(String accountName, String playerCharacterList);

    long count();
}
