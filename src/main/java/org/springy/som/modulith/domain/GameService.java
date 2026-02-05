package org.springy.som.modulith.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameData> findAll() {
        return gameRepository.findAll();
    }

    public GameData findGameDataByRulesetId(String id) {
        return gameRepository.findGameDataByRulesetId(id);
    }
}
