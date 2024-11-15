package org.springy.som.modulith.service.rom;


import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import org.springy.som.modulith.domain.rom.character.PlayerCharacter;
import org.springy.som.modulith.domain.rom.player.PlayerAccount;
import org.springy.som.modulith.repository.rom.CharacterRepository;
import org.springy.som.modulith.repository.rom.PlayerAccountRepository;
import org.springy.som.modulith.util.PlayerAccountUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class PlayerService {
    private PlayerAccountRepository playerAccountRepository;
    private CharacterRepository characterRepository;

    public PlayerService(PlayerAccountRepository playerAccountRepository,
                         CharacterRepository characterRepository) {
        this.playerAccountRepository = playerAccountRepository;
        this.characterRepository = characterRepository;
    }

    @GetMapping(path = "/api/v1/accounts")
    @ResponseBody
    public List<PlayerAccount> getPlayerAccounts() {
        return playerAccountRepository.findAll();
    }

    @GetMapping(path = "/api/v1/account")
    @ResponseBody
    public PlayerAccount getPlayerAccount(@RequestParam String accountName) {
        return playerAccountRepository.findPlayerByAccountName(accountName);
    }

    @GetMapping(path = "/api/v1/characters")
    @ResponseBody
    public List<PlayerCharacter> getPlayerCharacters() {
        return characterRepository.findAll();
    }

    @GetMapping(path = "/api/v1/playercharacters")
    @ResponseBody
    public List<PlayerCharacter> getPlayerCharactersByAccountId(@RequestParam String accountId) {
        List<PlayerCharacter> playerCharacters = characterRepository.findAllByAccountId(accountId);
        return playerCharacters;
    }

    @GetMapping(path = "/api/v1/character")
    @ResponseBody
    public PlayerCharacter getPlayerCharacter(@RequestParam String characterId) {
        log.info("findPlayerCharacterByCharacterId?characterId={}",characterId);
        PlayerCharacter playerCharacter = characterRepository.findPlayerCharacterByCharacterId(characterId);
        return playerCharacter;
    }

    @PostMapping(path = "/api/v1/create")
    @ResponseBody
    public PlayerAccount createPlayerAccount(@RequestParam String newAccount) {
        PlayerAccount playerAccount = PlayerAccountUtil.getPlayerFromJson(newAccount);
        return playerAccountRepository.save(playerAccount);
    }

    @DeleteMapping(path = "/api/v1/delete")
    @ResponseBody
    public PlayerAccount deletePlayerAccount(@RequestParam String accountName) {
        PlayerAccount playerAccount = playerAccountRepository.findPlayerByAccountName(accountName);
        playerAccountRepository.delete(playerAccount);
        return playerAccount;
    }

    @PutMapping(path = "/api/v1/addplayercharacter")
    @ResponseBody
    public PlayerAccount addPlayerCharacter(@RequestParam String accountName,
                                             @RequestParam String playerCharacter) {
        playerAccountRepository.findAndAddToPlayerCharacterListByAccountName(accountName, playerCharacter);
        PlayerAccount playerAccount = playerAccountRepository.findPlayerByAccountName(accountName);
        return playerAccount;
    }

    @DeleteMapping(path = "/api/v1/removeplayercharacter")
    @ResponseBody
    public PlayerAccount removePlayerCharacter(@RequestParam String accountName,
                                             @RequestParam String playerCharacter) {
        playerAccountRepository.findAndPopPlayerCharacterListByAccountName(accountName, playerCharacter);
        PlayerAccount playerAccount = playerAccountRepository.findPlayerByAccountName(accountName);
        return playerAccount;
    }

    @PutMapping(path = "/api/v1/data_dump")
    @ResponseBody
    public void dataDump() {
        List<PlayerAccount> playerAccounts = playerAccountRepository.findAll();
        List<PlayerCharacter> playerCharacters = characterRepository.findAll();
        try {
            FileWriter fileWriter = new FileWriter("data_dump.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (PlayerAccount playerAccount : playerAccounts) {
                bufferedWriter.write(playerAccount.toString());
                bufferedWriter.newLine();
            }
            for (PlayerCharacter playerCharacter : playerCharacters) {
                bufferedWriter.write(playerCharacter.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }
    }
}
