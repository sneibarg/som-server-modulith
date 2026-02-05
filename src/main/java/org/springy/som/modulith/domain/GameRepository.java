package org.springy.som.modulith.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GameRepository extends MongoRepository<GameData, String> {
    @Query("{_id: '?0'}")
    GameData findGameDataByRulesetId(String rulesetId);
}
