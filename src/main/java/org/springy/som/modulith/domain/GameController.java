package org.springy.som.modulith.domain;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/game", produces = "application/json")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameData>> findAll() {
        return ResponseEntity.ok(gameService.findAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<GameData> findGameDataById(@PathVariable String id) {
        return ResponseEntity.ok(gameService.findGameDataByRulesetId(id));
    }
}
