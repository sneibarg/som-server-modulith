package org.springy.som.modulith.domain.game.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.game.api.GameDataMapper;
import org.springy.som.modulith.domain.game.api.GameDataView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/game", produces = "application/json")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDataView>> findAll() {
        List<GameDataView> gameDataViews = new ArrayList<>();
        for (GameDataDocument gameDataDocument : gameService.findAll())
            gameDataViews.add(GameDataMapper.toView(gameDataDocument));
        return ResponseEntity.ok(gameDataViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<GameDataView> findGameDataById(@PathVariable String id) {
        return ResponseEntity.ok(GameDataMapper.toView(gameService.findGameDataByRulesetId(id)));
    }
}
