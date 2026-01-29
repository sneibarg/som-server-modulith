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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.service.CharacterService;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/characters", produces = "application/json")
public class CharacterController {
    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    @ResponseBody
    public List<PlayerCharacter> getPlayerCharacters() {
        return characterService.getAllPlayerCharacters();
    }

    @GetMapping(path = "/account/{accountId}")
    @ResponseBody
    public ResponseEntity<List<PlayerCharacter>> getPlayerCharactersByAccountId(@PathVariable String accountId) {
        return ResponseEntity.ok(characterService.getPlayerCharactersByAccountId(accountId));
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<PlayerCharacter> getPlayerCharacterById(@PathVariable String id) {
        log.info("findPlayerCharacterByCharacterId?characterId={}",id);
        return ResponseEntity.ok(characterService.getPlayerCharacterById(id));
    }

    @PostMapping
    public ResponseEntity<PlayerCharacter> createPlayerCharacter(@Valid @RequestBody PlayerCharacter playerCharacter) {
        PlayerCharacter saved = characterService.savePlayerCharacter(playerCharacter);
        return ResponseEntity
                .created(URI.create("/api/v1/characters/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerCharacter> updatePlayerCharacter(@PathVariable String id, @Valid @RequestBody PlayerCharacter playerCharacter) {
        PlayerCharacter updated = characterService.savePlayerCharacterForId(id, playerCharacter);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerCharacterById(@PathVariable String id) {
        characterService.deletePlayerCharacterById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<AreaController.DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new AreaController.DeleteAllResponse(characterService.deleteAllPlayerCharacters()));
    }
}
