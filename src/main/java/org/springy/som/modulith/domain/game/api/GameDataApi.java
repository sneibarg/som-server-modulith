package org.springy.som.modulith.domain.game.api;

import org.springy.som.modulith.domain.game.internal.GameDataDocument;

import java.util.List;

public interface GameDataApi {
    List<GameDataDocument> findAll();
    GameDataDocument findGameDataByRulesetId(String id);

}
