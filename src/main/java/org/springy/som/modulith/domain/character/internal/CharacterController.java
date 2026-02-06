package org.springy.som.modulith.domain.character.internal;

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
import org.springy.som.modulith.web.DeleteAllResponse;
import org.springy.som.modulith.domain.character.api.CharacterMapper;
import org.springy.som.modulith.domain.character.api.CharacterView;

import java.net.URI;
import java.util.ArrayList;
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
    public ResponseEntity<List<CharacterView>> getPlayerCharacters() {
        List<CharacterView> characterViews = new ArrayList<>();
        for (CharacterDocument characterDocument : characterService.getAllPlayerCharacters())
            characterViews.add(CharacterMapper.toView(characterDocument));
        return ResponseEntity.ok(characterViews);
    }

    @GetMapping(path = "/account/{accountId}")
    @ResponseBody
    public ResponseEntity<List<CharacterView>> getPlayerCharactersByAccountId(@PathVariable String accountId) {
        List<CharacterView> characterViews = new ArrayList<>();
        for (CharacterDocument characterDocument : characterService.getPlayerCharactersByAccountId(accountId))
            characterViews.add(CharacterMapper.toView(characterDocument));
        return ResponseEntity.ok(characterViews);
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<CharacterView> getPlayerCharacterById(@PathVariable String id) {
        log.info("findPlayerCharacterByCharacterId?characterId={}",id);
        return ResponseEntity.ok(CharacterMapper.toView(characterService.getPlayerCharacterById(id)));
    }

    @PostMapping
    public ResponseEntity<CharacterView> createPlayerCharacter(@Valid @RequestBody CharacterDocument characterDocument) {
        CharacterDocument saved = characterService.createPlayerCharacter(characterDocument);
        CharacterView characterView = CharacterMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/characters/" + saved.getId()))
                .body(characterView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterView> updatePlayerCharacter(@PathVariable String id, @Valid @RequestBody CharacterDocument characterDocument) {
        CharacterDocument updated = characterService.savePlayerCharacterForId(id, characterDocument);
        CharacterView characterView = CharacterMapper.toView(updated);
        return ResponseEntity.ok(characterView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerCharacterById(@PathVariable String id) {
        characterService.deletePlayerCharacterById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(characterService.deleteAllPlayerCharacters()));
    }
}
