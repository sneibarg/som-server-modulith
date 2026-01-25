package org.springy.som.modulith.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.service.CharacterService;

import java.util.List;

@Slf4j
@RestController
public class CharacterController {
    private final CharacterService characterService;
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping(path = "/api/v1/characters")
    @ResponseBody
    public List<PlayerCharacter> getPlayerCharacters() {
        return characterService.getAllPlayerCharacters();
    }

    @GetMapping(path = "/api/v1/playercharacters")
    @ResponseBody
    public List<PlayerCharacter> getPlayerCharactersByAccountId(@RequestParam String accountId) {
        return characterService.getPlayerCharactersByAccountId(accountId);
    }

    @GetMapping(path = "/api/v1/character")
    @ResponseBody
    public PlayerCharacter getPlayerCharacter(@RequestParam String characterId) {
        log.info("findPlayerCharacterByCharacterId?characterId={}",characterId);
        return characterService.getPlayerCharacterById(characterId);
    }
}
