package org.springy.som.modulith.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.repository.CharacterRepository;

import java.util.List;

@Service
public class CharacterService {
    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public List<PlayerCharacter> getAllPlayerCharacters() {
        return characterRepository.findAll();
    }

    public List<PlayerCharacter> getPlayerCharactersByAccountId(@RequestParam String accountId) {
        return characterRepository.findAllByAccountId(accountId);
    }

    public PlayerCharacter getPlayerCharacterById(@RequestParam String id) {
        return characterRepository.findPlayerCharacterByCharacterId(id);
    }
}
