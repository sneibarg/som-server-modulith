package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springy.som.modulith.domain.player.PlayerAccount;

public interface PlayerAccountRepository extends MongoRepository<PlayerAccount, String> {
    @Query("{accountName: '?0'}")
    PlayerAccount findPlayerAccountByName(String accountName);

    @Query("{id: '?0'}")
    PlayerAccount findPlayerAccountById(String id);

    @Update("{ '$addToSet' : { 'playerCharacterList' : ?1 } }")
    void findAndAddToPlayerCharacterListByAccountName(String accountName, String playerCharacterList);

    @Update("{ '$pull' : { 'playerCharacterList' : ?1 } }")
    void findAndPopPlayerCharacterListByAccountName(String accountName, String playerCharacterList);

    long count();
}
