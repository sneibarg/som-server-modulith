package org.springy.som.modulith.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.PlayerService;
import org.springy.som.modulith.domain.player.PlayerAccount;

import java.net.URI;
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
    public ResponseEntity<List<PlayerAccount>> getPlayerAccounts() {
        return ResponseEntity.ok(playerService.getAllPlayerAccounts());
    }

    @GetMapping(path = "/name/{name}")
    public ResponseEntity<PlayerAccount> getPlayerAccountByName(@RequestParam String name) {
        return ResponseEntity.ok(playerService.getPlayerAccountByName(name));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PlayerAccount> getPlayerAccountById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(playerService.getPlayerAccountById(id));
    }

    @PostMapping
    public ResponseEntity<PlayerAccount> createPlayerAccount(@Valid @RequestBody PlayerAccount playerAccount) {
        PlayerAccount saved = playerService.createPlayerAccount(playerAccount);
        return ResponseEntity
                .created(URI.create("/api/v1/players/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerAccount> updatePlayerAccount(@PathVariable String id, @Valid @RequestBody PlayerAccount playerAccount) {
        PlayerAccount updated = playerService.savePlayerAccountForId(id, playerAccount);
        return ResponseEntity.ok(updated);
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
