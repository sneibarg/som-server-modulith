package org.springy.som.modulith.domain.game.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GameRepository extends MongoRepository<GameDataDocument, String> {
    @Query("{_id: '?0'}")
    GameDataDocument findGameDataByRulesetId(String rulesetId);
}
