package org.springy.som.modulith.domain.game.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.command.internal.CommandDocument;

import java.util.List;

@Service
@Slf4j
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllGameDataFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<GameDataDocument> findAll() {
        return gameRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public GameDataDocument findGameDataByRulesetId(String id) {
        return gameRepository.findGameDataByRulesetId(id);
    }

    private List<CommandDocument> getAllGameDataFallback(Throwable t) {
        log.warn("Fallback getAllGameData due to {}", t.toString());
        return List.of();
    }
}
