package org.springy.som.modulith.service;


import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.player.PlayerAccount;
import org.springy.som.modulith.repository.CharacterRepository;
import org.springy.som.modulith.repository.PlayerAccountRepository;

import java.util.List;

@Slf4j
@Service
public class PlayerService {
    private PlayerAccountRepository playerAccountRepository;
    private CharacterRepository characterRepository;

    public PlayerService(PlayerAccountRepository playerAccountRepository,
                         CharacterRepository characterRepository) {
        this.playerAccountRepository = playerAccountRepository;
        this.characterRepository = characterRepository;
    }

    public List<PlayerAccount> getAllPlayers() {
        return playerAccountRepository.findAll();
    }

    public void addToPlayerCharacterListByAccountName(String accountName, String playerCharacterId) {
        playerAccountRepository.findAndPopPlayerCharacterListByAccountName(accountName, playerCharacterId);
    }

    public void removePlayerCharacterListByAccountName(String accountName, String playerCharacterId) {
        playerAccountRepository.findAndPopPlayerCharacterListByAccountName(accountName, playerCharacterId);
    }

    public PlayerAccount getPlayerAccountByName(String playerName) {
        return playerAccountRepository.findPlayerByAccountName(playerName);
    }

    public PlayerAccount savePlayerAccount(PlayerAccount playerAccount) {
        return playerAccountRepository.save(playerAccount);
    }

    public void deletePlayerAccount(PlayerAccount playerAccount) {
        playerAccountRepository.delete(playerAccount);
    }
}
