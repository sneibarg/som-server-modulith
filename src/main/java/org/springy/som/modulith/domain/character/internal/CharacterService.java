package org.springy.som.modulith.domain.character.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.playerCharacterIdMissing;
import static org.springy.som.modulith.util.DomainGuards.playerCharacterMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;

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
    public List<CharacterDocument> getAllPlayerCharacters() {
        try {
            return characterRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAllPlayerCharacters", ex);
            throw new PlayerCharacterPersistenceException("Failed to load player characters "+ ex);
        }

    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getPlayerCharacterByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<CharacterDocument> getPlayerCharactersByAccountId(@RequestParam String id) {
        requireText(id, playerCharacterIdMissing());

        try {
            List<CharacterDocument> characterDocument = characterRepository.findAllByAccountId(id);
            if (characterDocument == null) {
                throw new PlayerCharacterNotFoundException(id);
            }
            return characterDocument;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getPlayerCharacterById", ex);
            throw new PlayerCharacterPersistenceException("Failed to load player character "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getPlayerCharacterByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public CharacterDocument getPlayerCharacterById(@RequestParam String id) {
        requireText(id, playerCharacterIdMissing());

        try {
            CharacterDocument characterDocument = characterRepository.findPlayerCharacterByCharacterId(id);
            if (characterDocument == null) {
                throw new PlayerCharacterNotFoundException(id);
            }
            return characterDocument;
        } catch (PlayerCharacterNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getPlayerCharacterById", ex);
            throw new PlayerCharacterPersistenceException("Failed to load player character "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public CharacterDocument savePlayerCharacter(CharacterDocument characterDocument) {
        requireEntityWithId(characterDocument,
                CharacterDocument::getId, playerCharacterMissing(), playerCharacterIdMissing());

        return characterRepository.save(characterDocument);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public CharacterDocument savePlayerCharacterForId(String id, CharacterDocument characterDocument) {
        requireText(id, playerCharacterIdMissing());
        requireEntityWithId(characterDocument,
                CharacterDocument::getId, playerCharacterMissing(), playerCharacterIdMissing());

        return characterRepository.save(getPlayerCharacterById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deletePlayerCharacterById(String id) {
        requireText(id, playerCharacterIdMissing());

        try {
            if (!characterRepository.existsById(id)) {
                throw new PlayerCharacterNotFoundException(id);
            }
            characterRepository.deleteById(id);
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

    private List<CharacterDocument> getAllPlayerCharactersFallback(Throwable t) {
        log.warn("Fallback getAllPlayerCharacters due to {}", t.toString());
        return List.of();
    }

    private CharacterDocument getPlayerCharacterByIdFallback(String id, Throwable t) {
        log.warn("Fallback getPlayerCharacterById id={} due to {}", id, t.toString());
        throw new PlayerCharacterPersistenceException("CharacterDocument lookup temporarily unavailable: " + id + " " + t);
    }

    private CharacterDocument getPlayerCharactersByAccountIdFallback(String accountId, Throwable t) {
        log.warn("Fallback getPlayerCharactersByAccountId due to {}", t.toString());
        throw new PlayerCharacterPersistenceException("CharacterDocument lookup temporarily unavailable: " + accountId + " " + t);
    }
}
