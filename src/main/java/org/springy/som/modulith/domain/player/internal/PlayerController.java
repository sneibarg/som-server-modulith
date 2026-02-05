package org.springy.som.modulith.domain.player.internal;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.DeleteAllResponse;
import org.springy.som.modulith.domain.player.api.PlayerMapper;
import org.springy.som.modulith.domain.player.api.PlayerView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/players", produces = "application/json")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerView>> getPlayerAccounts() {
        List<PlayerView> playerViews = new ArrayList<>();
        for (PlayerDocument playerDocument : playerService.getAllPlayerAccounts())
            playerViews.add(PlayerMapper.toView(playerDocument));
        return ResponseEntity.ok(playerViews);
    }

    @GetMapping(path = "/name/{name}")
    public ResponseEntity<PlayerView> getPlayerAccountByName(@PathVariable String name) {
        return ResponseEntity.ok(PlayerMapper.toView(playerService.getPlayerAccountByName(name)));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PlayerView> getPlayerAccountById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(PlayerMapper.toView(playerService.getPlayerAccountById(id)));
    }

    @PostMapping
    public ResponseEntity<PlayerView> createPlayerAccount(@Valid @RequestBody PlayerDocument playerDocument) {
        PlayerDocument saved = playerService.createPlayerAccount(playerDocument);
        PlayerView playerView = PlayerMapper.toView(saved);

        return ResponseEntity
                .created(URI.create("/api/v1/players/" + saved.getId()))
                .body(playerView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerView> updatePlayerAccount(@PathVariable String id, @Valid @RequestBody PlayerDocument playerDocument) {
        PlayerDocument updated = playerService.savePlayerAccountForId(id, playerDocument);
        PlayerView playerView = PlayerMapper.toView(updated);
        return ResponseEntity.ok(playerView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerAccountById(@PathVariable String id) {
        playerService.deletePlayerAccountById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(playerService.deleteAllPlayerAccounts()));
    }
}
