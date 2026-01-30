package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.exception.character.InvalidPlayerCharacterException;
import org.springy.som.modulith.exception.character.PlayerCharacterNotFoundException;
import org.springy.som.modulith.exception.character.PlayerCharacterPersistenceException;
import org.springy.som.modulith.repository.CharacterRepository;

import java.util.List;

@Service
@Slf4j
public class CharacterService {
    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllPlayerCharactersFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<PlayerCharacter> getAllPlayerCharacters() {
        try {
            return characterRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAllPlayerCharacters", ex);
            throw new PlayerCharacterPersistenceException("Failed to load player characters "+ ex);
        }

    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getPlayerCharactersByAccountIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<PlayerCharacter> getPlayerCharactersByAccountId(@RequestParam String accountId) {
        requireAccountId(accountId);
        return characterRepository.findAllByAccountId(accountId);
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getPlayerCharacterByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerCharacter getPlayerCharacterById(@RequestParam String id) {
        requireId(id);
        try {
            PlayerCharacter playerCharacter = characterRepository.findPlayerCharacterByCharacterId(id);
            if (playerCharacter == null) {
                throw new PlayerCharacterNotFoundException(id);
            }
            return playerCharacter;
        } catch (PlayerCharacterNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getPlayerCharacterById", ex);
            throw new PlayerCharacterPersistenceException("Failed to load player character "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerCharacter savePlayerCharacter(PlayerCharacter playerCharacter) {
        requirePlayerCharacter(playerCharacter);

        return characterRepository.save(playerCharacter);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerCharacter savePlayerCharacterForId(String id, PlayerCharacter playerCharacter) {
        requireId(id);
        requirePlayerCharacter(playerCharacter);

        return characterRepository.save(getPlayerCharacterById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deletePlayerCharacterById(String id) {
        requireId(id);

        try {
            if (!characterRepository.existsById(id)) {
                throw new PlayerCharacterNotFoundException(id);
            }
            characterRepository.deleteById(id);
        } catch (PlayerCharacterNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deletePlayerCharacterById id={}", id, ex);
            throw new PlayerCharacterPersistenceException("Failed to delete player character: " + id + " " + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllPlayerCharacters() {
        try {
            long itemCount = characterRepository.count();
            characterRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllAreas", ex);
            throw new PlayerCharacterPersistenceException("Failed to delete all player characters "+ ex);
        }
    }


    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidPlayerCharacterException("PlayerCharacter id must be provided");
        }
    }

    private static void requireAccountId(String accountId) {
        if (!StringUtils.hasText(accountId)) {

        }
    }

    private static void requirePlayerCharacter(PlayerCharacter playerCharacter) {
        if (playerCharacter == null) {
            throw new InvalidPlayerCharacterException("PlayerCharacter must be provided");
        }

        requireId(playerCharacter.getId());
    }

    private static String safeId(PlayerCharacter playerCharacter) {
        try {
            return playerCharacter.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<PlayerCharacter> getAllPlayerCharactersFallback(Throwable t) {
        log.warn("Fallback getAllPlayerCharacters due to {}", t.toString());
        return List.of();
    }

    private PlayerCharacter getPlayerCharacterByIdFallback(String id, Throwable t) {
        log.warn("Fallback getPlayerCharacterById id={} due to {}", id, t.toString());
        throw new PlayerCharacterPersistenceException("PlayerCharacter lookup temporarily unavailable: " + id + " " + t);
    }

    private PlayerCharacter getPlayerCharactersByAccountIdFallback(String accountId, Throwable t) {
        log.warn("Fallback getPlayerCharactersByAccountId due to {}", t.toString());
        throw new PlayerCharacterPersistenceException("PlayerCharacter lookup temporarily unavailable: " + accountId + " " + t);
    }
}
