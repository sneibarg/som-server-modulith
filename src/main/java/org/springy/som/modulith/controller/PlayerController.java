package org.springy.som.modulith.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.PlayerService;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.domain.player.PlayerAccount;
import org.springy.som.modulith.service.CharacterService;
import org.springy.som.modulith.util.PlayerAccountUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class PlayerController {
    private final PlayerService playerService;
    private final CharacterService characterService;

    public PlayerController(PlayerService playerService, CharacterService characterService) {
        this.playerService = playerService;
        this.characterService = characterService;
    }

    @GetMapping(path = "/api/v1/accounts")
    @ResponseBody
    public List<PlayerAccount> getPlayerAccounts() {
        return playerService.getAllPlayers();
    }

    @GetMapping(path = "/api/v1/account")
    @ResponseBody
    public PlayerAccount getPlayerAccount(@RequestParam String accountName) {
        return playerService.getPlayerAccountByName(accountName);
    }

    @PostMapping(path = "/api/v1/create")
    @ResponseBody
    public PlayerAccount createPlayerAccount(@RequestParam String newAccount) {
        PlayerAccount playerAccount = PlayerAccountUtil.getPlayerFromJson(newAccount);
        return playerService.savePlayerAccount(playerAccount);
    }

    @DeleteMapping(path = "/api/v1/delete")
    @ResponseBody
    public PlayerAccount deletePlayerAccount(@RequestParam String accountName) {
        PlayerAccount playerAccount = playerService.getPlayerAccountByName(accountName);
        playerService.deletePlayerAccount(playerAccount);
        return playerAccount;
    }

    @PutMapping(path = "/api/v1/addplayercharacter")
    @ResponseBody
    public PlayerAccount addPlayerCharacter(@RequestParam String accountName, @RequestParam String playerCharacter) {
        playerService.addToPlayerCharacterListByAccountName(accountName, playerCharacter);
        return playerService.getPlayerAccountByName(accountName);
    }

    @DeleteMapping(path = "/api/v1/removeplayercharacter")
    @ResponseBody
    public PlayerAccount removePlayerCharacter(@RequestParam String accountName,
                                               @RequestParam String playerCharacter) {
        playerService.removePlayerCharacterListByAccountName(accountName, playerCharacter);
        return playerService.getPlayerAccountByName(accountName);
    }

    @PutMapping(path = "/api/v1/data_dump")
    @ResponseBody
    public void dataDump() {
        List<PlayerAccount> playerAccounts = playerService.getAllPlayers();
        List<PlayerCharacter> characters = characterService.getAllPlayerCharacters();
        try {
            FileWriter fileWriter = new FileWriter("data_dump.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (PlayerAccount playerAccount : playerAccounts) {
                bufferedWriter.write(playerAccount.toString());
                bufferedWriter.newLine();
            }
            for (PlayerCharacter character : characters) {
                bufferedWriter.write(character.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }
    }
}
